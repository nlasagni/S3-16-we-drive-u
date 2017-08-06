package com.wedriveu.vehicle.control

import com.wedriveu.services.shared.utilities.Log
import com.wedriveu.vehicle.boundary.VehicleStopView
import com.wedriveu.vehicle.entity.{Position, SelfDrivingVehicle}
import com.wedriveu.vehicle.shared.VehicleConstants

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

class VehicleBehavioursImpl(selfDrivingVehicle: SelfDrivingVehicle, stopUi: VehicleStopView) extends VehicleBehaviours {
   val zeroBattery: Double = 0.0
   val batteryThreshold: Double = 20.0
   // This value is given by the mathematical transformation of the speed in Km/h to Km/10s
   val kilometersInTenSecondsGivenSpeed: Double = selfDrivingVehicle.speed / 360
   // This value indicates the battery consumed after 10 seconds of the vehicle journey. The vehicle consumes 1% of
   // battery every 10Km.
   val batteryToConsume: Double = kilometersInTenSecondsGivenSpeed / 10
   val newPositionIsTheSame: String = "The position given is the same, the vehicle doesn't move"
   val timeOfJourney: Long = 0
   val timeStep: Long = 10
   val conversionInSeconds: Double = 3600.0
   val batteryValueLog: String = "Battery value = %"
   val distanceInKmLog: String = "Distance in Km is:"
   val timeInSecondsLog: String = "Time in seconds is:"
   val newPositionLog: String = "New Position = "
   val commaLog: String = " , "
   val stateBroken: String = "broken"
   val stateBrokenLog: String = "Vehicle is broken. His position is: "

   var deltaLat: Double = .0
   var deltaLon: Double = .0
   var notRecharging: Boolean = true

   private def drainBattery(): Unit = {
      if((selfDrivingVehicle.battery-batteryToConsume) <= zeroBattery) {
        selfDrivingVehicle.battery = zeroBattery
      }
      else {
        // Depends on vehicle speed
        selfDrivingVehicle.battery -= batteryToConsume
        stopUi.writeMessageLog(batteryValueLog + selfDrivingVehicle.battery)
      }
      if(selfDrivingVehicle.battery <= batteryThreshold && notRecharging) {
        selfDrivingVehicle.state = VehicleConstants.stateRecharging
        //This will be set to True when the vehicle recharges
        notRecharging = false
      }
   }

  //This algorithm calculates the distance in Km between the points, then estimates the journey time and calculates
  //the coordinates reached during the journey.
  override def movementAndPositionChange(position: Position): Unit = {
    if(!checkVehicleIsNotBroken()){
      return
    }
    if (selfDrivingVehicle.position.equals(position)) {
      Log.log(newPositionIsTheSame)
    }
    else {
      val distanceInKm: Double = selfDrivingVehicle.position.getDistanceInKm(position)
      stopUi.writeMessageLog(distanceInKmLog + distanceInKm)
      val estimatedJourneyTimeInSeconds: Long =
        ((distanceInKm / selfDrivingVehicle.speed) * conversionInSeconds).asInstanceOf[Long]
      stopUi.writeMessageLog(timeInSecondsLog + estimatedJourneyTimeInSeconds)
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
    if(!checkVehicleIsNotBroken()){
      return
    }
    val elapsedTime: Double = time.asInstanceOf[Double] / estimatedJourneyTimeInSeconds
    var latInter: Double = selfDrivingVehicle.position.latitude + deltaLat * elapsedTime
    var lonInter: Double = selfDrivingVehicle.position.longitude + deltaLon * elapsedTime
    selfDrivingVehicle.position = new Position(latInter, lonInter)
    drainBattery()
    stopUi.writeMessageLog(newPositionLog + latInter + commaLog + lonInter)
  }

  private def checkVehicleIsNotBroken(): Boolean = {
    if (selfDrivingVehicle.state.equals(stateBroken)) {
      stopUi.writeMessageLog(stateBrokenLog
        + selfDrivingVehicle.position.latitude
        + commaLog
        + selfDrivingVehicle.position.longitude)
      false
    }
    else {
      true
    }
  }

}

