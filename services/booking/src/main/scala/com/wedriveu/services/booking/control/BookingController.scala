package com.wedriveu.services.booking.control

import java.util.{Calendar, Date}

import com.wedriveu.services.booking.entity.{BookingStore, BookingStoreImpl}
import com.wedriveu.services.booking.util.{Constants, Dates}
import com.wedriveu.services.shared.model.Booking
import com.wedriveu.services.shared.store.{EntityListStoreStrategy, JsonFileEntityListStoreStrategyImpl}
import com.wedriveu.services.shared.vertx.VertxJsonMapper
import com.wedriveu.shared.rabbitmq.message.{BookVehicleResponse, CreateBookingRequest, CreateBookingResponse, VehicleReservationRequest}
import com.wedriveu.shared.util.Log
import io.vertx.core.json.JsonObject
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.eventbus.Message


/**
  * @author Nicola Lasagni on 18/08/2017.
  */
trait BookingController {

  /** Creates a booking and books a vehicle.
    *
    * @param bookingRequest The request data needed to create a [[com.wedriveu.services.shared.model.Booking]]
    *                       and to book the chosen vehicle.
    */
  def createBooking(bookingRequest: CreateBookingRequest): Unit

  /** Changes an active [[com.wedriveu.services.shared.model.Booking]] license plate.
    *
    * @param oldLicensePlate The license plate with which identify the booking to change.
    * @param newLicensePlate The new license plate.
    */
  def changeActiveBookingLicensePlate(oldLicensePlate: String, newLicensePlate: String): Unit

  /** Completes a [[com.wedriveu.services.shared.model.Booking]].
    *
    * @param licensePlate The license plate with which identify the booking to complete.
    */
  def completeBooking(licensePlate: String): Unit

  /** Gets the positions of a [[com.wedriveu.services.shared.model.Booking]].
    *
    * @param licensePlate The license plate with which identify the booking from which get the positions.
    */
  def findActiveBookingPositions(licensePlate: String): Unit

}

/** Represents a [[BookingController]] which implementation will be bounded to a [[ScalaVerticle]].
  *
  */
trait BookingControllerVerticle extends ScalaVerticle with BookingController {}

object BookingControllerVerticle {

  /**
    * The [[ScalaVerticle]] name to deploy a [[BookingControllerVerticle]].
    */
  val Verticle: String = s"scala:${classOf[BookingControllerVerticleImpl].getName}"

  private[this] class BookingControllerVerticleImpl extends BookingControllerVerticle {

    private val InvalidOperationMessage = "Invalid message received"
    private val BookingAlreadyStarted = "A booking process has been already started for this user."
    private val VehicleAlreadyBooked = "This vehicle has already been booked."
    private val UnableToCreateBooking = "An error occurred while creating the booking."

    private val fileName: String = "bookings.json"
    private val storeStrategy: EntityListStoreStrategy[Booking] =
      new JsonFileEntityListStoreStrategyImpl[Booking](classOf[Booking], fileName)
    private val store: BookingStore = new BookingStoreImpl(storeStrategy)

    override def start(): Unit = {
      registerForRequests()
    }

    private def registerForRequests(): Unit = {
      registerConsumer(
        Constants.EventBus.Address.Booking.CreateBookingRequest,
        classOf[CreateBookingRequest],
        (request: CreateBookingRequest) => {
          createBooking(request)
      })
      registerConsumer(Constants.EventBus.Address.Vehicle.BookVehicleResponse,
        classOf[BookVehicleResponse],
        (request: BookVehicleResponse) => {
          bindVehicleToBooking(request)
      })
    }

    private def registerConsumer[T](address: String, clazz: Class[T], operation: T => Unit): Unit = {
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.CreateBookingRequest,
        (msg: Message[Object]) => {
          handleControllerOperationMessage(msg.body(), clazz, operation)
        })
    }

    private def handleControllerOperationMessage[T](
      message: Object,
      clazz: Class[T],
      operation: T => Unit
    ) = message match {
      case msg: JsonObject =>
        operation(VertxJsonMapper.mapFromBodyTo(msg, clazz))
      case _ =>
        Log.error(this.getClass.getSimpleName,
          new IllegalArgumentException(InvalidOperationMessage + message.toString))
    }

    private def sendMessage(address: String, id: String, message: Object): Unit = {
      val jsonObject = VertxJsonMapper.mapInBodyFrom(message)
      jsonObject.put(Constants.EventBus.Message.SpecificRoutingKey, id)
      vertx.eventBus().send(address, jsonObject)
    }

    private def sendMessage(address: String, message: Object): Unit = {
      sendMessage(address, null, message)
    }

    private def sendErrorResponse(address: String, id: String, error: String): Unit = {
      val bookingResponse = new CreateBookingResponse()
      bookingResponse.setSuccess(false)
      bookingResponse.setErrorMessage(error)
      sendMessage(address, id, bookingResponse)
    }

    override def createBooking(request: CreateBookingRequest): Unit = {
      val startedBooking = store.getUserStartedBooking(request.getUsername)
      if (startedBooking.isPresent) {
        sendErrorResponse(
          Constants.EventBus.Address.Booking.CreateBookingResponse,
          request.getUsername,
          BookingAlreadyStarted)
      } else {
        val id = store.generateId()
        val result = store.addBooking(new Booking(
            id,
            new Date(),
            request.getUsername,
            request.getLicensePlate,
            request.getUserPosition,
            request.getDestinationPosition,
            Booking.STATUS_STARTED
          )
        )
        if (result) {
          val vehicleRequest = new VehicleReservationRequest
          vehicleRequest.setUsername(request.getUsername)
          sendMessage(Constants.EventBus.Address.Vehicle.BookVehicleRequest, vehicleRequest)
        } else {
          sendErrorResponse(Constants.EventBus.Address.Booking.CreateBookingResponse,
            request.getUsername,
            UnableToCreateBooking)
        }
      }
    }

    private def bindVehicleToBooking(response: BookVehicleResponse): Unit = {
      val booking = store.getStartedBookingByLicensePlate(response.getLicencePlate).get()
      if (response.getBooked) {
        store.updateBookingStatus(booking.getId, Booking.STATUS_PROCESSING)
        val arriveAtUserCalendar = Calendar.getInstance()
        val arriveAtDestinationCalendar = Calendar.getInstance()
        arriveAtUserCalendar.setTimeInMillis(booking.getDate.getTime + response.getDriveTimeToUser)
        arriveAtDestinationCalendar.setTimeInMillis(booking.getDate.getTime + response.getDriveTimeToDestination)
        val bookingResponse = new CreateBookingResponse
        bookingResponse.setSuccess(true)
        bookingResponse.setLicencePlate(response.getLicencePlate)
        bookingResponse.setDriveTimeToUser(Dates.format(arriveAtUserCalendar.getTime))
        bookingResponse.setDriveTimeToDestination(Dates.format(arriveAtDestinationCalendar.getTime))
        sendMessage(Constants.EventBus.Address.Booking.CreateBookingResponse, booking.getUsername, bookingResponse)
      } else {
        sendErrorResponse(
          Constants.EventBus.Address.Booking.CreateBookingResponse,
          booking.getUsername,
          VehicleAlreadyBooked)
        store.deleteBooking(booking.getId)
      }
    }

    override def changeActiveBookingLicensePlate(oldLicensePlate: String, newLicensePlate: String): Unit = ???

    override def completeBooking(licensePlate: String): Unit = ???

    override def findActiveBookingPositions(licensePlate: String): Unit = ???

  }

}

