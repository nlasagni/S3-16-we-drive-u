package com.wedriveu.vehicle.control

import com.rabbitmq.client._
import com.wedriveu.services.shared.utilities.{Constants, Log}
import com.wedriveu.vehicle.boundary.VehicleStopView
import com.wedriveu.vehicle.entity.{Position, SelfDrivingVehicle}
import com.wedriveu.vehicle.shared.VehicleConstants
import com.wedriveu.vehicle.simulation.{VehicleEventsObservables, VehicleEventsObservablesImpl}

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
    */
  def changePositionUponBooking(userPosition: Position, destinationPosition: Position): Unit

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

  stopUi.setVehicleAssociated(this)

  def startVehicleEngine(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        configureRabbitMq()
        try {
          while (true) {
            registerConsumer()
          }
        }finally{
          if (connection != null) {
            connection.close()
          }
        }
      }
    }).start()
  }

  private def estimateBatteryConsumption(kilometersToDo: Double): Boolean = {
    ((kilometersToDo + Constants.MAXIMUM_DISTANCE_TO_RECHARGE)
      / Constants.ESTIMATED_KILOMETERS_PER_PERCENTAGE) < vehicleGiven.battery
  }

  private def configureRabbitMq(): Unit = {
    val factory: ConnectionFactory = new ConnectionFactory
    factory.setHost(Constants.RabbitMQ.Broker.HOST)
    factory.setPassword(Constants.RabbitMQ.Broker.PASSWORD)
    connection = factory.newConnection
    channel = connection.createChannel
    channel.queueDeclare(vehicleGiven.plate, false, false, false, null)
    channel.queueDeclare(vehicleGiven.plate + Constants.VEHICLE_TO_SERVICE, false, false, false, null)
    channel.basicQos(10)
    Log.log(awaiting)
  }

  private def registerConsumer(): Unit = {
    val vehicle: Consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: AMQP.BasicProperties,
                                  body: Array[Byte]): Unit = {
        var response: String = new String(body, Constants.UTF)
        kilometersToDo = response.toDouble
        Log.log(received + response + "'")
        channel.basicPublish("",
          vehicleGiven.plate + Constants.VEHICLE_TO_SERVICE,
          null,
          (estimateBatteryConsumption(kilometersToDo)).toString.getBytes())
      }
    }
    channel.basicConsume(vehicleGiven.plate, true, vehicle)
  }

  private def executeBehaviour(callback:() => Unit) = callback()

  private def executeBehaviour(callback:(Position) => Unit, position: Position) = callback(position)

  private def executeBehaviour(callback:(Position, Position) => Unit,
                               position1: Position,
                               position2: Position) = callback(position1,position2 )

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

  def changePositionUponBooking(userPosition: Position, destinationPosition: Position): Unit = {
    executeBehaviour(vehicleBehaviours.positionChangeUponBooking, userPosition, destinationPosition)
  }

}
