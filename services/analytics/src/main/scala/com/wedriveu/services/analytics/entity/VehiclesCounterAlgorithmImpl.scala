package com.wedriveu.services.analytics.entity
import com.wedriveu.services.shared.entity.VehicleCounter
import collection.mutable._
import collection.JavaConverters._


/**
  * @author Stefano Bernagozzi
  */


 class VehiclesCounterAlgorithmImpl extends VehiclesCounterAlgorithm{
    override def vehicleCounter(vehicleList: java.util.List[AnalyticsVehicle]): VehicleCounter = {
      var vehicleCounterObject = new VehicleCounter
      vehicleCounterRecursive(vehicleList.asScala.toList, vehicleCounterObject)
      vehicleCounterObject
    }

    @annotation.tailrec
    final def vehicleCounterRecursive(vehicleList: List[AnalyticsVehicle], counter: VehicleCounter): Boolean = vehicleList match {
      case x :: tail =>
        x.getStatus match {
          case "available" => counter.increaseAvailable()
          case "broken" => counter.increaseBroken()
          case "booked" => counter.increaseBooked()
          case "stolen" => counter.increaseStolen()
          case "recharging" => counter.increaseRecharging()
        }
        vehicleCounterRecursive(tail, counter)
      case Nil => true

    }
  }

