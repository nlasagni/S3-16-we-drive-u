package com.wedriveu.vehicle.control

import java.math.BigInteger
import java.security.SecureRandom

import com.wedriveu.vehicle.boundary.{VehicleStopView, VehicleStopViewImpl}
import com.wedriveu.vehicle.entity.Position
import com.wedriveu.vehicle.shared.VehicleConstants

/**
  * Created by Michele on 02/08/2017.
  */
class VehicleCreator(speed: Double, battery: Double, doBreak: Boolean, doNotBreak: Boolean, vehiclesCounter: Int) {
  val randomNumber: SecureRandom = new SecureRandom()
  val randomPlate: String = randomLicensePlateGenerator()
  val initialState: String = VehicleConstants.stateAvailable
  val initialLatitude: Double = 44.1454528
  val initialLongitude: Double = 12.2474513
  val initialPosition: Position = new Position(initialLatitude, initialLongitude)
  val bitsOfPlate: Int = 128
  val integersToString: Int = 32

  val stopUi: VehicleStopView = new VehicleStopViewImpl(vehiclesCounter)
  stopUi.render()

  val newVehicle: VehicleControl =
    new VehicleControlImpl(randomPlate, initialState, initialPosition, battery, speed, stopUi)
  newVehicle.startVehicleEngine()
  newVehicle.subscribeToMovementAndChangePositionEvents()

  private def randomLicensePlateGenerator(): String = {
    new BigInteger(bitsOfPlate, randomNumber).toString(integersToString)
  }

}
