package com.wedriveu.vehicle.control

import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.vehicle.boundary.VehicleStopViewImpl
import com.wedriveu.vehicle.entity.Position
import com.wedriveu.vehicle.shared.VehicleConstants
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Created by Michele on 31/07/2017.
  */
class VehicleBehavioursTest extends FunSuite with BeforeAndAfterEach {
  val speedTest: Double = 50.0
  val licenseFirstTest: String = "veicolo1"
  val stateFirstTest: String = "available"
  val licenseSecondTest: String = "veicolo"
  val stateSecondTest: String = "available"
  val latitude: Double = 44.1454528
  val longitude: Double = 12.2474513
  val nVehicles: Int = 3
  val minorBound: Int = 30
  val maxBound: Int = 101

  //These two variables indicate the bounds for the random latitude and longitude calculation. The variation estimated
  //for containing the distance in 50 - 100 kilometers is like this (from 10.0,10.0 to 11.0,11.0 results in thousands
  //of kilometers).
  val minorBoundPositionLat: Double = 44.1343417
  val maxBoundPositionLat: Double = 44.1565639
  val minorBoundPositionLon: Double = 12.2363402
  val maxBoundPositionLon: Double = 12.2585623

  var randomLatitudeUser: Double = .0
  var randomLongitudeUser: Double = .0
  var randomLatitudeDestination: Double = .0
  var randomLongitudeDestination: Double = .0

  //The debugging variable of the VehicleControl works like this: if it is set True, it will not do any recharge. If it
  // set False, the vehicle will works as inteded.
  override def beforeEach() {}

  test("The vehicle position, after a random destination position input, should be equals to it") {
    val vehicleControl: VehicleControl =
      new VehicleControlImpl(licenseFirstTest,
        stateFirstTest,
        new Position(latitude, longitude),
        VehicleConstants.maxBatteryValue,
        speedTest,
        new VehicleStopViewImpl(1),
        true)
    val vehicleBehaviours = new VehicleBehavioursImpl(vehicleControl.getVehicle(), new VehicleStopViewImpl(1), true)
    randomLatitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat)
    randomLongitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon)
    vehicleBehaviours.movementAndPositionChange(new Position(randomLatitudeDestination,randomLongitudeDestination))
    while(!(vehicleBehaviours.getDebuggingVar())){}
      assert(vehicleControl.getVehicle().position.latitude == randomLatitudeDestination
        && vehicleControl.getVehicle().position.longitude == randomLongitudeDestination
        && vehicleControl.getVehicle().battery < VehicleConstants.maxBatteryValue)
  }

  test("The vehicle battery, after 10 seconds of recharging must be 100.0") {
    val vehicleControl: VehicleControl =
      new VehicleControlImpl(licenseFirstTest,
        stateFirstTest,
        new Position(latitude, longitude),
        VehicleConstants.maxBatteryValue,
        speedTest,
        new VehicleStopViewImpl(1),
        false)
    val vehicleBehaviours = new VehicleBehavioursImpl(vehicleControl.getVehicle(), new VehicleStopViewImpl(1), true)
    randomLatitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat)
    randomLongitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon)
    vehicleBehaviours.movementAndPositionChange(new Position(randomLatitudeDestination,randomLongitudeDestination))
    vehicleBehaviours.goToRecharge()
    while(!(vehicleBehaviours.getDebuggingVar())){}
    assert(vehicleControl.getVehicle().battery == VehicleConstants.maxBatteryValue)
    assert(vehicleControl.getVehicle().getSate().equals(VehicleConstants.stateAvailable))
  }

  test("The vehicle state should be broken when the broken event arrives") {
    val vehicleControl: VehicleControl =
      new VehicleControlImpl(licenseFirstTest,
        stateFirstTest,
        new Position(latitude, longitude),
        VehicleConstants.maxBatteryValue,
        speedTest,
        new VehicleStopViewImpl(1),
        false)
    val vehicleBehaviours = new VehicleBehavioursImpl(vehicleControl.getVehicle(), new VehicleStopViewImpl(1), true)
    vehicleControl.subscribeToBrokenEvents()
    Thread.sleep(17000)
    assert(vehicleControl.getVehicle().getSate().equals(VehicleConstants.stateBroken))
  }

  test("The vehicle state should be stolen when the stolen event arrives") {
    val vehicleControl: VehicleControl =
      new VehicleControlImpl(licenseFirstTest,
        stateFirstTest,
        new Position(latitude, longitude),
        VehicleConstants.maxBatteryValue,
        speedTest,
        new VehicleStopViewImpl(1),
        false)
    val vehicleBehaviours = new VehicleBehavioursImpl(vehicleControl.getVehicle(), new VehicleStopViewImpl(1), true)
    vehicleControl.subscribeToStolenEvents()
    Thread.sleep(18000)
    assert(vehicleControl.getVehicle().getSate().equals(VehicleConstants.stateStolen))
  }
}
