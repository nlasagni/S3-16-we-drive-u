package com.wedriveu.vehicle.control

import com.rabbitmq.client._
import com.wedriveu.shared.util.{Constants, Position}
import com.wedriveu.vehicle.boundary.{VehicleStopView, _}
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants
import com.wedriveu.vehicle.simulation.{VehicleEventsObservables, VehicleEventsObservablesImpl}
import io.vertx.core.json.JsonObject
import io.vertx.core.{AsyncResult, DeploymentOptions, Handler, Vertx}

import scala.concurrent.Future

/**
  * @author Michele Donati on 28/07/2017.
  */

/** This models the control part of the vehicle. */
trait VehicleControl {
  /** This starts the engine of the vehicle, initializing the communication infrastructure. */
  def startVehicleEngine(): Unit

  /** This is usefull to get the vehicle for retrieve vehicle informations.
    *
    * @return Returns the instance of the vehicle.
    */
  def getVehicle(): SelfDrivingVehicle

  /** This method permits, at configurator will, to pick up Vehicle Broken events. */
  def subscribeToBrokenEvents(): Unit

  /** This method permits, at configurator will, to pick up Vehicle Stolen events. */
  def subscribeToStolenEvents(): Unit

  /** This method should be called when a booking request arrive from the service.
    *
    * @param userPosition        Indicates the user position.
    * @param destinationPosition Indicates the destination position for the user.
    * @param notRealisticVar     Setteted to False the execution of the system will be more realistic.
    */
  def changePositionUponBooking(userPosition: Position, destinationPosition: Position, notRealisticVar: Boolean): Unit

  /** This method permits to retrieve the username of the user associated to this vehicle.
    *
    * @return The username of the user.
    */
  def getUsername: String

  /** This method permits to define the username of the user associated to this vehicle.
    *
    * @param newUsername This indicates the new username to set.
    */
  def setUsername(newUsername: String): Unit

  /** This method permits to retrieve the variable which indicates if the user is on board or not.
    *
    * @return True if the user is on board, False otherwise.
    */
  def getUserOnBoard(): Boolean

  /** This method permits to set the internal variable if the user is on board or not.
    *
    * @param userOnBoard True if the user is on board, False otherwise.
    */
  def setUserOnBoard(userOnBoard: Boolean): Unit

  /** This method permits to execute the behaviour "Change Position".
    *
    * @param position The position to reach.
    */
  def goToDestination(position: Position): Unit

  /** This method permits to retrieve the behaviours control of the system
    *
    * @return The behaviours instance.
    */
  def getBehavioursControl(): VehicleBehaviours

  /** This method permits to unsubsribe the control system to vehicle broken events. */
  def unsubscribeToBrokenEvents(): Unit

  /** This method permits to unsubsribe the control system to vehicle stolen events. */
  def unsubscribeToStolenEvents(): Unit
}

