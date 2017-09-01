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
    private val Response = "service.booking.response"
    private val Find = Request + Dot + "find"

    /**
      * A queue to receive create booking messages.
      */
    val Create: String = Request + Dot + "create"
    /**
      * A queue to receive change booking messages.
      */
    val Change: String = Request + Dot + "change"
    /**
      * A queue to receive complete booking messages.
      */
    val Complete: String = Request + Dot + "complete"
    /**
      * A queue to receive find by date booking messages.
      */
    val FindByDate: String = Find + Dot + "byDate"
    /**
      * A queue to receive find position booking messages.
      */
    val FindPosition: String = Find + Dot + "position"
    /**
      * A queue to receive book vehicle booking response messages.
      */
    val BookVehicle: String = Response + Dot + "bookVehicle"
    /**
      * A queue to receive book vehicle booking response messages.
      */
    val GetBookings: String = Find + Dot + "allBookings"
    /**
      * A queue to receive book vehicle booking response messages.
      */
    val AbortBookings: String = Request + Dot + "abort"

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
        private val GetBookings = "getBookings"
        private val LicensePlate = "licensePlate"
        private val Position = "position"

        /**
          * Address for receiving and sending messages for a create booking request.
          */
        val CreateBookingRequest: String = Request + Dot + CreateBooking
        /**
          * Address for receiving and sending messages for a create booking response.
          */
        val CreateBookingResponse: String = Response + Dot + CreateBooking
        /**
          * Address for receiving and sending messages for a change booking request.
          */
        val ChangeBookingLicensePlateRequest: String = Request + Dot + ChangeBooking + Dot + LicensePlate
        /**
          * Address for receiving and sending messages for a change booking response.
          */
        val ChangeBookingLicensePlateResponse: String =
          Response + Dot + ChangeBooking + Dot + LicensePlate
        /**
          * Address for receiving and sending messages for a complete booking request.
          */
        val CompleteBookingRequest: String = Request + Dot + CompleteBooking
        /**
          * Address for receiving and sending messages for a complete booking response.
          */
        val CompleteBookingVehicleServiceResponse: String = Response + Dot + CompleteBooking + "vehicleService"
        /**
          * Address for receiving and sending messages for a complete booking response to the user.
          */
        val CompleteBookingUserResponse: String = Response + Dot + CompleteBooking + "user"
        /**
          * Address for receiving and sending messages for a find booking position request.
          */
        val FindBookingPositionRequest: String = Request + Dot + FindBooking + Dot + Position
        /**
          * Address for receiving and sending messages for a find booking position response.
          */
        val FindBookingPositionResponse: String = Response + Dot + FindBooking + Dot + Position
        /**
          * Address for receiving and sending messages for a get all bookings request.
          */
        val GetBookingsRequest: String = Request + Dot + GetBookings
        /**
          * Address for receiving and sending messages for a get all bookings response.
          */
        val GetBookingsResponse: String = Response + Dot + GetBookings
        /**
          * Address for receiving and sending messages for an abort booking request.
          */
        val AbortBookingRequest: String = Request + Dot + "abort"
        /**
          * Address for receiving and sending messages for an abort booking response.
          */
        val AbortGetBookingResponse: String = Response + Dot + "abort"

      }

      /**
        * Vehicle event bus addresses.
        */
      object Vehicle {

        private val BookVehicle = "book.vehicle"

        /**
          * Address for receiving and sending messages for a book vehicle request.
          */
        val BookVehicleRequest: String = Request + Dot + BookVehicle
        /**
          * Address for receiving and sending messages for a book vehicle response.
          */
        val BookVehicleResponse: String = Response + Dot + BookVehicle

      }

    }

    /**
      * Constants for [[io.vertx.scala.core.eventbus.EventBus]] message content management.
      */
    object Message {

      /**
        * Key used to retrieve a specificRoutingKey value.
        */
      val SpecificRoutingKey: String = "specificRoutingKey"

    }

  }

}
