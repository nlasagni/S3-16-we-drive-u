package com.wedriveu.vehicle.control

import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.services.shared.utilities.{Log, Position}
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
  val timeToSleep: Int = 5000
  val nVehicles: Int = 3
  val minorBound: Int = 30
  val maxBound: Int = 101

  val minorBoundPosition: Double = 9.9999979899999
  val maxBoundPosition: Double = 10.01111111111111
  var randomLatitudeUser: Double = .0
  var randomLongitudeUser: Double = .0
  var randomLatitudeDestination: Double = .0
  var randomLongitudeDestination: Double = .0

  override def beforeEach() {
    vehicleControl =
      new VehicleControlImpl(licenseFirstTest, stateFirstTest, new Position(latitude, longitude), maxBattery)
  }

  test("The battery after 3 seconds should be less than 100.0") {
    vehicleControl.subscribeToDrainBatteryEvents()
    Thread.sleep(timeToSleep)
    assert(vehicleControl.getVehicle().battery < maxBattery)
  }

  test("The vehicle position, after a random user position input, should be equals to it." +
    "After that, the vehicle position, after a random destination position input, should be equals to id") {
    randomLatitudeUser = ThreadLocalRandom.current().nextDouble(minorBoundPosition, maxBoundPosition)
    randomLongitudeUser = ThreadLocalRandom.current().nextDouble(minorBoundPosition, maxBoundPosition)
    randomLatitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPosition, maxBoundPosition)
    randomLongitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPosition, maxBoundPosition)
    vehicleControl.changePositionUponBooking(new Position(randomLatitudeUser, randomLongitudeUser),
      new Position(randomLatitudeDestination,randomLongitudeDestination ))
    Thread.sleep(timeToSleep)
    assert(vehicleControl.getVehicle().position.getLatitude == randomLatitudeDestination)
    assert(vehicleControl.getVehicle().position.getLongitude == randomLongitudeDestination)
  }

  test("The vehicle position, after a random destination position input, should be equals to it") {
    randomLatitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPosition, maxBoundPosition)
    randomLongitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPosition, maxBoundPosition)
    var vehicleBehaviours: VehicleBehaviours = new VehicleBehavioursImpl(vehicleControl.getVehicle())
    vehicleBehaviours.movementAndPositionChange(new Position(randomLatitudeDestination,randomLongitudeDestination))
    Thread.sleep(timeToSleep)
    assert(vehicleControl.getVehicle().position.getLatitude == randomLatitudeDestination)
    assert(vehicleControl.getVehicle().position.getLongitude == randomLongitudeDestination)
  }
}
