package com.wedriveu.vehicle.app

import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.services.shared.utilities.Position
import com.wedriveu.vehicle.control.{VehicleControl, VehicleControlImpl}

/**
  * Created by Michele on 31/07/2017.
  */
object Main extends App{
  val nVehicles: Int = 3
  val minorBound: Int = 30
  val maxBound: Int = 101
  val vehicle: String = "veicolo"
  val state: String = "available"
  val latitude: Double = 10.0
  val longitude: Double = 10.0

  var vehicles: Array[VehicleControl] = new Array[VehicleControl](nVehicles)
  var a = 0
  for (a <- 0 until vehicles.length) {
    var randomBattery: Double = ThreadLocalRandom.current().nextDouble(minorBound, maxBound)
    vehicles(a) = new VehicleControlImpl(vehicle + (a+1), state, new Position(latitude, longitude), randomBattery)
    vehicles(a).startVehicleEngine()
  }

}

