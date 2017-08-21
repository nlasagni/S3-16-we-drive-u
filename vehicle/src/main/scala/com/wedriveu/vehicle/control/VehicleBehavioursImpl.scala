package com.wedriveu.vehicle.control

import com.wedriveu.shared.util.{Constants, Log, Position}
import com.wedriveu.vehicle.boundary.VehicleStopView
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants
import com.wedriveu.vehicle.simulation.{RechargingLatchManager, RechargingLatchManagerImpl}
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject

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

  /** This method checks the vehicle's state, and if it isn't Stolen, sets it to Broken. */
  def checkVehicleAndSetItBroken(): Unit

  /** This method sets the vehicle status to Stolen. This state has priority over Broken status. */
  def setVehicleStolen(): Unit

  /** This method permits to set test variables in order to handle tests.
    *
    * @param userVar The value of the variable.
    */
  def setTestUserVar(userVar: Boolean): Unit
}

class VehicleBehavioursImpl(vehicleControl: VehicleControl,
                            vertx: Vertx,
                            selfDrivingVehicle: SelfDrivingVehicle,
                            stopUi: VehicleStopView,
                            var debugVar:Boolean)
  extends VehicleBehaviours {
   val eventBus: EventBus = vertx.eventBus()
   val zeroBattery: Double = 0.0
   val batteryThreshold: Double = 20.0
   // This value is given by the mathematical transformation of the speed in Km/h to Km/10s
   val kilometersInTenSecondsGivenSpeed: Double = selfDrivingVehicle.speed / 360.0
   // This value indicates the battery consumed after 10 seconds of the vehicle journey. The vehicle consumes 1% of
   // battery every 10Km.

  //TODO
   //val batteryToConsume: Double = kilometersInTenSecondsGivenSpeed / 10.0

  //TODO
  val metersRunBeforeUpdate: Double = 100.0
  val batteryToConsume: Double = 1/((Constants.ESTIMATED_KILOMETERS_PER_PERCENTAGE*1000)/metersRunBeforeUpdate)

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
   val stateBrokenLog: String = "Vehicle is broken. His position is: "
   val stateStolenLog: String = "Vehicle is stolen. His position is: "
   val needRechargingLog: String = "Need recharging, next recharging station is at coordinates: "
   val arrivedToRechargeLog: String = "Arrived to recharging station, proceed with the recharge."
   val startRechargeProcessLog: String = "Started the recharge process, 10 seconds until finish..."
   val endRechargeProcessLog: String = "Ended recharge process. Vehicle battery percentage = "
   val errorRechargeProcessLog: String = "Error in the recharge process, vehicle status = "
   val vehicleSetToBrokenLog: String = "Vehicle is broken, please send substitute"
   val vehicleIsAlreadyStolenLog: String = "The vehicle is stolen actually, but it is also broken"
   val vehicleSetToStolenLog: String = "Vehicle is stolen, please contact authorities"
   val cantRechargeLog: String = "Can't recharge, vehicle is broken/stolen."

   var deltaLat: Double = .0
   var deltaLon: Double = .0
   var debugging: Boolean = false
   var rechargingLatchManager: RechargingLatchManager = null
   var testVar: Boolean = false
   var testUserVar: Boolean = false
   var verticlesTestVar: Boolean = false
   var destinationPosition: Position = _

  //This algorithm calculates the distance in Km between the points, then estimates the journey time and calculates
  //the coordinates reached during the journey.
  // The Thread.sleep in the method is useful to render the simulation real and for testing observables.
  override def movementAndPositionChange(position: Position): Unit = {
    movementAndPositionChangeStefano(position)
//    if(checkVehicleIsBrokenOrStolen()){
//      return
//    }
//    else {
//      val distanceInKm: Double = selfDrivingVehicle.position.getDistanceInKm(position)
//      stopUi.writeMessageLog(distanceInKmLog + distanceInKm)
//      val estimatedJourneyTimeInSeconds: Long =
//        ((distanceInKm / selfDrivingVehicle.speed) * conversionInSeconds).asInstanceOf[Long]
//      stopUi.writeMessageLog(timeInSecondsLog + estimatedJourneyTimeInSeconds)
//      breakable {
//
//        for (time <- timeOfJourney to estimatedJourneyTimeInSeconds by timeStep) {
//          if (checkVehicleIsBrokenOrStolen()) {
//            break()
//          }
//          if(!debugVar && !testVar){
//            Thread.sleep(oneSecondInMillis * timeStep)
//          }
//          eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
//            new JsonObject())
//          deltaLat = position.getLatitude - selfDrivingVehicle.position.getLatitude
//          deltaLon = position.getLongitude - selfDrivingVehicle.position.getLongitude
//          calculateMovement(time, estimatedJourneyTimeInSeconds, deltaLat, deltaLon)
//          val distanceDone: Double = distanceInKm - selfDrivingVehicle.position.getDistanceInKm(position)
//
//          //TODO
//          Log.info(this.getClass.getSimpleName, "NOT USED Distance done: " + distanceDone)
//
//          if ((time + timeStep) > estimatedJourneyTimeInSeconds) {
//            if (checkVehicleIsBrokenOrStolen()) {
//              break()
//            }
//            deltaLat = position.getLatitude - selfDrivingVehicle.position.getLatitude
//            deltaLon = position.getLongitude - selfDrivingVehicle.position.getLongitude
//            calculateMovement(estimatedJourneyTimeInSeconds, estimatedJourneyTimeInSeconds, deltaLat, deltaLon)
//            debugging = true
//          }
//        }
//        if(!verticlesTestVar) {
//          if (vehicleControl.getUserOnBoard() && !testUserVar) {
//            if (destinationPosition.getDistanceInKm(selfDrivingVehicle.getPosition)
//              <= VehicleConstants.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS) {
//              vehicleControl.setUserOnBoard(false)
//              eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_NOTIFY,vehicleControl.getVehicle().plate),
//                new JsonObject())
//            }
//          }
//        }
//      }
//    }
  }

  //This algorithm calculates the distance in Km between the points, then estimates the journey time and calculates
  //the coordinates reached during the journey.
  // The Thread.sleep in the method is useful to render the simulation real and for testing observables.
  def movementAndPositionChangeStefano(position: Position): Unit = {
    if(checkVehicleIsBrokenOrStolen()){
      return
    }
    else {

      //TODO
      Log.info(this.getClass.getSimpleName, "Destination: " + position.toString)
      //TODO
      Log.info(this.getClass.getSimpleName, "Vehicle start position: " + selfDrivingVehicle.position.toString)

      val distanceInKm: Double = selfDrivingVehicle.position.getDistanceInKm(position)
      stopUi.writeMessageLog(distanceInKmLog + distanceInKm)
      val estimatedJourneyTimeInSeconds: Long =
        ((distanceInKm / selfDrivingVehicle.speed) * conversionInSeconds).asInstanceOf[Long]
      stopUi.writeMessageLog(timeInSecondsLog + estimatedJourneyTimeInSeconds)
      val distanceInMeters: Double = distanceInKm * 1000

      //TODO
      Log.info(this.getClass.getSimpleName, "distanceInMeters: " + distanceInMeters)

      //TODO
      val numberUpdates: Int = (distanceInMeters / metersRunBeforeUpdate).toInt

      //TODO
      if (numberUpdates == 0 && !vehicleControl.getUserOnBoard()) {
        return
      }

      //TODO
      Log.info(this.getClass.getSimpleName, "numberUpdates: " + numberUpdates)

      val latitudeDifference = position.getLatitude - selfDrivingVehicle.getPosition.getLatitude
      val longitudeDifference = position.getLongitude - selfDrivingVehicle.getPosition.getLongitude
      deltaLat = (latitudeDifference * metersRunBeforeUpdate) / distanceInMeters
      deltaLon = (longitudeDifference * metersRunBeforeUpdate) / distanceInMeters

      Log.info(this.getClass.getSimpleName, "deltaLat: " + deltaLat)
      Log.info(this.getClass.getSimpleName, "deltaLon: " + deltaLon)

//      deltaLat = (position.getLatitude - selfDrivingVehicle.getPosition.getLatitude) /
//          (distanceInMeters / metersRunBeforeUpdate)
//      deltaLon = (position.getLongitude - selfDrivingVehicle.getPosition.getLongitude) /
//          (distanceInMeters / metersRunBeforeUpdate)


      breakable {
        //for (time <- timeOfJourney to estimatedJourneyTimeInSeconds by timeStep) {
        for (metersDriven <- 0.0 to distanceInMeters by metersRunBeforeUpdate) {
          //TODO
          Log.info(this.getClass.getSimpleName, "Meters driven: " + metersDriven)
          if (checkVehicleIsBrokenOrStolen()) {
            break()
          }
          if(!debugVar && !testVar){

            //TODO
            Log.info(this.getClass.getSimpleName ,
              "Sleep of: " + ((metersRunBeforeUpdate * 1000) / (selfDrivingVehicle.speed/3.6)).toInt)

            //TODO Restore millisecondsForUpdateMeters
//            val millisecondsForUpdateMeters: Int =
//              ((metersRunBeforeUpdate * 1000) / (selfDrivingVehicle.speed/3.6)).toInt
            Thread.sleep(1000)
          }
          eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
            new JsonObject())
          updatePosition(deltaLat, deltaLon)
          //          val distanceDone: Double = distanceInKm - selfDrivingVehicle.position.getDistanceInKm(position)
//          if ((metersDriven + metersRunBeforeUpdate) > distanceInMeters) {
//
//            //TODO
//            Log.info(this.getClass.getSimpleName,
//              "Extra cycle, my position: " + selfDrivingVehicle.position.toString)
//
//            if (checkVehicleIsBrokenOrStolen()) {
//              break()
//            }
//            updatePosition(deltaLat, deltaLon)
//          }
        }
        if(!verticlesTestVar) {
          if (vehicleControl.getUserOnBoard() && !testUserVar) {
            if (destinationPosition.getDistanceInKm(selfDrivingVehicle.getPosition)
                <= VehicleConstants.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS) {

              //TODO
              Log.info(this.getClass.getSimpleName, "Arrived at destination")

              vehicleControl.setUserOnBoard(false)
              eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_NOTIFY,vehicleControl.getVehicle().plate),
                new JsonObject())
            }
          }
        }
      }
    }
  }

  private def logVehicleUpdate(): Unit = {
    stopUi.writeMessageLog(newPositionLog
        + selfDrivingVehicle.position.getLatitude
        + commaLog
        + selfDrivingVehicle.position.getLongitude)
  }

  private def updatePosition(deltaLat: Double, deltaLon: Double) :Unit ={
    val positionCurrent: Position = new Position(
      selfDrivingVehicle.getPosition.getLatitude + deltaLat,
      selfDrivingVehicle.getPosition.getLongitude + deltaLon)

    //TODO
    Log.info(this.getClass.getSimpleName, "updatePosition: " + positionCurrent.toString)

    selfDrivingVehicle.position = positionCurrent
    logVehicleUpdate()
    drainBattery()
  }

  private def checkVehicleIsBrokenOrStolen(): Boolean = {
    if(selfDrivingVehicle.getState().equals(VehicleConstants.stateStolen)){
      stopUi.writeMessageLog(stateStolenLog
        + selfDrivingVehicle.position.getLatitude
        + commaLog
        + selfDrivingVehicle.position.getLongitude)
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject)
      true
    }
    else if (selfDrivingVehicle.getState().equals(VehicleConstants.stateBroken)) {
      stopUi.writeMessageLog(stateBrokenLog
        + selfDrivingVehicle.position.getLatitude
        + commaLog
        + selfDrivingVehicle.position.getLongitude)
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject)
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
    val elapsedTime: Double =
      if (estimatedJourneyTimeInSeconds != 0) time.asInstanceOf[Double] / estimatedJourneyTimeInSeconds else 0
    val latInter: Double = selfDrivingVehicle.position.getLatitude + deltaLat * elapsedTime
    val lonInter: Double = selfDrivingVehicle.position.getLongitude + deltaLon * elapsedTime

    //TODO
    Log.info(this.getClass.getSimpleName, "Elapsed time: " + elapsedTime)

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
    verticlesTestVar = notRealisticVar

    //TODO
    Log.info(this.getClass.getSimpleName, "New Destination Position: " + destinationPosition.toString)

    this.destinationPosition = destinationPosition
    movementAndPositionChange(userPosition)
    if(userPosition.getDistanceInKm(selfDrivingVehicle.getPosition)
      <= VehicleConstants.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS) {

      //TODO
      Log.info(this.getClass.getSimpleName, "Arrived at user")

      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_FOR_USER, vehicleControl.getVehicle().plate),
        createMessage())
    }
  }

  private def createMessage(): JsonObject = {
    val jsonObject: JsonObject = new JsonObject()
    jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(destinationPosition).toString)
    jsonObject
  }

  //This method calculates a random pair of latitude and longitude to simulate the position of the recharging station,
  // then sends the vehicle there and awaits its recharging.
   override def goToRecharge(): Unit = {
    debugging = false
    val canRecharge: Boolean = selfDrivingVehicle.checkVehicleIsBrokenOrStolenAndSetRecharging()
    if (canRecharge) {
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject())
      val randomNumber1 : Double = Math.random()
      val randomNumber2 : Double = Math.random()
      val distance : Double = 20.0 * Math.sqrt(randomNumber1)
      val bearing: Double = 2 * Math.PI * randomNumber2
      //TODO
      val newLatitude: Double = selfDrivingVehicle.position.getLatitude +
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
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject)
      simulateRecharging()
      debugging = true
    }
    else {
      stopUi.writeMessageLog(cantRechargeLog)
    }
  }

  private def simulateRecharging(): Unit = {
    stopUi.writeMessageLog(startRechargeProcessLog)
    rechargingLatchManager = new RechargingLatchManagerImpl(selfDrivingVehicle, vertx)
    rechargingLatchManager.startLatchedThread()
    if(selfDrivingVehicle.battery < VehicleConstants.maxBatteryValue) {
      stopUi.writeMessageLog(errorRechargeProcessLog + selfDrivingVehicle.getState())
    }
    else {
      stopUi.writeMessageLog(endRechargeProcessLog + selfDrivingVehicle.battery)
    }
  }

  override def getDebuggingVar(): Boolean = debugging

  override def checkVehicleAndSetItBroken(): Unit = {
    val result = selfDrivingVehicle.checkVehicleIsStolenAndSetBroken()
    if(result) {
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject())
      stopUi.writeMessageLog(vehicleSetToBrokenLog)
    }
    else {
      stopUi.writeMessageLog(vehicleIsAlreadyStolenLog)
    }
  }

  override def setVehicleStolen(): Unit = {
    selfDrivingVehicle.setState(VehicleConstants.stateStolen)
    eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
      new JsonObject())
    stopUi.writeMessageLog(vehicleSetToStolenLog)
  }

  override def setTestUserVar(userVar: Boolean): Unit = {
    this.testUserVar = userVar
  }

}

