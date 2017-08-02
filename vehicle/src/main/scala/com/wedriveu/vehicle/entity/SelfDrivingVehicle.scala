package com.wedriveu.vehicle.entity

/**
  * Created by Michele on 28/07/2017.
  */
class SelfDrivingVehicle(var plate: String, var state: String, var position: Position, var battery: Double) {
  //this is an average speed in Km/h
  val speed: Double = 50.0
}
