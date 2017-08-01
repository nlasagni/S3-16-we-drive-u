package com.wedriveu.vehicle.control

import com.wedriveu.services.shared.utilities.Log
import com.wedriveu.vehicle.entity.{Position, SelfDrivingVehicle}

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
   val timeOfJourney: Long = 0.0.asInstanceOf[Long]
   val timeStep: Long = 1000.0.asInstanceOf[Long]

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

  //This algorithm calculates the distance in Km between the points, then estimates the journey time and calculates
  //the coordinates reached during the journey.
  override def movementAndPositionChange(position: Position): Unit = {
    if (vehicle.position.equals(position)) {
      Log.log(newPositionIsTheSame)
    }
    else {
      val distanceInKm: Double = vehicle.position.getDistanceInKm(position)
      val estimatedJourneyTimeInMilliseconds: Long =
        ((distanceInKm / vehicle.speed) * conversionInMillis).asInstanceOf[Long]
      val deltaLat: Double = position.latitude - vehicle.position.latitude
      val deltaLon: Double = position.longitude - vehicle.position.longitude
      for(time <- timeOfJourney to estimatedJourneyTimeInMilliseconds by timeStep) {
        calculateMovement(time, estimatedJourneyTimeInMilliseconds, deltaLat, deltaLon)
        if((time + timeStep) > estimatedJourneyTimeInMilliseconds) {
          calculateMovement(estimatedJourneyTimeInMilliseconds, estimatedJourneyTimeInMilliseconds, deltaLat, deltaLon)
        }
      }
    }
  }

  override def positionChangeUponBooking(userPosition: Position, destinationPosition: Position): Unit = {
    movementAndPositionChange(userPosition)
    movementAndPositionChange(destinationPosition)
  }

  private def calculateMovement(time: Long,
                                estimatedJourneyTimeInMilliseconds: Long,
                                deltaLat: Double,
                                deltaLon: Double): Unit = {
    val elapsedTime: Double = time / estimatedJourneyTimeInMilliseconds
    var latInter: Double = vehicle.position.latitude + deltaLat * elapsedTime
    var lonInter: Double = vehicle.position.longitude + deltaLon * elapsedTime
    vehicle.position = new Position(latInter, lonInter)
  }

}

