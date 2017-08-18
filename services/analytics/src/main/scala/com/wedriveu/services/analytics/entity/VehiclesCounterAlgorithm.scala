package com.wedriveu.services.analytics.entity

import com.wedriveu.services.shared.entity.AnalyticsVehicle
import com.wedriveu.shared.rabbitmq.message.VehicleCounter

/**
  * @author Stefano Bernagozzi
  */
trait VehiclesCounterAlgorithm {
  def vehicleCounter(vehicleList: java.util.List[AnalyticsVehicle]): VehicleCounter
}