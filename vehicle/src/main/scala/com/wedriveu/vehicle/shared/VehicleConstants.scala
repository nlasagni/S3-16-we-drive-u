package com.wedriveu.vehicle.shared

/**
  * @author Michele Donati on 06/08/2017.
  */

/** This trait is useful to access vehicle's constants. */

object VehicleConstants {
  final val stateBroken: String = "broken"
  final val stateRecharging: String = "recharging"
  final val stateAvailable: String ="available"
  final val stateBooked: String ="booked"
  final val stateStolen: String ="stolen"
  final val earthRadiusInKm: Double = 6372.795477598
  final val maxBatteryValue: Double = 100.0
  final val MAXIMUM_DISTANCE_TO_RECHARGE = 20
  final val ESTIMATED_KILOMETERS_PER_PERCENTAGE = 10
  final val initialLatitude: Double = 44.1454528
  final val initialLongitude: Double = 12.2474513
}

object Exchanges {
  final val VEHICLE = "vehicle"
  final val NO_EXCHANGE = ""
}

object RoutingKeys {
  final val CAN_DRIVE_REQUEST = "vehicle.request.candrive.%s"
  final val CAN_DRIVE_RESPONSE = "vehicle.request.candrive.%s"
  final val REGISTER_REQUEST = "vehicle.request.add"
  final val REGISTER_RESPONSE = "vehicle.response.add.%s"
  final val BOOK_REQUEST = "vehicle.request.book.%s"
  final val BOOK_RESPONSE = "vehicle.response.book.%s"
  final val VEHICLE_ARRIVED = "vehicle.event.arrived"
  final val VEHICLE_UPDATE = "vehicle.event.updated"
}

object EventBusConstants {
  final val BODY = "body"
}
