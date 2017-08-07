package com.wedriveu.vehicle.shared

/**
  * @author Michele Donati on 06/08/2017.
  */

/** This trait is useful to access vehicle's constants. */
trait VehicleConstants {}

object VehicleConstants {
  final val stateBroken: String = "broken"
  final val stateRecharging: String = "recharging"
  final val stateAvailable: String ="available"
  final val stateBooked: String ="booked"
  final val stateStolen: String ="stolen"
  final val earthRadiusInKm: Double = 6372.795477598
  final val maxBatteryValue: Double = 100.0
  //TODO Put this constants in the Nicola's shared module, after it's push.

}
