package com.wedriveu.vehicle.control

import com.wedriveu.services.shared.utilities.Log
import com.wedriveu.vehicle.entity.SelfDrivingVehicle

/**
  * Created by Michele on 31/07/2017.
  */

/** This is the vehicle behaviours models capturing different events. */
trait VehicleBehaviours {
  /** This models the behaviour of the vehicle's battery draining. */
  def drainBattery() : Unit
}

class VehicleBehavioursImpl(vehicle: SelfDrivingVehicle) extends VehicleBehaviours {
   var notRecharging: Boolean = true
   val zeroBattery: Double = 0.0
   val batteryThreshold: Double = 20.0
   val stateRecharging: String = "recharging"
   val batteryToConsume: Double = 1.0

   def drainBattery() = {
    if(vehicle.battery <= zeroBattery){
      Log.log(vehicle.battery.toString)
    }
    else{
      if((vehicle.battery-batteryToConsume) < zeroBattery) {
        vehicle.battery = zeroBattery
      }
      else {
        // Depends on vehicle speed
        vehicle.battery -= batteryToConsume
      }
      if(vehicle.battery <= batteryThreshold && notRecharging) {
        vehicle.state = stateRecharging
        //This will be set to True when the vehicle recharges
        notRecharging = false
        Log.log("Vehicle " + vehicle.plate + " state changed to: " + vehicle.state)
      }
      Log.log(vehicle.battery.toString)
    }
  }
}
