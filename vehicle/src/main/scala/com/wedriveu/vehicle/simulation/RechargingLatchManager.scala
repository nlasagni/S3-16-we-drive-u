package com.wedriveu.vehicle.simulation

import java.util.concurrent.{CountDownLatch, TimeUnit}

import com.wedriveu.vehicle.entity.SelfDrivingVehicle
import io.vertx.core.Vertx

/**
  * @author Michele Donati on 07/08/2017.
  */

/** This trait models the manager for the Latch Thread. */
trait RechargingLatchManager {
  /** This method permits to start the Latch Thread. */
  def startLatchedThread(): Unit
}

class RechargingLatchManagerImpl(selfDrivingVehicle: SelfDrivingVehicle, vertx: Vertx) extends RechargingLatchManager {
  val tenSecondsOfRecharge: Int = 10
  val maxTimeOfAwaiting: Long = 15L
  var countdownLatch: CountDownLatch = new CountDownLatch(tenSecondsOfRecharge)

  val rechargingLatchedThread: RechargingLatchedThread =
    new RechargingLatchedThread(selfDrivingVehicle, countdownLatch, vertx)

  override def startLatchedThread(): Unit = {
    rechargingLatchedThread.start()
    countdownLatch.await(maxTimeOfAwaiting, TimeUnit.SECONDS)
  }

}
