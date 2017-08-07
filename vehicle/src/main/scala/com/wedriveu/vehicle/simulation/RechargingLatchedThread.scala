package com.wedriveu.vehicle.simulation

import java.util.concurrent.{CountDownLatch, TimeUnit}

import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants

/**
  * @author Michele Donati on 07/08/2017.
  */

/** This trait models the CountDownLatch Thread that is used for simulating the recharging process of the vehicle,
  * for about 10 seconds.
  */
trait RechargingLatched {
  /** This method permits to run the CoundDownLatch Thread.
    *
    * @param vehicle Indicates the vehicle associated.
    */
  def performRechargeSimulatorTask(vehicle: SelfDrivingVehicle): Unit
}

class RechargingLatchedThread() extends Thread with RechargingLatched {
  val oneSecondInMillis: Int = 1000
  val tenSecondsOfRecharge: Int = 10
  val maxTimeOfAwaiting: Long = 15L

  var countdownLatch: CountDownLatch = null
  var vehicle: SelfDrivingVehicle = null

  override def run(): Unit = {
    for(i <- 0 until 10 by 1){
      try {
        if(!vehicle.getSate().equals(VehicleConstants.stateBroken)) {
          Thread.sleep(oneSecondInMillis)
        }
        else {
          return
        }
      }
      finally {
        countdownLatch.countDown()
      }
    }
    vehicle.battery = VehicleConstants.maxBatteryValue
    vehicle.setState(VehicleConstants.stateAvailable)
  }

  override def performRechargeSimulatorTask(vehicle: SelfDrivingVehicle): Unit = {
    this.vehicle = vehicle
    countdownLatch = new CountDownLatch(tenSecondsOfRecharge)
    this.start()
    countdownLatch.await(maxTimeOfAwaiting, TimeUnit.SECONDS)
  }

}

