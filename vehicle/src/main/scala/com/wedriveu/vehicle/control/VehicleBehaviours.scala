package com.wedriveu.vehicle.control

import com.wedriveu.services.shared.utilities.Log
import com.wedriveu.vehicle.entity.SelfDrivingVehicle

/**
  * Created by Michele on 31/07/2017.
  */
class VehicleBehaviours (vehicle: SelfDrivingVehicle) {
   var notRecharging: Boolean = true

   def drainBattery() = {
    if(vehicle.getBattery() <= 0){
      Log.log(vehicle.getBattery().toString)
    }
    else{
      if((vehicle.getBattery()-1.0) < 0) {
        vehicle.setBattery(0.0)
      }
      else {
        vehicle.setBattery(vehicle.getBattery()-1) //Questo varia in base alla velocità di spostamento del veicolo
      }
      if(vehicle.getBattery() <= 20.0 && notRecharging) {
        vehicle.setState("recharging")
        notRecharging = false  //verrà settato a true quando verrà effettuato il recharge
        Log.log("Vehicle " + vehicle.getLicensePlate() + " state changed to: " + vehicle.getState())
      }
      Log.log(vehicle.getBattery().toString)
    }
  }
}