class VehicleControlImpl(vertx: Vertx,
  imageUrl: String,
  description: String,
  licence: String,
  state: String,
  position: Position,
  battery: Double,
  speed: Double,
  stopUi: VehicleStopView,
  var debugVar: Boolean) extends VehicleControl {
  val vehicleGiven: SelfDrivingVehicle =
    new SelfDrivingVehicle(imageUrl, description, licence, state, position, battery, speed)
  val vehicleEventsObservables: VehicleEventsObservables = new VehicleEventsObservablesImpl
  val vehicleBehaviours: VehicleBehaviours = new VehicleBehavioursImpl(this, vertx, vehicleGiven, stopUi, debugVar)
  val received: String = " [x] Received '"
  val awaiting: String = " [x] Awaiting requests"
  var kilometersToDo: Double = .0
  var connection: Connection = _
  var channel: Channel = _
  var username: String = _
  var userOnBoard: Boolean = false
  var vehicleVerticleCanDrive: VehicleVerticleCanDriveImpl = _
  var vehicleVerticleBook: VehicleVerticleBookImpl = _
  var vehicleVerticleArrivedNotify: VehicleVerticleArrivedNotifyImpl = _
  var vehicleVerticleDriveCommand: VehicleVerticleDriveCommandImpl = _
  var vehicleVerticleUpdate: VehicleVerticleUpdateImpl = _
  var vehicleVerticleRegister: VehicleVerticleRegisterImpl = _
  var vehicleVerticleForUser: VehicleVerticleForUserImpl = _
  private val workerPoolName = "controlWorkerPool"

  stopUi.setVehicleAssociated(this)

  override def startVehicleEngine(): Unit = {
    vehicleVerticleCanDrive = new VehicleVerticleCanDriveImpl(this)
    vertx.deployVerticle(vehicleVerticleCanDrive, new DeploymentOptions().setWorker(true))

    vehicleVerticleBook = new VehicleVerticleBookImpl(this)
    vertx.deployVerticle(vehicleVerticleBook, new DeploymentOptions().setWorker(true))

    vehicleVerticleArrivedNotify = new VehicleVerticleArrivedNotifyImpl(this)
    vertx.deployVerticle(vehicleVerticleArrivedNotify, new DeploymentOptions().setWorker(true))

    vehicleVerticleDriveCommand = new VehicleVerticleDriveCommandImpl(this, debugVar)
    vertx.deployVerticle(vehicleVerticleDriveCommand, new DeploymentOptions().setWorker(true))

    vehicleVerticleUpdate = new VehicleVerticleUpdateImpl(this)
    vertx.deployVerticle(vehicleVerticleUpdate, new DeploymentOptions().setWorker(true))

    vehicleVerticleForUser = new VehicleVerticleForUserImpl(this)
    vertx.deployVerticle(vehicleVerticleForUser, new DeploymentOptions().setWorker(true))

    vehicleVerticleRegister = new VehicleVerticleRegisterImpl(this)
    vertx.deployVerticle(vehicleVerticleRegister, new DeploymentOptions().setWorker(true))
  }

  private def executeBehaviour(callback: () => Unit) = callback()

  private def executeBehaviour(callback: (Position) => Unit, position: Position) = callback(position)

  private def executeBehaviour(callback: (Position, Position, Boolean) => Unit,
    position1: Position,
    position2: Position,
    notRealisticVar: Boolean) = callback(position1, position2, notRealisticVar)

  override def getVehicle(): SelfDrivingVehicle = vehicleGiven

  override def getUsername: String = username

  override def setUsername(newUsername: String): Unit = {
    username = newUsername
  }

  override def getUserOnBoard(): Boolean = userOnBoard

  override def setUserOnBoard(newUserOnBoard: Boolean): Unit = {
    userOnBoard = newUserOnBoard
  }

  override def subscribeToBrokenEvents(): Unit = {
    vehicleEventsObservables.brokenEventObservable().subscribe(event => {
      executeBehaviour(vehicleBehaviours.checkVehicleAndSetItBroken)
    })
  }

  override def subscribeToStolenEvents(): Unit = {
    vehicleEventsObservables.stolenEventObservable().subscribe(event => {
      executeBehaviour(vehicleBehaviours.setVehicleStolen)
    })
  }

  override def changePositionUponBooking(userPosition: Position,
    destinationPosition: Position,
    notRealisticVar: Boolean): Unit = {
    if (!vehicleGiven.getState().equals(Constants.Vehicle.STATUS_RECHARGING) ||
        !vehicleGiven.getState().equals(Constants.Vehicle.STATUS_BROKEN_STOLEN)) {
      val executor = vertx.createSharedWorkerExecutor(workerPoolName)
      executor.executeBlocking((async: io.vertx.core.Future[Object]) => {
        executeBehaviour(vehicleBehaviours.positionChangeUponBooking, userPosition, destinationPosition, notRealisticVar)
        async.complete()
      }, (result: AsyncResult[Object]) => {
        executor.close()
      })
    }
  }

  override def goToDestination(position: Position): Unit = {
    val executor = vertx.createSharedWorkerExecutor(workerPoolName)
    executor.executeBlocking((async: io.vertx.core.Future[Object]) => {
      executeBehaviour(vehicleBehaviours.movementAndPositionChange, position)
      async.complete()
    }, (result: AsyncResult[Object]) => {
      executor.close()
      if (!vehicleGiven.getState().equals(Constants.Vehicle.STATUS_RECHARGING)
          && !vehicleGiven.getState().equals(Constants.Vehicle.STATUS_BROKEN_STOLEN)
          && !userOnBoard
          && !debugVar) {
        checkAndGoToRecharge()
      }
    })
  }

  private def checkAndGoToRecharge(): Unit = vehicleGiven.battery match {
    case b if b < VehicleConstants.RechargingThreshold =>
      val executor = vertx.createSharedWorkerExecutor(workerPoolName)
      executor.executeBlocking((async: io.vertx.core.Future[Object]) => {
        executeBehaviour(vehicleBehaviours.goToRecharge)
        async.complete()
      }, (result: AsyncResult[Object]) => {executor.close()})
    case _ =>
      vehicleGiven.setState(Constants.Vehicle.STATUS_AVAILABLE)
      vertx.eventBus().send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, getVehicle().plate), null)
      stopUi.writeMessageLog(VehicleConstants.noNeedToRechargeLog)
  }

  override def getBehavioursControl(): VehicleBehaviours = vehicleBehaviours

  override def unsubscribeToBrokenEvents(): Unit = {
    vehicleEventsObservables.unsubscribeToBrokenEvents()
  }

  override def unsubscribeToStolenEvents(): Unit = {
    vehicleEventsObservables.unsubscribeToStolenEvents()
  }

}
