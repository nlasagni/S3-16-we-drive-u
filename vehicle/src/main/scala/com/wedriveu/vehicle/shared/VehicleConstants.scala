package com.wedriveu.vehicle.shared

/**
  * @author Michele Donati on 06/08/2017.
  */

/** This trait is useful to access vehicle's constants and RabbitMq infrastructure's constants. */

object VehicleConstants {
  final val stateBroken: String = "broken"
  final val stateRecharging: String = "recharging"
  final val stateAvailable: String = "available"
  final val stateBooked: String = "booked"
  final val stateStolen: String = "stolen"
  final val earthRadiusInKm: Double = 6372.795477598
  final val maxBatteryValue: Double = 100.0
  final val MAXIMUM_DISTANCE_TO_RECHARGE = 20
  final val ESTIMATED_KILOMETERS_PER_PERCENTAGE = 10
  final val ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS = 0.1
  final val initialLatitude: Double = 44.1454528
  final val initialLongitude: Double = 12.2474513
}

