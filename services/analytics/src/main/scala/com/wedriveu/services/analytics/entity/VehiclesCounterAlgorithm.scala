package com.wedriveu.services.analytics.entity

import com.wedriveu.services.shared.entity.AnalyticsVehicle
import com.wedriveu.services.shared.model.{AnalyticsVehicle, VehicleCounter}

/**
  * @author Stefano Bernagozzi
  */
trait VehiclesCounterAlgorithm {
  def vehicleCounter(vehicleList: java.util.List[AnalyticsVehicle]): VehicleCounter
}