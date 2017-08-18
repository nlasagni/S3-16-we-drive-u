package com.wedriveu.services.analytics.entity


import com.wedriveu.services.shared.entity.AnalyticsVehicle
import com.wedriveu.shared.rabbitmq.message.VehicleCounter
import com.wedriveu.services.shared.entity.Vehicle

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
        case Vehicle.STATUS_AVAILABLE => counter.increaseAvailable()
        case Vehicle.STATUS_BROKEN_STOLEN => counter.increaseBroken()
        case Vehicle.STATUS_BOOKED => counter.increaseBooked()
        case Vehicle.STATUS_NETWORK_ISSUES => counter.increaseNetworkIssues()
        case Vehicle.STATUS_RECHARGING => counter.increaseRecharging()
      }
      vehicleCounterRecursive(tail, counter)
    case Nil => true

  }
}

