package com.wedriveu.vehicle.entity


import com.wedriveu.services.shared.utilities.{Constants, Position}

/**
  * Created by Michele on 28/07/2017.
  */
case class SelfDrivingVehicle(plate: String, var state: String, var position: Position, var battery: Double) {
  val speed: Double = Constants.VEHICLE_AVERAGE_SPEED
}
