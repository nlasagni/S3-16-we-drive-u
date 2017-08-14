package com.wedriveu.vehicle.control

import com.wedriveu.shared.util.Position
import com.wedriveu.vehicle.boundary.VehicleStopView
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants
import com.wedriveu.vehicle.simulation.{RechargingLatchManager, RechargingLatchManagerImpl}

import scala.util.control.Breaks._

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

  /** This method is used when we want the vehicle drives to the user and then to the destination position.
    *
    * @param userPosition Indicates the position of the user.
    * @param destinationPosition Indicates the destination position for the user.
    * @param notRealisticVar Setteted to False the execution of the system will be more realistic.
    */
  def positionChangeUponBooking(userPosition: Position, destinationPosition: Position, notRealisticVar: Boolean): Unit

  /** This method is called to start the recharge process of the vehicle. */
  def goToRecharge(): Unit

  /** This method is used for retrieve the debugging variable for testing.
    *
    * @return The value of the debugging value.
    */
  def getDebuggingVar(): Boolean

  /** This method is used to retrieve informations about if the user is on board of the vehicle.
    *
    * @return True if the user is on board, False otherwise.
    */
  def isUserOnBoard(): Boolean

  /** This method checks the vehicle's state, and if it isn't Stolen, sets it to Broken. */
  def checkVehicleAndSetItBroken(): Unit

  /** This method sets the vehicle status to Stolen. This state has priority over Broken status. */
  def setVehicleStolen(): Unit
}

