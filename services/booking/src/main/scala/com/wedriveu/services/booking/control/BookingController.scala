package com.wedriveu.services.booking.control

import java.util.Date

import com.wedriveu.services.booking.entity.{BookingStore, BookingStoreImpl}
import com.wedriveu.services.booking.util.Constants
import com.wedriveu.services.shared.model.Booking
import com.wedriveu.services.shared.store.{EntityListStoreStrategy, JsonFileEntityListStoreStrategyImpl}
import com.wedriveu.services.shared.vertx.VertxJsonMapper
import com.wedriveu.shared.rabbitmq.message._
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

  /** Changes the license plate of a [[com.wedriveu.services.shared.model.Booking]] that is in status
    * [[Booking.STATUS_PROCESSING]].
    *
    * @param changeRequest The request data needed to change an active [[com.wedriveu.services.shared.model.Booking]]
    *                      license plate.
    */
  def changeProcessingBookingLicensePlate(changeRequest: ChangeBookingRequest): Unit

  /** Completes a [[com.wedriveu.services.shared.model.Booking]].
    *
    * @param completeRequest The request data needed to complete a [[com.wedriveu.services.shared.model.Booking]].
    */
  def completeBooking(completeRequest: CompleteBookingRequest): Unit

  /** Gets the positions of a [[com.wedriveu.services.shared.model.Booking]] that is in status
    * [[Booking.STATUS_PROCESSING]].
    *
    * @param findRequest The request data needed to find a
    *                    [[com.wedriveu.services.shared.model.Booking]] positions.
    */
  def findProcessingBookingPositions(findRequest: FindBookingPositionsRequest): Unit

  /** Gets all the [[com.wedriveu.services.shared.model.Booking]]s stored.
    *
    * @param getRequest The request data needed to find all the [[com.wedriveu.services.shared.model.Booking]]s.
    */
  def findAllBookings(getRequest: BookingListRequest): Unit

  /** Aborts a [[com.wedriveu.services.shared.model.Booking]].
    *
    * @param abortRequest The request data needed to abort a [[com.wedriveu.services.shared.model.Booking]].
    */
  def abortBooking(abortRequest: AbortBookingRequest): Unit

}

/** Represents a [[BookingController]] which implementation will be bounded to a [[ScalaVerticle]].
  *
  */
trait BookingControllerVerticle extends ScalaVerticle with BookingController

object BookingControllerVerticle {

  /**
    * The [[ScalaVerticle]] name to deploy a [[BookingControllerVerticle]].
    */
  val Verticle: String = s"scala:${classOf[BookingControllerVerticleImpl].getName}"

  private[this] class BookingControllerVerticleImpl extends BookingControllerVerticle {

    private val Timer = 20000
    private val InvalidOperationMessage = "Invalid message received"
    private val BookingAlreadyStarted = "A booking process has not yet been completed for this user."
    private val VehicleAlreadyBooked = "This vehicle has already been booked."
    private val UnableToCreateBooking = "An error occurred while creating the booking."
    private var timerIds = Map.empty[String, Long]

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
      registerConsumer(Constants.EventBus.Address.Booking.ChangeBookingLicensePlateRequest,
        classOf[ChangeBookingRequest],
        (request: ChangeBookingRequest) => {
          changeProcessingBookingLicensePlate(request)
        })
      registerConsumer(Constants.EventBus.Address.Booking.CompleteBookingRequest,
        classOf[CompleteBookingRequest],
        (request: CompleteBookingRequest) => {
          completeBooking(request)
        })
      registerConsumer(Constants.EventBus.Address.Booking.FindBookingPositionRequest,
        classOf[FindBookingPositionsRequest],
        (request: FindBookingPositionsRequest) => {
          findProcessingBookingPositions(request)
        })
      registerConsumer(Constants.EventBus.Address.Booking.GetBookingsRequest,
        classOf[BookingListRequest],
        (request: BookingListRequest) => {
          findAllBookings(request)
        })
      registerConsumer(Constants.EventBus.Address.Booking.AbortBookingRequest,
        classOf[AbortBookingRequest],
        (request: AbortBookingRequest) => {
          abortBooking(request)
        })
    }

