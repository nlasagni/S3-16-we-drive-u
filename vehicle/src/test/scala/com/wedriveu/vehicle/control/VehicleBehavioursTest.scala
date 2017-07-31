package com.wedriveu.vehicle.control

import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.services.shared.utilities.Position
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Created by Michele on 31/07/2017.
  */
class VehicleBehavioursTest extends FunSuite with BeforeAndAfterEach {
  var selfDrivingVehicle: SelfDrivingVehicle = null
  var battery: Double = 100.0

  override def beforeEach() {
    selfDrivingVehicle = new SelfDrivingVehicle("veicolo1", "available", new Position(10.0, 10.0), battery)
  }

  test("The battery after 3 seconds should be less than 100.0") {
    selfDrivingVehicle.startVehicleControl()
    Thread.sleep(3000)
    assert(selfDrivingVehicle.getBattery() < battery && selfDrivingVehicle.getBattery() != 100.0)
  }

  test("The battery af all vehicles, after 3 seconds, should be less than intial values") {
    var nVehicles: Int = 3
    var vehicles: Array[SelfDrivingVehicle] = new Array[SelfDrivingVehicle](nVehicles)
    var a = 0
    for (a <- 0 until vehicles.length) {
      var randomBattery: Double = ThreadLocalRandom.current().nextDouble(30, 100 + 1)
      vehicles(a) = new SelfDrivingVehicle("veicolo" + (a+1), "available", new Position(10.0, 10.0), randomBattery)
      vehicles(a).startVehicleControl()
      Thread.sleep(3000)
      assert(vehicles(a).getBattery() < randomBattery)
    }
  }

}
