package com.wedriveu.vehicle.control

import com.rabbitmq.client._

import com.wedriveu.shared.utils.Position

import com.wedriveu.vehicle.boundary.VehicleStopView
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants
import com.wedriveu.vehicle.simulation.{VehicleEventsObservables, VehicleEventsObservablesImpl}
import com.weriveu.vehicle.boundary._
import io.vertx.core.Vertx

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
}

class VehicleControlImpl(license: String,
                         state: String,
                         position: Position,
                         battery: Double,
                         speed: Double,
                         stopUi: VehicleStopView,
                         var debugVar: Boolean) extends VehicleControl {
  val vehicleGiven : SelfDrivingVehicle = new SelfDrivingVehicle(license, state, position, battery, speed)
  val vehicleEventsObservables: VehicleEventsObservables = new VehicleEventsObservablesImpl
  val vehicleBehaviours: VehicleBehaviours = new VehicleBehavioursImpl(vehicleGiven, stopUi, false)
  val received: String = " [x] Received '"
  val awaiting: String = " [x] Awaiting requests"
  var kilometersToDo: Double = .0
  var connection: Connection = null
  var channel: Channel = null
  var username: String = null

  stopUi.setVehicleAssociated(this)

  def startVehicleEngine(): Unit = {
    val vertx: Vertx = Vertx.vertx()
    val vehicleVerticleCanDrive: VehicleVerticleCanDriveImpl = new VehicleVerticleCanDriveImpl(this)
    vertx.deployVerticle(vehicleVerticleCanDrive)

    val vehicleVerticleBook: VehicleVerticleBookImpl = new VehicleVerticleBookImpl(this)
    vertx.deployVerticle(vehicleVerticleBook)

    val vehicleVerticleArrivedNotify: VehicleVerticleArrivedNotifyImpl = new VehicleVerticleArrivedNotifyImpl(this)
    vertx.deployVerticle(vehicleVerticleArrivedNotify)

    val vehicleVerticleDriveCommand: VehicleVerticleDriveCommandImpl = new VehicleVerticleDriveCommandImpl(this)
    vertx.deployVerticle(vehicleVerticleDriveCommand)

    val vehicleVerticleUpdate: VehicleVerticleUpdateImpl = new VehicleVerticleUpdateImpl(this)
    vertx.deployVerticle(vehicleVerticleUpdate)

    val vehicleVerticleRegister: VehicleVerticleRegisterImpl = new VehicleVerticleRegisterImpl(this)
    vertx.deployVerticle(vehicleVerticleRegister)
  }

  private def executeBehaviour(callback:() => Unit) = callback()

  private def executeBehaviour(callback:(Position) => Unit, position: Position) = callback(position)

  private def executeBehaviour(callback:(Position, Position, Boolean) => Unit,
                               position1: Position,
                               position2: Position,
                               notRealisticVar: Boolean) = callback(position1,position2,notRealisticVar)

  def getVehicle() = vehicleGiven

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

  def getUsername(): String = username

  def setUsername(newUsername: String): Unit = {
    username = newUsername
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
  }

}
