package com.wedriveu.services.analytics.entity

import com.wedriveu.services.shared.model.AnalyticsVehicle
import com.wedriveu.shared.rabbitmq.message.VehicleCounter

/**
  * @author Stefano Bernagozzi
  */
/** This trait models the vehicle counter algorithm */
trait VehiclesCounterAlgorithm {
  /**
    *
    * @param vehicleList a list of vehicle objects
    *
    * @return a vehicle counter representing the vehicles included in the list
    */
  def vehicleCounter(vehicleList: java.util.List[AnalyticsVehicle]): VehicleCounter
}