package com.wedriveu.vehicle.control

import com.rabbitmq.client._
import com.wedriveu.services.shared.utilities.{Constants, Log, Position}
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.simulation.{VehicleEventsObservables, VehicleEventsObservablesImpl}

/**
  * Created by Michele on 28/07/2017.
  */

/** This models the control part of the vehicle. */
trait VehicleControl {
  /** This starts the engine of the vehicle, initializing the communication and rxScala infrastructures. */
  def startVehicleEngine(): Unit

  /** This is usefull to get the vehicle for retrieve vehicle informations.
    *
    * @return Returns the instance of the vehicle.
    */
  def getVehicle(): SelfDrivingVehicle
}
class VehicleControlImpl(license: String, state: String, position: Position, battery: Double) extends VehicleControl {
  val vehicleGiven : SelfDrivingVehicle = new SelfDrivingVehicle(license, state, position, battery)
  val vehicleEventsObservables: VehicleEventsObservables = new VehicleEventsObservablesImpl
  val vehicleBehaviours: VehicleBehaviours = new VehicleBehavioursImpl(vehicleGiven)
  val received: String = " [x] Received '"
  val awaiting: String = " [x] Awaiting requests"
  var kilometersToDo: Double = .0
  var connection: Connection = null
  var channel: Channel = null

  def startVehicleEngine(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        configureRabbitMq()
        try {
          vehicleEventsObservables.batteryDrainObservable().subscribe(event => {
            //Scala kills strategy pattern, i can simply pass a function as a parameter
            executeBehaviour(vehicleBehaviours.drainBattery)
          })
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
    factory.setHost(Constants.SERVER_HOST)
    factory.setPassword(Constants.SERVER_PASSWORD)
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

  def getVehicle() = vehicleGiven
}
