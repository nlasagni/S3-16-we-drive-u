package com.wedriveu.vehicle.control

import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants

/**
  * Created by Michele on 09/08/2017.
  */
trait CanDriveChecker {

  def checkJourney(distanceInKm: Double): Boolean

}

class CanDriveCheckerImpl(vehicleGiven: SelfDrivingVehicle) extends CanDriveChecker {

  override def checkJourney(distanceInKm: Double): Boolean = {
    estimateBatteryConsumption(distanceInKm)
  }

  private def estimateBatteryConsumption(kilometersToDo: Double): Boolean = {
    ((kilometersToDo + VehicleConstants.MAXIMUM_DISTANCE_TO_RECHARGE)
      / VehicleConstants.ESTIMATED_KILOMETERS_PER_PERCENTAGE) < vehicleGiven.battery
  }

}