    private def registerConsumer[T](address: String, clazz: Class[T], operation: T => Unit): Unit = {
      vertx.eventBus().consumer(address,
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

    private def sendJsonObject(address: String, message: JsonObject) = {
      vertx.eventBus().send(address, message)
    }

    private def sendMessage(address: String, id: String, message: Object): Unit = {
      val jsonObject = VertxJsonMapper.mapInBodyFrom(message)
      jsonObject.put(Constants.EventBus.Message.SpecificRoutingKey, id)
      sendJsonObject(address, jsonObject)
    }

    private def sendMessage(address: String, message: Object): Unit = {
      sendMessage(address, null, message)
    }

    private def sendListInMessage[T](address: String, id: String, message: java.util.List[T]): Unit = {
      val jsonObject = VertxJsonMapper.mapListInBodyFrom(message)
      jsonObject.put(Constants.EventBus.Message.SpecificRoutingKey, id)
      sendJsonObject(address, jsonObject)
    }

    private def sendCreateBookingErrorResponse(address: String, id: String, error: String): Unit = {
      val bookingResponse = new CreateBookingResponse()
      bookingResponse.setSuccess(false)
      bookingResponse.setErrorMessage(error)
      sendMessage(address, id, bookingResponse)
    }

    override def createBooking(request: CreateBookingRequest): Unit = {
      val startedBooking = store.getBookingByUser(request.getUsername, Booking.STATUS_STARTED)
      val processingBooking = store.getBookingByUser(request.getUsername, Booking.STATUS_PROCESSING)
      if (startedBooking.isPresent || processingBooking.isPresent) {
        sendCreateBookingErrorResponse(
          Constants.EventBus.Address.Booking.CreateBookingResponse,
          request.getUsername,
          BookingAlreadyStarted)
      } else {
        val id = store.generateId()
        val result = store.addBooking(
          new Booking(
            id,
            new Date(),
            request.getUsername,
            request.getLicensePlate,
            request.getUserPosition,
            request.getDestinationPosition,
            Booking.STATUS_STARTED)
        )
        if (result) {
          val bookVehicleRequest = new BookVehicleRequest
          bookVehicleRequest.setUsername(request.getUsername)
          bookVehicleRequest.setLicencePlate(request.getLicensePlate)
          bookVehicleRequest.setUserPosition(request.getUserPosition)
          bookVehicleRequest.setDestinationPosition(request.getDestinationPosition)
          sendMessage(Constants.EventBus.Address.Vehicle.BookVehicleRequest, bookVehicleRequest)

          timerIds = timerIds + (request.getUsername -> vertx.setTimer(Timer, _ => {
            store.deleteBooking(id)
            sendCreateBookingErrorResponse(Constants.EventBus.Address.Booking.CreateBookingResponse,
              request.getUsername,
              UnableToCreateBooking)
          }))
        } else {
          sendCreateBookingErrorResponse(Constants.EventBus.Address.Booking.CreateBookingResponse,
            request.getUsername,
            UnableToCreateBooking)
        }
      }
    }

    private def bindVehicleToBooking(response: BookVehicleResponse): Unit = {
      val optBooking = store.getStartedBookingByLicensePlate(response.getLicensePlate)
      if (optBooking.isPresent) {
        val booking = optBooking.get()
        timerIds.filter(map => map._1.equals(booking.getUsername)).foreach(map => vertx.cancelTimer(map._2))
        if (response.getBooked) {
          store.updateBookingStatus(booking.getId, Booking.STATUS_PROCESSING)
          val bookingResponse = new CreateBookingResponse
          bookingResponse.setSuccess(true)
          bookingResponse.setLicencePlate(response.getLicensePlate)
          bookingResponse.setUserArrivalTime(booking.getDate.getTime + response.getDriveTimeToUser)
          bookingResponse.setDestinationArrivalTime(booking.getDate.getTime + response.getDriveTimeToDestination)
          sendMessage(Constants.EventBus.Address.Booking.CreateBookingResponse, booking.getUsername, bookingResponse)
        } else {
          sendCreateBookingErrorResponse(
            Constants.EventBus.Address.Booking.CreateBookingResponse,
            booking.getUsername,
            VehicleAlreadyBooked)
          store.deleteBooking(booking.getId)
        }
      }
    }

    override def changeProcessingBookingLicensePlate(changeRequest: ChangeBookingRequest): Unit = {
      val booking = store.getBookingByUser(changeRequest.getUsername, Booking.STATUS_PROCESSING)
      val response = new ChangeBookingResponse
      response.setLicencePlate(changeRequest.getNewLicensePlate)
      response.setUsername(changeRequest.getUsername)
      if (!booking.isPresent) {
        response.setSuccessful(false)
      } else {
        response.setSuccessful(store.updateBookingLicensePlate(booking.get().getId, changeRequest.getNewLicensePlate))
      }
      sendMessage(Constants.EventBus.Address.Booking.ChangeBookingLicensePlateResponse, response)
    }

    override def completeBooking(completeRequest: CompleteBookingRequest): Unit = {
      val username = completeRequest.getUsername
      val booking = store.getBookingByUser(username, Booking.STATUS_PROCESSING)
      val response = new CompleteBookingResponse
      if (!booking.isPresent) {
        response.setSuccess(false)
      } else {
        response.setSuccess(store.updateBookingStatus(booking.get().getId, Booking.STATUS_COMPLETED))
      }
      sendMessage(Constants.EventBus.Address.Booking.CompleteBookingVehicleServiceResponse, response)
      sendMessage(Constants.EventBus.Address.Booking.CompleteBookingUserResponse, username, response)
    }

    override def findProcessingBookingPositions(findRequest: FindBookingPositionsRequest): Unit = {
      val booking = store.getBookingByUser(findRequest.getUsername, Booking.STATUS_PROCESSING)
      val response = new FindBookingPositionsResponse
      if (!booking.isPresent) {
        response.setSuccessful(false)
      } else {
        response.setSuccessful(true)
        response.setLicensePlate(booking.get().getVehicleLicensePlate)
        response.setUsername(booking.get().getUsername)
        response.setUserPosition(booking.get().getUserPosition)
        response.setDestinationPosition(booking.get().getDestinationPosition)
      }
      sendMessage(Constants.EventBus.Address.Booking.FindBookingPositionResponse, response)
    }

    override def findAllBookings(getRequest: BookingListRequest): Unit = {
      val id = getRequest.getBackofficeId
      sendListInMessage(Constants.EventBus.Address.Booking.GetBookingsResponse, id, store.getBookings)
    }

    override def abortBooking(abortRequest: AbortBookingRequest): Unit = {
      val username = abortRequest.getUsername
      val booking = store.getBookingByUser(username, Booking.STATUS_PROCESSING)
      if (booking.isPresent) {
        store.updateBookingStatus(booking.get().getId, Booking.STATUS_ABORTED)
      }
    }
  }

}

