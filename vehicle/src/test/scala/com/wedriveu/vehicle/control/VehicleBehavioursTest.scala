package com.wedriveu.vehicle.control

import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.services.shared.utilities.Position
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Created by Michele on 31/07/2017.
  */
class VehicleBehavioursTest extends FunSuite with BeforeAndAfterEach {
  var vehicleControl: VehicleControl = null
  val maxBattery: Double = 100.0
  val licenseFirstTest: String = "veicolo1"
  val stateFirstTest: String = "available"
  val licenseSecondTest: String = "veicolo"
  val stateSecondTest: String = "available"
  val latitude: Double = 10.0
  val longitude: Double = 10.0
  val timeToSleep: Int = 3000
  val nVehicles: Int = 3
  val minorBound: Int = 30
  val maxBound: Int = 101

  override def beforeEach() {
    vehicleControl =
      new VehicleControlImpl(licenseFirstTest, stateFirstTest, new Position(latitude, longitude), maxBattery)
  }

  test("The battery after 3 seconds should be less than 100.0") {
    vehicleControl.startVehicleEngine()
    Thread.sleep(timeToSleep)
    assert(vehicleControl.getVehicle().battery < maxBattery)
  }

  test("The battery af all vehicles, after 3 seconds, should be less than intial values") {
    var vehicles: Array[VehicleControl] = new Array[VehicleControl](nVehicles)
    var a = 0
    for (a <- 0 until vehicles.length) {
      var randomBattery: Double = ThreadLocalRandom.current().nextDouble(minorBound, maxBound)
      vehicles(a) =
        new VehicleControlImpl(licenseSecondTest + (a+1),
          stateSecondTest,
          new Position(latitude, longitude),
          randomBattery)
      vehicles(a).startVehicleEngine()
      Thread.sleep(timeToSleep)
      assert(vehicles(a).getVehicle().battery < randomBattery)
    }
  }

}
