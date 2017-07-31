package com.wedriveu.vehicle.control

import com.rabbitmq.client._
import com.wedriveu.services.shared.utilities.{Constants, Log}
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.simulation.{VehicleEventsObservables, VehicleEventsObservablesImpl}

/**
  * Created by Michele on 28/07/2017.
  */
class VehicleControl(vehicleGiven: SelfDrivingVehicle) {
  var kilometersToDo: Double = .0
  val vehicleEventsObservables: VehicleEventsObservables = new VehicleEventsObservablesImpl
  var connection: Connection = null
  var channel: Channel = null
  val vehicleBehaviours: VehicleBehaviours = new VehicleBehaviours(vehicleGiven)

  def startVehicleEngine(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        configureRabbitMq()
        try {
          vehicleEventsObservables.batteryDrainObservable().subscribe(event => {
            executeBehaviour(vehicleBehaviours.drainBattery) //Scala kills strategy pattern, i can simply pass a
          })                                                 // function as a parameter
          while (true) {
            val vehicle: Consumer = new DefaultConsumer(channel) {
              override def handleDelivery(consumerTag: String,
                                          envelope: Envelope,
                                          properties: AMQP.BasicProperties,
                                          body: Array[Byte]): Unit = {
                var response: String = new String(body, Constants.UTF)
                kilometersToDo = response.toDouble
                Log.log(" [x] Received '" + response + "'")
                channel.basicPublish("",
                  vehicleGiven.getLicensePlate() + Constants.VEHICLE_TO_SERVICE,
                  null,
                  (estimateBatteryConsumption(kilometersToDo)).toString.getBytes())
              }
            }
            channel.basicConsume(vehicleGiven.getLicensePlate(), true, vehicle)
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
      / Constants.ESTIMATED_KILOMETERS_PER_PERCENTAGE) < vehicleGiven.getBattery()
  }

  private def configureRabbitMq(): Unit = {
    val factory: ConnectionFactory = new ConnectionFactory
    factory.setHost(Constants.SERVER_HOST)
    factory.setPassword(Constants.SERVER_PASSWORD)
    connection = factory.newConnection
    channel = connection.createChannel
    channel.queueDeclare(vehicleGiven.getLicensePlate(), false, false, false, null)
    channel.queueDeclare(vehicleGiven.getLicensePlate() + Constants.VEHICLE_TO_SERVICE, false, false, false, null)
    channel.basicQos(10)
    Log.log(" [x] Awaiting requests")
  }

  private def executeBehaviour(callback:() => Unit) = callback()

}
