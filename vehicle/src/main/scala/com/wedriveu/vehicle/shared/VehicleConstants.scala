package com.wedriveu.vehicle.shared

/**
  * @author Michele Donati on 06/08/2017.
  */

/** This trait is useful to access vehicle's constants and RabbitMq infrastructure's constants. */

object VehicleConstants {
  final val earthRadiusInKm: Double = 6372.795477598
  final val maxBatteryValue: Double = 100.0
  final val MAXIMUM_DISTANCE_TO_RECHARGE = 2.0
  final val ESTIMATED_KILOMETERS_PER_PERCENTAGE = 10
  final val ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS = 0.1
  val RechargingThreshold = 50.0

  val zeroBattery: Double = 0.0

  // This value indicates the battery consumed after 10 seconds of the vehicle journey. The vehicle consumes 1% of
  // battery every 10Km.

  val newPositionIsTheSame: String = "The position given is the same, the vehicle doesn't move"
  val timeOfJourney: Long = 0
  val timeStep: Long = 10
  val conversionInSeconds: Double = 3600.0
  val oneSecondInMillis: Long = 1000
  val batteryValueLog: String = "Battery value = %"
  val distanceInKmLog: String = "Distance in Km is:"
  val timeInSecondsLog: String = "Time in seconds is:"
  val newPositionLog: String = "New Position = "
  val commaLog: String = " , "
  val stateBrokenStolenLog: String = "Vehicle is broken or has been stolen. Its position is: "
  val needRechargingLog: String = "Need recharging, next recharging station is at coordinates: "
  val arrivedToRechargeLog: String = "Arrived to recharging station, proceed with the recharge."
  val startRechargeProcessLog: String = "Started the recharge process, 10 seconds until finish..."
  val endRechargeProcessLog: String = "Ended recharge process. Vehicle battery percentage = "
  val errorRechargeProcessLog: String = "Error in the recharge process, vehicle status = "
  val vehicleSetToBrokenLog: String = "Vehicle is broken, please send substitute"
  val vehicleIsAlreadyStolenLog: String = "The vehicle is stolen actually, but it is also broken"
  val vehicleSetToStolenLog: String = "Vehicle is stolen, please contact authorities"
  val noNeedToRechargeLog: String = "No need to recharge, battery value is over " + RechargingThreshold + "."
  val cantRechargeLog: String = "Can't recharge, vehicle is broken/stolen."

}

