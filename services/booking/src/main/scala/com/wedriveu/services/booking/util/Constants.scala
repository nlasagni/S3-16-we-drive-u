package com.wedriveu.services.booking.util

/** Constants of the booking service.
  *
  * @author Nicola Lasagni on 12/08/2017.
  */
object Constants {

  /**
    * Just a dot separator.
    */
  private val Dot = "."

  /**
    * Queues used by the [[com.wedriveu.services.booking.boundary.BookingBoundaryConsumer]] to receive messages.
    */
  object Queue {

    private val Request = "service.booking.request"
    private val Find = Request + Dot + "find"

    /**
      * A queue to receive create booking messages.
      */
    val Create: String = Request + Dot + "create"
    /**
      * A queue to receive change booking messages.
      */
    val Change: String  = Request + Dot + "change"
    /**
      * A queue to receive complete booking messages.
      */
    val Complete: String  = Request + Dot + "complete"
    /**
      * A queue to receive find by date booking messages.
      */
    val FindByDate: String  = Find + Dot + "byDate"
    /**
      * A queue to receive find position booking messages.
      */
    val FindPosition: String  = Find + Dot + "position"

  }

  /**
    * Constants used by all the [[io.vertx.lang.scala.ScalaVerticle]] of the booking service.
    */
  object EventBus {

    private val EventBus = "eventbus"

    /**
      * Constants for [[io.vertx.scala.core.eventbus.EventBus]] management.
      */
    object Address {

      private val Address = EventBus + Dot + "address"
      private val Request = Address + Dot + "request"
      private val Response = Address + Dot + "response"

      /**
        * Booking event bus addresses.
        */
      object Booking {

        private val CreateBooking = "create.booking"
        private val ChangeBooking = "change.booking"
        private val CompleteBooking = "complete.booking"
        private val FindBooking = "find.booking"
        private val LicensePlate = "licensePlate"
        private val Position = "position"
        private val ByDate = "byDate"

        /**
          * Address for receiving and sending messages for a create booking request.
          */
        val CreateBookingRequest: String = Request + CreateBooking
        /**
          * Address for receiving and sending messages for a create booking response.
          */
        val CreateBookingResponse: String = Response + CreateBooking
        /**
          * Address for receiving and sending messages for a change booking request.
          */
        val ChangeBookingLicensePlateRequest: String = Request + ChangeBooking + Dot + LicensePlate
        /**
          * Address for receiving and sending messages for a change booking response.
          */
        val ChangeBookingLicensePlateResponse: String = Response + ChangeBooking + Dot + LicensePlate
        /**
          * Address for receiving and sending messages for a complete booking request.
          */
        val CompleteBookingRequest: String = Request + CompleteBooking
        /**
          * Address for receiving and sending messages for a complete booking response.
          */
        val CompleteBookingResponse: String = Response + CompleteBooking
        /**
          * Address for receiving and sending messages for a find booking position request.
          */
        val FindBookingPositionRequest: String = Request + FindBooking + Dot + Position
        /**
          * Address for receiving and sending messages for a find booking position response.
          */
        val FindBookingPositionResponse: String = Response + FindBooking + Dot + Position
        /**
          * Address for receiving and sending messages for a find booking by date request.
          */
        val FindBookingByDateRequest: String = Request + FindBooking + Dot + ByDate
        /**
          * Address for receiving and sending messages for a find booking by date response.
          */
        val FindBookingByDateResponse: String = Response + FindBooking + Dot + ByDate

      }

    }

  }

}
