package com.wedriveu.services.analytics.entity


import com.wedriveu.services.shared.model.AnalyticsVehicle
import com.wedriveu.shared.rabbitmq.message.VehicleCounter
import com.wedriveu.shared.util.Constants

import scala.collection.JavaConverters._


/**
  * @author Stefano Bernagozzi
  */


class VehiclesCounterAlgorithmImpl extends VehiclesCounterAlgorithm {
  override def vehicleCounter(vehicleList: java.util.List[AnalyticsVehicle]): VehicleCounter = {
    var vehicleCounterObject = new VehicleCounter
    vehicleCounterRecursive(vehicleList.asScala.toList, vehicleCounterObject)
    vehicleCounterObject
  }

  @annotation.tailrec
  private[this] final def vehicleCounterRecursive(vehicleList: List[AnalyticsVehicle], counter: VehicleCounter): Boolean = vehicleList match {
    case x :: tail =>
      x.getStatus match {
        case Constants.Vehicle.STATUS_AVAILABLE => counter.increaseAvailable()
        case Constants.Vehicle.STATUS_BROKEN_STOLEN => counter.increaseBroken()
        case Constants.Vehicle.STATUS_BOOKED => counter.increaseBooked()
        case Constants.Vehicle.STATUS_NETWORK_ISSUES => counter.increaseNetworkIssues()
        case Constants.Vehicle.STATUS_RECHARGING => counter.increaseRecharging()
        case _ =>
      }
      vehicleCounterRecursive(tail, counter)
    case Nil => true

  }
}

