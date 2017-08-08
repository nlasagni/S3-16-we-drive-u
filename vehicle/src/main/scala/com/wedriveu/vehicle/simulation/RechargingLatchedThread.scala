package com.wedriveu.vehicle.simulation

import java.util.concurrent.{CountDownLatch, TimeUnit}

import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants

/**
  * @author Michele Donati on 07/08/2017.
  */

class RechargingLatchedThread(selfDrivingVehicle: SelfDrivingVehicle, countdownLatch: CountDownLatch) extends Thread {
  val oneSecondInMillis: Int = 1000

  override def run(): Unit = {
    for(i <- 0 until 10 by 1){
      try {
        if(!selfDrivingVehicle.getSate().equals(VehicleConstants.stateBroken)) {
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
    selfDrivingVehicle.battery = VehicleConstants.maxBatteryValue
    selfDrivingVehicle.setState(VehicleConstants.stateAvailable)
  }

}

