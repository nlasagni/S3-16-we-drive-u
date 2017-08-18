package com.wedriveu.services.booking.boundary

import java.util.Date

import com.wedriveu.services.booking.util.Constants
import com.wedriveu.shared.rabbitmq.message.CreateBookingRequest
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.eventbus.Message

import scala.concurrent.Future


/** Represents the booking service boundary.
  *
  * @author Nicola Lasagni on 12/08/2017.
  */
trait BookingBoundary {

  /** Creates a booking.
    *
    * @param bookingRequest The request data needed to create a [[com.wedriveu.services.shared.model.Booking]].
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

  /** Finds [[com.wedriveu.services.shared.model.Booking]]s in a range of dates.
    *
    * @param startDate The starting date filter.
    * @param endDate The end date filter.
    */
  def findAllBookingsByDate(startDate: Date, endDate: Date): Unit

}

/** Represents a [[BookingBoundary]] which implementation will be bounded to a [[ScalaVerticle]].
  *
  */
trait BookingBoundaryVerticle extends ScalaVerticle with BookingBoundary {}


object BookingBoundary {

  /**
    * The [[ScalaVerticle]] name to deploy a [[BookingBoundaryVerticle]].
    */
  val Verticle: String = s"scala:${classOf[BookingBoundaryVerticleImpl].getName}"

  private[boundary] class BookingBoundaryVerticleImpl extends BookingBoundaryVerticle {

    override def startFuture(): Future[_] = {
      registerForRequests()
      registerForResponses()
      vertx.deployVerticleFuture(BookingBoundaryConsumer.CreateBooking).flatMap({
        _ => vertx.deployVerticleFuture(BookingBoundaryConsumer.ChangeBooking)
      }).flatMap({
        _ => vertx.deployVerticleFuture(BookingBoundaryConsumer.CompleteBooking)
      }).flatMap({
        _ => vertx.deployVerticleFuture(BookingBoundaryConsumer.FindBookingPosition)
      }).flatMap({
        _ => vertx.deployVerticleFuture(BookingBoundaryConsumer.FindBookingByDate)
      })
    }

    private def registerForRequests(): Unit = {
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.CreateBookingRequest,
        handleCreateBookingRequest)
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.ChangeBookingLicensePlateRequest,
        handleChangeBookingRequest)
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.FindBookingPositionRequest,
        handleFindBookingByDestinationRequest)
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.FindBookingByDateRequest,
        handleFindBookingsByDateRequest)
    }

    private def registerForResponses(): Unit = {
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.CreateBookingResponse,
        handleCreateBookingResponse)
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.ChangeBookingLicensePlateResponse,
        handleChangeBookingResponse)
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.FindBookingPositionResponse,
        handleFindBookingByDestinationResponse)
      vertx.eventBus().consumer(Constants.EventBus.Address.Booking.FindBookingByDateResponse,
        handleFindBookingsByDateResponse)
    }

    private def handleCreateBookingRequest(message: Message[Object]): Unit = {
      println(s"I have received a message: ${message.body()}")
    }

    private def handleCreateBookingResponse(message: Message[Object]): Unit = {

    }

    private def handleChangeBookingRequest(message: Message[Object]): Unit = {
      println(s"I have received a message: ${message.body()}")
    }

    private def handleChangeBookingResponse(message: Message[Object]): Unit = {

    }

    private def handleFindBookingByDestinationRequest(message: Message[Object]): Unit = {
      println(s"I have received a message: ${message.body()}")
    }

    private def handleFindBookingByDestinationResponse(message: Message[Object]): Unit = {

    }

    private def handleFindBookingsByDateRequest(message: Message[Object]): Unit = {
      println(s"I have received a message: ${message.body()}")
    }

    private def handleFindBookingsByDateResponse(message: Message[Object]): Unit = {

    }

    override def createBooking(bookingRequest: CreateBookingRequest): Unit = ???

    override def changeActiveBookingLicensePlate(oldLicensePlate: String, newLicensePlate: String): Unit = ???

    override def findActiveBookingPositions(licensePlate: String): Unit = ???

    override def findAllBookingsByDate(startDate: Date, endDate: Date): Unit = ???

    override def completeBooking(licensePlate: String): Unit = ???
  }

}

