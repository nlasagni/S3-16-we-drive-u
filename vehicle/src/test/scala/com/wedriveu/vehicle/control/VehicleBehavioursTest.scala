package com.wedriveu.vehicle.control

import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.shared.util.{Constants, Position}
import com.wedriveu.vehicle.boundary.VehicleStopViewImpl
import com.wedriveu.vehicle.shared.VehicleConstants
import io.vertx.core.Vertx
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * @author Michele Donati on 31/07/2017.
  */

class VehicleBehavioursTest extends FunSuite with BeforeAndAfterEach {
  val speedTest: Double = 50.0
  val licenseFirstTest: String = "veicolo1"
  val stateFirstTest: String = "available"
  val licenseSecondTest: String = "veicolo"
  val stateSecondTest: String = "available"
  val nVehicles: Int = 3
  val minorBound: Int = 30
  val maxBound: Int = 101
  val vertx: Vertx = Vertx.vertx()

  //These two variables indicate the bounds for the random latitude and longitude calculation. The variation estimated
  //for containing the distance in 50 - 100 kilometers is like this (from 10.0,10.0 to 11.0,11.0 results in thousands
  //of kilometers).
  val minorBoundPositionLat: Double = 44.1343417
  val maxBoundPositionLat: Double = 44.1565639
  val minorBoundPositionLon: Double = 12.2363402
  val maxBoundPositionLon: Double = 12.2585623
  val timeToSleepTest1 = 5000
  val timeToSleepTest2 = 11000
  val timeToSleepForBrokenEvent = 19000
  val timeToSleepForStolenEvent = 20000

  var randomLatitudeUser: Double = .0
  var randomLongitudeUser: Double = .0
  var randomLatitudeDestination: Double = .0
  var randomLongitudeDestination: Double = .0

  //The debugging variable of the VehicleControl works like this: if it is set True, it will not do any recharge. If it
  // set False, the vehicle will works as inteded.
  override def beforeEach() {}

  test("The vehicle position, after a random destination position input, should be equals to it") {
    val vehicleControl: VehicleControl = createVehicleControl(true)
    val vehicleBehaviours = createVehicleBehaviour(vehicleControl, true)
    randomLatitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat)
    randomLongitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon)
    vehicleBehaviours.movementAndPositionChange(new Position(randomLatitudeDestination, randomLongitudeDestination))
    Thread.sleep(timeToSleepTest1)
    assert(vehicleControl.getVehicle().position.getLatitude == randomLatitudeDestination
        && vehicleControl.getVehicle().position.getLongitude == randomLongitudeDestination
        && vehicleControl.getVehicle().battery < VehicleConstants.maxBatteryValue)
  }

  test("The vehicle battery, after 10 seconds of recharging must be 100.0") {
    val vehicleControl: VehicleControl = createVehicleControl(false)
    val vehicleBehaviours = createVehicleBehaviour(vehicleControl, debugVar = true)
    randomLatitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat)
    randomLongitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon)
    vehicleBehaviours.movementAndPositionChange(new Position(randomLatitudeDestination, randomLongitudeDestination))
    vehicleBehaviours.goToRecharge()
    Thread.sleep(timeToSleepTest2)
    assert(vehicleControl.getVehicle().battery == VehicleConstants.maxBatteryValue)
    assert(vehicleControl.getVehicle().getState().equals(Constants.Vehicle.STATUS_AVAILABLE))
  }

  test("The vehicle state should be broken when the broken event arrives") {
    val vehicleControl: VehicleControl = createVehicleControl(false)
    vehicleControl.subscribeToBrokenEvents()
    Thread.sleep(timeToSleepForBrokenEvent)
    assert(vehicleControl.getVehicle().getState().equals(Constants.Vehicle.STATUS_BROKEN_STOLEN))
  }

  test("The vehicle state should be stolen when the stolen event arrives") {
    val vehicleControl: VehicleControl = createVehicleControl(false)
    vehicleControl.subscribeToStolenEvents()
    Thread.sleep(timeToSleepForStolenEvent)
    assert(vehicleControl.getVehicle().getState().equals(Constants.Vehicle.STATUS_BROKEN_STOLEN))
  }

  private def createVehicleControl(debugVar: Boolean): VehicleControl = {
    new VehicleControlImpl(vertx,
      "",
      "",
      licenseFirstTest,
      stateFirstTest,
      Constants.HEAD_QUARTER,
      VehicleConstants.maxBatteryValue,
      speedTest,
      new VehicleStopViewImpl(vertx, 1),
      debugVar)
  }

  private def createVehicleBehaviour(vehicleControl: VehicleControl, debugVar: Boolean): VehicleBehaviours = {
    new VehicleBehavioursImpl(vehicleControl, vertx, vehicleControl.getVehicle(), new VehicleStopViewImpl(vertx, 1), debugVar)
  }

}
