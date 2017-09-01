package com.wedriveu.services.booking.boundary

import io.vertx.lang.scala.ScalaVerticle

import scala.concurrent.Future


/** Represents the booking service boundary.
  *
  * @author Nicola Lasagni on 12/08/2017.
  */
trait BookingBoundary {

  /** Handles a create [[com.wedriveu.services.shared.model.Booking]] request.
    *
    * @return The future containing the result of this request.
    */
  def handleCreateBookingRequest(): Future[_]

  /** Handles a change [[com.wedriveu.services.shared.model.Booking]] license plate request.
    *
    * @return The future containing the result of this request.
    */
  def handleChangeActiveBookingLicensePlateRequest(): Future[_]

  /** Handles a complete [[com.wedriveu.services.shared.model.Booking]] request.
    *
    * @return The future containing the result of this request.
    */
  def handleCompleteBookingRequest(): Future[_]

  /** Handles a find positions of [[com.wedriveu.services.shared.model.Booking]] request.
    *
    * @return The future containing the result of this request.
    */
  def handleFindActiveBookingPositionsRequest(): Future[_]

  /** Handles a get all [[com.wedriveu.services.shared.model.Booking]]s request.
    *
    * @return The future containing the result of this request.
    */
  def handleGetBookingsRequest(): Future[_]

  /** Handles an abort [[com.wedriveu.services.shared.model.Booking]] request.
    *
    * @return The future containing the result of this request.
    */
  def handleAbortBookingRequest(): Future[_]

}

/** Represents a [[BookingBoundary]] which implementation will be bounded to a [[ScalaVerticle]].
  *
  */
trait BookingBoundaryVerticle extends ScalaVerticle with BookingBoundary {}


object BookingBoundaryVerticle {

  /**
    * The [[ScalaVerticle]] name to deploy a [[BookingBoundaryVerticle]].
    */
  val Verticle: String = s"scala:${classOf[BookingBoundaryVerticleImpl].getName}"

  private[boundary] class BookingBoundaryVerticleImpl extends BookingBoundaryVerticle {

    override def startFuture(): Future[_] = {
      handleCreateBookingRequest().flatMap({
        _ => handleChangeActiveBookingLicensePlateRequest()
      }).flatMap({
        _ => handleCompleteBookingRequest()
      }).flatMap({
        _ => handleFindActiveBookingPositionsRequest()
      }).flatMap({
        _ => handleGetBookingsRequest()
      }).flatMap({
        _ => handleAbortBookingRequest()
      })
    }

    override def handleCreateBookingRequest(): Future[_] = {
      vertx.deployVerticleFuture(BookingBoundaryConsumerVerticle.CreateBooking).flatMap({ _ =>
        vertx.deployVerticleFuture(BookingBoundaryConsumerVerticle.BookVehicle)
      }).flatMap(_ => {
        vertx.deployVerticleFuture(BookingBoundaryPublisherVerticle.CreateBooking)
      }).flatMap(_ => {
        vertx.deployVerticleFuture(BookingBoundaryPublisherVerticle.BookVehicle)
      })
    }

    override def handleChangeActiveBookingLicensePlateRequest(): Future[_] = {
      vertx.deployVerticleFuture(BookingBoundaryConsumerVerticle.ChangeBooking).flatMap({ _ =>
        vertx.deployVerticleFuture(BookingBoundaryPublisherVerticle.ChangeBooking)
      })
    }

    override def handleCompleteBookingRequest(): Future[_] = {
      vertx.deployVerticleFuture(BookingBoundaryConsumerVerticle.CompleteBooking).flatMap({ _ =>
        vertx.deployVerticleFuture(BookingBoundaryPublisherVerticle.CompleteBookingVehicleService)
      }).flatMap({ _ =>
        vertx.deployVerticleFuture(BookingBoundaryPublisherVerticle.CompleteBookingUser)
      })
    }

    override def handleFindActiveBookingPositionsRequest(): Future[_] = {
      vertx.deployVerticleFuture(BookingBoundaryConsumerVerticle.FindBookingPosition).flatMap({ _ =>
        vertx.deployVerticleFuture(BookingBoundaryPublisherVerticle.FindBookingPosition)
      })
    }

    override def handleGetBookingsRequest(): Future[_] = {
      vertx.deployVerticleFuture(BookingBoundaryConsumerVerticle.GetBookings).flatMap({ _ =>
        vertx.deployVerticleFuture(BookingBoundaryPublisherVerticle.GetBookings)
      })
    }

    override def handleAbortBookingRequest(): Future[_] = {
      vertx.deployVerticleFuture(BookingBoundaryConsumerVerticle.AbortBooking)
    }
  }

}

