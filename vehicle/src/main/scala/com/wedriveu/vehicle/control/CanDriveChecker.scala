package com.wedriveu.vehicle.control

import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants

/**
  * @author Michele Donati on 09/08/2017.
  */

/** This trait models the part of the system used to check if a journey is possible for the vehicle. */
trait CanDriveChecker {
  /** This method permits to check if the journey is possible given its distance and the vehicle's battery.
    *
    * @param distanceInKm This indicates the distance in Km to travel.
    * @return Return a Boolean object that is True if the journey can be done, False otherwise.
    */
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
