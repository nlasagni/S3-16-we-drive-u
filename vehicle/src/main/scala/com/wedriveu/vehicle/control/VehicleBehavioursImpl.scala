package com.wedriveu.vehicle.control

import com.wedriveu.services.shared.utilities.{Log}
import com.wedriveu.vehicle.entity.{Position, SelfDrivingVehicle}

/**
  * @author Michele Donati on 31/07/2017.
  */

/** This is the vehicle behaviours models capturing different events. */
trait VehicleBehaviours {
  /** This models the behaviour of the vehicle's movement and position changing.
    *
    * @param position Indicates the final position we want for the vehicle.
    */
  def movementAndPositionChange(position: Position): Unit

  /** This methos is used when we want the vehicle drives to the user and then to the destination position.
    *
    * @param userPosition Indicates the position of the user.
    * @param destinationPosition Indicates the destination position for the user.
    */
  def positionChangeUponBooking(userPosition: Position, destinationPosition: Position): Unit

}

class VehicleBehavioursImpl(selfDrivingVehicle: SelfDrivingVehicle) extends VehicleBehaviours {
   val zeroBattery: Double = 0.0
   val batteryThreshold: Double = 20.0
   val stateRecharging: String = "recharging"
   // This value indicates the battery consumed after 100 seconds of the vehicle journey. In 100 seconds, the vehicle,
   // with an average speed of 50Km/h travels 1,4 km. The consume is 1% of battery per 10 Km, so the consume estimated
   // is 0.14%.
   val batteryToConsume: Double = 0.14
   val newPositionIsTheSame: String = "The position given is the same, the vehicle doesn't move"
   val timeOfJourney: Long = 0
   val timeStep: Long = 10
   val timeStepBatteryConsumed: Double = 100.0
   val conversionInSeconds: Double = 3600.0

   var deltaLat: Double = .0
   var deltaLon: Double = .0
   var notRecharging: Boolean = true
   var timePassedForBattery: Double = .0

   private def drainBattery(): Unit = {
      if((selfDrivingVehicle.battery-batteryToConsume) <= zeroBattery) {
        selfDrivingVehicle.battery = zeroBattery
      }
      else {
        // Depends on vehicle speed
        selfDrivingVehicle.battery -= batteryToConsume
      }
      if(selfDrivingVehicle.battery <= batteryThreshold && notRecharging) {
        selfDrivingVehicle.state = stateRecharging
        //This will be set to True when the vehicle recharges
        notRecharging = false
      }
      Log.log("Battery of " + selfDrivingVehicle.plate + " = " + selfDrivingVehicle.battery.toString)
  }

  //This algorithm calculates the distance in Km between the points, then estimates the journey time and calculates
  //the coordinates reached during the journey.
  override def movementAndPositionChange(position: Position): Unit = {
    if (selfDrivingVehicle.position.equals(position)) {
      Log.log(newPositionIsTheSame)
    }
    else {
      timePassedForBattery = timeStepBatteryConsumed
      val distanceInKm: Double = selfDrivingVehicle.position.getDistanceInKm(position)
      Log.log("Distance in Km is:" + distanceInKm)
      val estimatedJourneyTimeInSeconds: Long =
        ((distanceInKm / selfDrivingVehicle.speed) * conversionInSeconds).asInstanceOf[Long]
      Log.log("Time in seconds is:" + estimatedJourneyTimeInSeconds)
      for(time <- timeOfJourney to estimatedJourneyTimeInSeconds by timeStep) {
        deltaLat = position.latitude - selfDrivingVehicle.position.latitude
        deltaLon = position.longitude - selfDrivingVehicle.position.longitude
        calculateMovement(time, estimatedJourneyTimeInSeconds, deltaLat, deltaLon)
        if((time + timeStep) > estimatedJourneyTimeInSeconds) {
          deltaLat = position.latitude - selfDrivingVehicle.position.latitude
          deltaLon = position.longitude - selfDrivingVehicle.position.longitude
          calculateMovement(estimatedJourneyTimeInSeconds, estimatedJourneyTimeInSeconds, deltaLat, deltaLon)
        }
      }
    }
  }

  override def positionChangeUponBooking(userPosition: Position, destinationPosition: Position): Unit = {
    movementAndPositionChange(userPosition)
    movementAndPositionChange(destinationPosition)
  }

  private def calculateMovement(time: Long,
                                estimatedJourneyTimeInSeconds: Long,
                                deltaLat: Double,
                                deltaLon: Double): Unit = {
    val elapsedTime: Double = time.asInstanceOf[Double] / estimatedJourneyTimeInSeconds
    var latInter: Double = selfDrivingVehicle.position.latitude + deltaLat * elapsedTime
    var lonInter: Double = selfDrivingVehicle.position.longitude + deltaLon * elapsedTime
    selfDrivingVehicle.position = new Position(latInter, lonInter)
    if(time != 0.0 && ((time / timePassedForBattery)%1) == 0.0) {
      drainBattery()
      timePassedForBattery += timeStepBatteryConsumed
    }
  }

}

