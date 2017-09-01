package com.wedriveu.vehicle.simulation

import java.util.concurrent.CountDownLatch

import com.wedriveu.shared.util.Constants
import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import com.wedriveu.vehicle.shared.VehicleConstants
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject

/**
  * @author Michele Donati on 07/08/2017.
  */

class RechargingLatchedThread(selfDrivingVehicle: SelfDrivingVehicle,
  countdownLatch: CountDownLatch,
  vertx: Vertx) extends Thread {
  val oneSecondInMillis: Int = 1000
  val eventBus: EventBus = vertx.eventBus()

  override def run(): Unit = {
    for (i <- 0 until 10 by 1) {
      try {
        if (!selfDrivingVehicle.getState().equals(Constants.Vehicle.STATUS_BROKEN_STOLEN)) {
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
    selfDrivingVehicle.setState(Constants.Vehicle.STATUS_AVAILABLE)
    eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, selfDrivingVehicle.plate), new JsonObject())
  }

}

