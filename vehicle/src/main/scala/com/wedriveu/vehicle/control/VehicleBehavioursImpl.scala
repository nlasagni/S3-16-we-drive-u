package com.wedriveu.vehicle.control

import com.wedriveu.services.shared.utilities.{Log, Position}
import com.wedriveu.vehicle.entity.SelfDrivingVehicle

/**
  * @author Michele Donati on 31/07/2017.
  */

/** This is the vehicle behaviours models capturing different events. */
trait VehicleBehaviours {
  /** This models the behaviour of the vehicle's battery draining. */
  def drainBattery() : Unit

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

class VehicleBehavioursImpl(vehicle: SelfDrivingVehicle) extends VehicleBehaviours {
   var notRecharging: Boolean = true
   val zeroBattery: Double = 0.0
   val batteryThreshold: Double = 20.0
   val stateRecharging: String = "recharging"
   val batteryToConsume: Double = 1.0
   val newPositionIsTheSame: String = "The position given is the same, the vehicle doesn't move"
   val conversionInMillis: Int = 3600 * 1000

   override def drainBattery() = {
    if(vehicle.battery <= zeroBattery){
      Log.log("Battery of " + vehicle.plate + " = " + vehicle.battery.toString)
    }
    else{
      if((vehicle.battery-batteryToConsume) < zeroBattery) {
        vehicle.battery = zeroBattery
      }
      else {
        // Depends on vehicle speed
        vehicle.battery -= batteryToConsume
      }
      if(vehicle.battery <= batteryThreshold && notRecharging) {
        vehicle.state = stateRecharging
        //This will be set to True when the vehicle recharges
        notRecharging = false
        Log.log("Vehicle " + vehicle.plate + " state changed to: " + vehicle.state)
      }
      Log.log("Battery of " + vehicle.plate + " = " + vehicle.battery.toString)
    }
  }

  override def movementAndPositionChange(position: Position): Unit = {
    if (vehicle.position.equals(position)) {
      Log.log(newPositionIsTheSame)
    }
    else {
      var distanceInKm: Double = vehicle.position.getDistanceInKm(position)
      var estimatedJourneyTimeInMilliseconds: Long =
        ((distanceInKm / vehicle.speed) * conversionInMillis).asInstanceOf[Long]
      var deltaLat: Double = position.getLatitude - vehicle.position.getLatitude
      var deltaLon: Double = position.getLongitude - vehicle.position.getLongitude
      for(a <- 0.0.asInstanceOf[Long] to estimatedJourneyTimeInMilliseconds by 1000.0.asInstanceOf[Long]) {
        calculateMovement(a, estimatedJourneyTimeInMilliseconds, deltaLat, deltaLon)
        if((a + 1000.0.asInstanceOf[Long]) > estimatedJourneyTimeInMilliseconds) {
          calculateMovement(estimatedJourneyTimeInMilliseconds, estimatedJourneyTimeInMilliseconds, deltaLat, deltaLon)
        }
      }
    }
  }

  override def positionChangeUponBooking(userPosition: Position, destinationPosition: Position): Unit = {
    movementAndPositionChange(userPosition)
    movementAndPositionChange(destinationPosition)
  }

  private def calculateMovement(a: Long,
                                estimatedJourneyTimeInSeconds: Long,
                                deltaLat: Double,
                                deltaLon: Double): Unit = {
    var t0_1: Double = a / estimatedJourneyTimeInSeconds
    var latInter: Double = vehicle.position.getLatitude + deltaLat * t0_1
    var lonInter: Double = vehicle.position.getLongitude + deltaLon * t0_1
    vehicle.position = new Position(latInter, lonInter)
  }

}

