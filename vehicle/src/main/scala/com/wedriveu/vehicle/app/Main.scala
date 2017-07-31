package com.wedriveu.vehicle.app

import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.services.shared.utilities.Position
import com.wedriveu.vehicle.entity.SelfDrivingVehicle


/**
  * Created by Michele on 31/07/2017.
  */
object Main extends App{
    var nVehicles: Int = 3
    var vehicles: Array[SelfDrivingVehicle] = new Array[SelfDrivingVehicle](nVehicles)
    var a = 0
    for (a <- 0 until vehicles.length) {
      var randomBattery: Double = ThreadLocalRandom.current().nextDouble(30, 100 + 1)
      vehicles(a) = new SelfDrivingVehicle("veicolo" + (a+1), "available", new Position(10.0, 10.0), randomBattery)
      vehicles(a).startVehicleControl()
    }

}

