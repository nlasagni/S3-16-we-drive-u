package com.wedriveu.services.analytics.entity
import com.wedriveu.services.shared.entity.VehicleCounter
import collection.mutable._
import collection.JavaConverters._


/**
  * @author Stefano Bernagozzi
  */

//trait VehiclesCounterAlgorithm {
//  def vehicleCounter(vehicleList: java.util.List[AnalyticsVehicle]): VehicleCounter
//}
 class VehiclesCounterAlgorithm {
  //object VehiclesCounterAlgorithmImpl { //extends VehiclesCounterAlgorithm {
    def vehicleCounter(vehicleList: java.util.List[AnalyticsVehicle]): VehicleCounter = {
      var vehicleCounterObject = new VehicleCounter
      vehicleCounterRecursive(vehicleList.asScala.toList, vehicleCounterObject)
      vehicleCounterObject
    }

    def vehicleCounterRecursive(vehicleList: List[AnalyticsVehicle], counter: VehicleCounter): Boolean = vehicleList match {
      case Nil => true
      case x :: tail =>
        x.getStatus match {
          case "available" => counter.increaseAvailable()
          case "broken" => counter.increaseBroken()
          case "booked" => counter.increaseBooked()
          case "stolen" => counter.increaseStolen()
          case "recharging" => counter.increaseRecharging()
        }
        vehicleCounterRecursive(tail, counter)
    }
  }

