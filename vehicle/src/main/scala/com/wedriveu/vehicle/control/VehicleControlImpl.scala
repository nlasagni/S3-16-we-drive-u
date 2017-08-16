package com.wedriveu.vehicle.control

import com.rabbitmq.client._
import com.wedriveu.shared.util.Position
import com.wedriveu.vehicle.boundary.VehicleStopView
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants
import com.wedriveu.vehicle.simulation.{VehicleEventsObservables, VehicleEventsObservablesImpl}
import com.weriveu.vehicle.boundary._
import io.vertx.core.{DeploymentOptions, Vertx}

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

  /** This method permits, at configurator will, to pick up Movement and Position changes events.*/
  def subscribeToMovementAndChangePositionEvents(): Unit

  /** This method permits, at configurator will, to pick up Vehicle Broken events. */
  def subscribeToBrokenEvents(): Unit

  /** This method permits, at configurator will, to pick up Vehicle Stolen events. */
  def subscribeToStolenEvents(): Unit

  /** This method should be called when a booking request arrive from the service.
    *
    * @param userPosition Indicates the user position.
    * @param destinationPosition Indicates the destination position for the user.
    * @param notRealisticVar Setteted to False the execution of the system will be more realistic.
    */
  def changePositionUponBooking(userPosition: Position, destinationPosition: Position, notRealisticVar: Boolean): Unit

  /** This method permits to retrieve the username of the user associated to this vehicle.
    *
    * @return The username of the user.
    */
  def getUsername(): String

  /** This method permits to define the username of the user associated to this vehicle.
    *
    * @param newUsername This indicates the new username to set.
    */
  def setUsername(newUsername: String): Unit

  /** This method permits to retrieve the verticle used for notify the vehicle service that the vehicle is arrived to
    * destination.
    *
    * @return Tthe verticle.
    */
  def getArrivedNotifyVerticle(): VehicleVerticleArrivedNotifyImpl

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

}

class VehicleControlImpl(imageUrl: String,
                         description: String,
                         license: String,
                         state: String,
                         position: Position,
                         battery: Double,
                         speed: Double,
                         stopUi: VehicleStopView,
                         var debugVar: Boolean) extends VehicleControl {
  val vehicleGiven : SelfDrivingVehicle =
    new SelfDrivingVehicle(imageUrl, description, license, state, position, battery, speed)
  val vehicleEventsObservables: VehicleEventsObservables = new VehicleEventsObservablesImpl
  val vehicleBehaviours: VehicleBehaviours = new VehicleBehavioursImpl(this, vehicleGiven, stopUi, false)
  val received: String = " [x] Received '"
  val awaiting: String = " [x] Awaiting requests"
  var kilometersToDo: Double = .0
  var connection: Connection = null
  var channel: Channel = null
  var username: String = null
  var userOnBoard: Boolean = false
  var vehicleVerticleCanDrive: VehicleVerticleCanDriveImpl = null
  var vehicleVerticleBook: VehicleVerticleBookImpl = null
  var vehicleVerticleArrivedNotify: VehicleVerticleArrivedNotifyImpl = null
  var vehicleVerticleDriveCommand: VehicleVerticleDriveCommandImpl = null
  var vehicleVerticleUpdate: VehicleVerticleUpdateImpl = null
  var vehicleVerticleRegister: VehicleVerticleRegisterImpl = null


  stopUi.setVehicleAssociated(this)

  override def startVehicleEngine(): Unit = {
    val vertx: Vertx = Vertx.vertx()
    vehicleVerticleCanDrive = new VehicleVerticleCanDriveImpl(this)
    vertx.deployVerticle(vehicleVerticleCanDrive, new DeploymentOptions().setWorker(true))

    vehicleVerticleBook = new VehicleVerticleBookImpl(this)
    vertx.deployVerticle(vehicleVerticleBook, new DeploymentOptions().setWorker(true))

    vehicleVerticleArrivedNotify = new VehicleVerticleArrivedNotifyImpl(this)
    vertx.deployVerticle(vehicleVerticleArrivedNotify, new DeploymentOptions().setWorker(true))

    vehicleVerticleDriveCommand = new VehicleVerticleDriveCommandImpl(this,debugVar)
    vertx.deployVerticle(vehicleVerticleDriveCommand, new DeploymentOptions().setWorker(true))

    vehicleVerticleUpdate = new VehicleVerticleUpdateImpl(this)
    vertx.deployVerticle(vehicleVerticleUpdate, new DeploymentOptions().setWorker(true))

    vehicleVerticleRegister = new VehicleVerticleRegisterImpl(this)
    vertx.deployVerticle(vehicleVerticleRegister, new DeploymentOptions().setWorker(true))
  }

  private def executeBehaviour(callback:() => Unit) = callback()

  private def executeBehaviour(callback:(Position) => Unit, position: Position) = callback(position)

  private def executeBehaviour(callback:(Position, Position, Boolean) => Unit,
                               position1: Position,
                               position2: Position,
                               notRealisticVar: Boolean) = callback(position1,position2,notRealisticVar)

  override def getVehicle() = vehicleGiven

  override def subscribeToMovementAndChangePositionEvents(): Unit = {
    vehicleEventsObservables.movementAndChangePositionObservable().subscribe(event => {
      if(!(vehicleGiven.getState().equals(VehicleConstants.stateRecharging))
        || !(vehicleGiven.getState().equals(VehicleConstants.stateBroken))) {
        executeBehaviour(vehicleBehaviours.movementAndPositionChange, event)
        if (!(vehicleGiven.getState().equals(VehicleConstants.stateRecharging))
          && !(vehicleGiven.getState().equals(VehicleConstants.stateBroken))
          && !(vehicleGiven.getState().equals(VehicleConstants.stateStolen))
          && !vehicleBehaviours.isUserOnBoard()
          && !debugVar) {
          executeBehaviour(vehicleBehaviours.goToRecharge)
        }
      }
    })
  }

  override def getArrivedNotifyVerticle(): VehicleVerticleArrivedNotifyImpl = vehicleVerticleArrivedNotify

  override def getUsername(): String = username

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
    executeBehaviour(vehicleBehaviours.positionChangeUponBooking, userPosition, destinationPosition, notRealisticVar)
    if (!(vehicleGiven.getState().equals(VehicleConstants.stateRecharging))
      && !(vehicleGiven.getState().equals(VehicleConstants.stateBroken))
      && !(vehicleGiven.getState().equals(VehicleConstants.stateStolen))
      && !vehicleBehaviours.isUserOnBoard()
      && !debugVar) {
      executeBehaviour(vehicleBehaviours.goToRecharge)
    }
  }

}
