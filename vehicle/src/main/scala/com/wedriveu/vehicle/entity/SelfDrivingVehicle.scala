package com.wedriveu.vehicle.entity


import com.wedriveu.services.shared.utilities.Position
import com.wedriveu.vehicle.control.VehicleControl

/**
  * Created by Michele on 28/07/2017.
  */
class SelfDrivingVehicle(plate: String, stateGiven: String, positionGiven: Position, batteryGiven: Double) {
   private var licensePlate: String = plate
   private var battery: Double = batteryGiven
   private var state: String = stateGiven
   private var position: Position = positionGiven

  def getLicensePlate(): String = licensePlate

  def setLicensePlate(licensePlate : String) = {
    this.licensePlate = licensePlate
  }

  def getState(): String = state

  def setState(state: String) = {
    this.state = state
  }

  def getPosition(): Position = position

  def setPosition(position: Position) = {
    this.position = position
  }

  def getBattery(): Double = battery

  def setBattery(battery: Double) = {
    this.battery = battery
  }

  def startVehicleControl() = {
    val vehicleControl: VehicleControl = new VehicleControl(this)
    vehicleControl.startVehicleEngine()
  }

}
