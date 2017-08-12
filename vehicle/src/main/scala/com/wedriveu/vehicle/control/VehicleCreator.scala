package com.wedriveu.vehicle.control

import java.util.UUID

import com.wedriveu.shared.utils.Position
import com.wedriveu.vehicle.boundary.{VehicleStopView, VehicleStopViewImpl}
import com.wedriveu.vehicle.shared.VehicleConstants

/**
  * @author Michele Donati on 02/08/2017.
  */

class VehicleCreator(speed: Double, battery: Double, doBreak: Boolean, doStolen: Boolean, vehiclesCounter: Int) {
  val randomPlate: String = randomLicensePlateGenerator()
  val initialState: String = ""
  val initialPosition: Position = new Position(VehicleConstants.initialLatitude, VehicleConstants.initialLongitude)

  val stopUi: VehicleStopView = new VehicleStopViewImpl(vehiclesCounter)
  stopUi.render()

  val newVehicle: VehicleControl =
    new VehicleControlImpl(randomPlate, initialState, initialPosition, battery, speed, stopUi, false)
  newVehicle.startVehicleEngine()
  newVehicle.subscribeToMovementAndChangePositionEvents()
  if(doBreak) {
    newVehicle.subscribeToBrokenEvents()
  }
  if(doStolen) {
    newVehicle.subscribeToStolenEvents()
  }

  private def randomLicensePlateGenerator(): String = {
    UUID.randomUUID.toString
  }

}
