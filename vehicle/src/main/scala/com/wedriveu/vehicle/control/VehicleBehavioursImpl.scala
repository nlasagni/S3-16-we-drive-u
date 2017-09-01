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
    * @param userPosition        Indicates the position of the user.
    * @param destinationPosition Indicates the destination position for the user.
    * @param notRealisticVar     Setteted to False the execution of the system will be more realistic.
    */
  def positionChangeUponBooking(userPosition: Position, destinationPosition: Position, notRealisticVar: Boolean): Unit

  /** This method is called to start the recharge process of the vehicle. */
  def goToRecharge(): Unit

  /** This method is used for retrieve the debugging variable for testing.
    *
    * @return The value of the debugging value.
    */
  def getDebuggingVar: Boolean

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
  var debugVar: Boolean)
    extends VehicleBehaviours {

  private val VehicleSkipMovement = "Vehicle stolen or broken, skipping movement algorithm."

  private val metersRunBeforeUpdate: Double = 100.0
  private val batteryToConsume: Double =
    1 / ((VehicleConstants.ESTIMATED_KILOMETERS_PER_PERCENTAGE * VehicleConstants.oneSecondInMillis) / metersRunBeforeUpdate)

  val eventBus: EventBus = vertx.eventBus()
  var deltaLat: Double = .0
  var deltaLon: Double = .0
  var debugging: Boolean = false
  var rechargingLatchManager: RechargingLatchManager = _
  var testVar: Boolean = false
  var testUserVar: Boolean = false
  var verticlesTestVar: Boolean = false
  var destinationPosition: Position = _

  override def movementAndPositionChange(position: Position): Unit = {
    if (checkVehicleIsBrokenOrStolen()) {
      Log.info(this.getClass.getSimpleName, VehicleSkipMovement)
    } else {
      val distanceInKm: Double = selfDrivingVehicle.position.getDistanceInKm(position)
      stopUi.writeMessageLog(VehicleConstants.distanceInKmLog + distanceInKm)
      val estimatedJourneyTimeInSeconds: Long =
        ((distanceInKm / selfDrivingVehicle.speed) * VehicleConstants.conversionInSeconds).asInstanceOf[Long]
      stopUi.writeMessageLog(VehicleConstants.timeInSecondsLog + estimatedJourneyTimeInSeconds)
      val distanceInMeters: Double = distanceInKm * 1000
      val numberUpdates: Int = (distanceInMeters / metersRunBeforeUpdate).toInt
      if (numberUpdates == 0 && !vehicleControl.getUserOnBoard()) {
        return
      }
      val latitudeDifference = position.getLatitude - selfDrivingVehicle.getPosition.getLatitude
      val longitudeDifference = position.getLongitude - selfDrivingVehicle.getPosition.getLongitude
      deltaLat = (latitudeDifference * metersRunBeforeUpdate) / distanceInMeters
      deltaLon = (longitudeDifference * metersRunBeforeUpdate) / distanceInMeters
      breakable {
        for (_ <- 0.0 to distanceInMeters by metersRunBeforeUpdate) {
          if (checkVehicleIsBrokenOrStolen()) {
            break()
          }
          if (!debugVar && !testVar) {
            val millisecondsForUpdateMeters: Int =
              ((metersRunBeforeUpdate * VehicleConstants.oneSecondInMillis) / (selfDrivingVehicle.speed / 3.6)).toInt
              Thread.sleep(millisecondsForUpdateMeters)
          }
          eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
            new JsonObject())
          updatePosition(deltaLat, deltaLon)
        }
        if (!verticlesTestVar) {
          if (vehicleControl.getUserOnBoard() && !testUserVar) {
            val distance = destinationPosition.getDistanceInKm(selfDrivingVehicle.getPosition)

            if (distance <= VehicleConstants.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS) {
              vehicleControl.setUserOnBoard(false)
              eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_NOTIFY, vehicleControl.getVehicle().plate),
                new JsonObject())
            }
          }
        }
      }
    }
  }

  private def logVehicleUpdate(): Unit = {
    stopUi.writeMessageLog(VehicleConstants.newPositionLog
        + selfDrivingVehicle.position.getLatitude
        + VehicleConstants.commaLog
        + selfDrivingVehicle.position.getLongitude)
  }

  private def updatePosition(deltaLat: Double, deltaLon: Double): Unit = {
    val positionCurrent: Position = new Position(
      selfDrivingVehicle.getPosition.getLatitude + deltaLat,
      selfDrivingVehicle.getPosition.getLongitude + deltaLon)
    selfDrivingVehicle.position = positionCurrent
    logVehicleUpdate()
    drainBattery()
  }

  private def checkVehicleIsBrokenOrStolen(): Boolean = {
    if (selfDrivingVehicle.getState().equals(Constants.Vehicle.STATUS_BROKEN_STOLEN)) {
      stopUi.writeMessageLog(VehicleConstants.stateBrokenStolenLog
          + selfDrivingVehicle.position.getLatitude
          + VehicleConstants.commaLog
          + selfDrivingVehicle.position.getLongitude)
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject)
      true
    }
    else {
      false
    }
  }

  private def drainBattery(): Unit = {
    if ((selfDrivingVehicle.battery - batteryToConsume) <= VehicleConstants.zeroBattery) {
      selfDrivingVehicle.battery = VehicleConstants.zeroBattery
    }
    else {
      // Depends on vehicle speed
      selfDrivingVehicle.battery -= batteryToConsume
      stopUi.writeMessageLog(VehicleConstants.batteryValueLog + selfDrivingVehicle.battery)
    }
  }

  override def positionChangeUponBooking(userPosition: Position,
    destinationPosition: Position,
    notRealisticVar: Boolean): Unit = {
    verticlesTestVar = notRealisticVar
    this.destinationPosition = destinationPosition
    movementAndPositionChange(userPosition)
    if (userPosition.getDistanceInKm(selfDrivingVehicle.getPosition)
        <= VehicleConstants.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS) {
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_FOR_USER, vehicleControl.getVehicle().plate),
        createMessage())
    }
  }

  private def createMessage(): JsonObject = {
    val jsonObject: JsonObject = new JsonObject()
    jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(destinationPosition).toString)
    jsonObject
  }

  //This method calculates a random pair of latitude and longitude to simulate the position
  // of the recharging station, then sends the vehicle there and awaits its recharging.
  override def goToRecharge(): Unit = {
    debugging = false
    val canRecharge: Boolean = selfDrivingVehicle.checkVehicleIsBrokenOrStolenAndSetRecharging()
    if (canRecharge) {
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject())
      val randomNumber1: Double = Math.random()
      val randomNumber2: Double = Math.random()
      val distance: Double = VehicleConstants.MAXIMUM_DISTANCE_TO_RECHARGE * Math.sqrt(randomNumber1)
      val bearing: Double = 2 * Math.PI * randomNumber2
      val newLatitude: Double = selfDrivingVehicle.position.getLatitude +
          Math.asin(Math.sin(selfDrivingVehicle.position.getLatitude)
              * Math.cos(distance / VehicleConstants.earthRadiusInKm)
              + Math.cos(selfDrivingVehicle.position.getLatitude)
              * Math.sin(distance / VehicleConstants.earthRadiusInKm)
              * Math.cos(bearing))
      val newLongitude: Double =
        selfDrivingVehicle.position.getLongitude
      +Math.atan2(Math.sin(bearing)
          * Math.sin(distance / VehicleConstants.earthRadiusInKm)
          * Math.cos(selfDrivingVehicle.position.getLatitude),
        Math.cos(distance / VehicleConstants.earthRadiusInKm)
            - Math.sin(selfDrivingVehicle.position.getLatitude)
            * Math.sin(newLatitude))
      stopUi.writeMessageLog(VehicleConstants.needRechargingLog + newLatitude + VehicleConstants.commaLog + newLongitude)
      movementAndPositionChange(new Position(newLatitude, newLongitude))
      stopUi.writeMessageLog(VehicleConstants.arrivedToRechargeLog)
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject)
      simulateRecharging()
      debugging = true
    }
    else {
      stopUi.writeMessageLog(VehicleConstants.cantRechargeLog)
    }
  }

  private def simulateRecharging(): Unit = {
    stopUi.writeMessageLog(VehicleConstants.startRechargeProcessLog)
    rechargingLatchManager = new RechargingLatchManagerImpl(selfDrivingVehicle, vertx)
    rechargingLatchManager.startLatchedThread()
    if (selfDrivingVehicle.battery < VehicleConstants.maxBatteryValue) {
      stopUi.writeMessageLog(VehicleConstants.errorRechargeProcessLog + selfDrivingVehicle.getState())
    }
    else {
      stopUi.writeMessageLog(VehicleConstants.endRechargeProcessLog + selfDrivingVehicle.battery)
    }
  }

  override def getDebuggingVar(): Boolean = debugging

  override def checkVehicleAndSetItBroken(): Unit = {
    val result = selfDrivingVehicle.checkVehicleIsStolenAndSetBroken()
    if (result) {
      eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
        new JsonObject())
      stopUi.writeMessageLog(VehicleConstants.vehicleSetToBrokenLog)
    }
    else {
      stopUi.writeMessageLog(VehicleConstants.vehicleIsAlreadyStolenLog)
    }
  }

  override def setVehicleStolen(): Unit = {
    selfDrivingVehicle.setState(Constants.Vehicle.STATUS_BROKEN_STOLEN)
    eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicleControl.getVehicle().plate),
      new JsonObject())
    stopUi.writeMessageLog(VehicleConstants.vehicleSetToStolenLog)
  }

  override def setTestUserVar(userVar: Boolean): Unit = {
    this.testUserVar = userVar
  }

}