class VehicleBehavioursImpl(selfDrivingVehicle: SelfDrivingVehicle, stopUi: VehicleStopView,var debugVar:Boolean)
  extends VehicleBehaviours {
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
   val stateBrokenLog: String = "Vehicle is broken. His position is: "
   val stateStolenLog: String = "Vehicle is stolen. His position is: "
   val needRechargingLog: String = "Need recharging, next recharging station is at coordinates: "
   val arrivedToRechargeLog: String = "Arrived to recharging station, proceed with the recharge."
   val startRechargeProcessLog: String = "Started the recharge process, 10 seconds until finish..."
   val endRechargeProcessLog: String = "Ended recharge process. Vehicle battery percentage = "
   val errorRechargeProcessLog: String = "Error in the recharge process, vehicle status = "
   val vehicleSetToBrokenLog: String = "Vehicle is broken, please sends substitute"
   val vehicleIsAlreadyStolenLog: String = "The vehicle is stolen actually, but it is also broken"
   val vehicleSetToStolenLog: String = "Vehicle is stolen, please contacts authorities"
   val cantRechargeLog: String = "Can't recharge, vehicle is broken/stolen."

   var deltaLat: Double = .0
   var deltaLon: Double = .0
   var userOnBoard: Boolean = false
   var debugging: Boolean = false
   var rechargingLatchManager: RechargingLatchManager = null
   var testVar: Boolean = false

  //This algorithm calculates the distance in Km between the points, then estimates the journey time and calculates
  //the coordinates reached during the journey.
  // The Thread.sleep in the method is useful to render the simulation real and for testing observables.
  override def movementAndPositionChange(position: Position): Unit = {
    if(checkVehicleIsBrokenOrStolen()){
      return
    }
    else {
      val distanceInKm: Double = selfDrivingVehicle.position.getDistanceInKm(position)
      stopUi.writeMessageLog(distanceInKmLog + distanceInKm)
      val estimatedJourneyTimeInSeconds: Long =
        ((distanceInKm / selfDrivingVehicle.speed) * conversionInSeconds).asInstanceOf[Long]
      stopUi.writeMessageLog(timeInSecondsLog + estimatedJourneyTimeInSeconds)
      breakable {
        for (time <- timeOfJourney to estimatedJourneyTimeInSeconds by timeStep) {
          if (checkVehicleIsBrokenOrStolen()) {
            break()
          }
          if(!debugVar && !testVar){
            Thread.sleep(500)
          }
          deltaLat = position.getLatitude - selfDrivingVehicle.position.getLatitude
          deltaLon = position.getLongitude - selfDrivingVehicle.position.getLongitude
          calculateMovement(time, estimatedJourneyTimeInSeconds, deltaLat, deltaLon)
          if ((time + timeStep) > estimatedJourneyTimeInSeconds) {
            if (checkVehicleIsBrokenOrStolen()) {
              break()
            }
            deltaLat = position.getLatitude - selfDrivingVehicle.position.getLatitude
            deltaLon = position.getLongitude - selfDrivingVehicle.position.getLongitude
            calculateMovement(estimatedJourneyTimeInSeconds, estimatedJourneyTimeInSeconds, deltaLat, deltaLon)
            debugging = true
          }
        }
      }
    }
  }

  private def checkVehicleIsBrokenOrStolen(): Boolean = {
    if(selfDrivingVehicle.getState().equals(VehicleConstants.stateStolen)){
      stopUi.writeMessageLog(stateStolenLog
        + selfDrivingVehicle.position.getLatitude
        + commaLog
        + selfDrivingVehicle.position.getLongitude)
      //TODO Here i will notify the service that i'm stolen
      true
    }
    else if (selfDrivingVehicle.getState().equals(VehicleConstants.stateBroken)) {
      stopUi.writeMessageLog(stateBrokenLog
        + selfDrivingVehicle.position.getLatitude
        + commaLog
        + selfDrivingVehicle.position.getLongitude)
      //TODO Here i will notify the service that i'm broken
      true
    }
    else {
      false
    }
  }

  private def calculateMovement(time: Long,
                                estimatedJourneyTimeInSeconds: Long,
                                deltaLat: Double,
                                deltaLon: Double): Unit = {
    val elapsedTime: Double = time.asInstanceOf[Double] / estimatedJourneyTimeInSeconds
    var latInter: Double = selfDrivingVehicle.position.getLatitude + deltaLat * elapsedTime
    var lonInter: Double = selfDrivingVehicle.position.getLongitude + deltaLon * elapsedTime
    selfDrivingVehicle.position = new Position(latInter, lonInter)
    drainBattery()
    stopUi.writeMessageLog(newPositionLog
      + selfDrivingVehicle.position.getLatitude
      + commaLog
      + selfDrivingVehicle.position.getLongitude)
  }

  private def drainBattery(): Unit = {
    if((selfDrivingVehicle.battery-batteryToConsume) <= zeroBattery) {
      selfDrivingVehicle.battery = zeroBattery
    }
    else {
      // Depends on vehicle speed
      selfDrivingVehicle.battery -= batteryToConsume
      stopUi.writeMessageLog(batteryValueLog + selfDrivingVehicle.battery)
    }
  }

  override def positionChangeUponBooking(userPosition: Position,
                                         destinationPosition: Position,
                                         notRealisticVar: Boolean): Unit = {
    testVar = notRealisticVar
    movementAndPositionChange(userPosition)
    userOnBoard = true
    //TODO Here i will notify the service and the user that i'm arrived to the user
    movementAndPositionChange(destinationPosition)
    userOnBoard = false
    //TODO Here i will notify the service that i'm arrived to destination
  }

  //This method calculates a random pair of latitude and longitude to simulate the position of the recharging station,
  // then sends the vehicle there and awaits its recharging.
   override def goToRecharge(): Unit = {
    debugging = false
    val canRecharge: Boolean = selfDrivingVehicle.checkVehicleIsBrokenOrStolenAndSetRecharging()
    if (canRecharge) {
      //TODO Here i will notify the service that i'm going to recharge
      val randomNumber1 : Double = Math.random()
      val randomNumber2 : Double = Math.random()
      val distance : Double = 20.0 * Math.sqrt(randomNumber1)
      val bearing: Double = 2 * Math.PI * randomNumber2
      val newLatitude: Double =
        Math.asin(Math.sin(selfDrivingVehicle.position.getLatitude)
          * Math.cos(distance/VehicleConstants.earthRadiusInKm)
          + Math.cos(selfDrivingVehicle.position.getLatitude)
          * Math.sin(distance/VehicleConstants.earthRadiusInKm)
          * Math.cos(bearing))
      val newLongitude: Double =
        selfDrivingVehicle.position.getLongitude
      + Math.atan2(Math.sin(bearing)
        *Math.sin(distance/VehicleConstants.earthRadiusInKm)
        *Math.cos(selfDrivingVehicle.position.getLatitude),
        Math.cos(distance/VehicleConstants.earthRadiusInKm)
          -Math.sin(selfDrivingVehicle.position.getLatitude)
          *Math.sin(newLatitude))
      stopUi.writeMessageLog(needRechargingLog + newLatitude + commaLog + newLongitude)
      movementAndPositionChange(new Position(newLatitude, newLongitude))
      stopUi.writeMessageLog(arrivedToRechargeLog)
      simulateRecharging()
      debugging = true
      //TODO At the end i will set the state available (if not broken during the process) and notify the service
    }
    else {
      stopUi.writeMessageLog(cantRechargeLog)
    }
  }

  private def simulateRecharging(): Unit = {
    stopUi.writeMessageLog(startRechargeProcessLog)
    rechargingLatchManager = new RechargingLatchManagerImpl(selfDrivingVehicle)
    rechargingLatchManager.startLatchedThread()
    if(selfDrivingVehicle.battery < VehicleConstants.maxBatteryValue) {
      stopUi.writeMessageLog(errorRechargeProcessLog + selfDrivingVehicle.getState())
    }
    else {
      stopUi.writeMessageLog(endRechargeProcessLog + selfDrivingVehicle.battery)
    }
  }

  override def getDebuggingVar(): Boolean = debugging

  override def isUserOnBoard(): Boolean = userOnBoard

  override def checkVehicleAndSetItBroken(): Unit = {
    val result = selfDrivingVehicle.checkVehicleIsStolenAndSetBroken
    if(result) {
      stopUi.writeMessageLog(vehicleSetToBrokenLog)
    }
    else {
      stopUi.writeMessageLog(vehicleIsAlreadyStolenLog)
    }
  }

  override def setVehicleStolen(): Unit = {
    selfDrivingVehicle.setState(VehicleConstants.stateStolen)
    stopUi.writeMessageLog(vehicleSetToStolenLog)
  }

}

