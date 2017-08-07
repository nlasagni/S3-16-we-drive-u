package com.wedriveu.vehicle.simulation

import com.wedriveu.vehicle.entity.SelfDrivingVehicle

/**
  * @author Michele Donati on 07/08/2017.
  */

/** This trait models the manager for the Latch Thread. */
trait RechargingLatchManager {
  /** This method permits to start the Latch Thread. */
  def startLatchedThread(): Unit
}
class RechargingLatchManagerImpl(selfDrivingVehicle: SelfDrivingVehicle) extends RechargingLatchManager {

  val rechargingLatchedThread: RechargingLatchedThread = new RechargingLatchedThread
  override def startLatchedThread(): Unit = {
      rechargingLatchedThread.performRechargeSimulatorTask(selfDrivingVehicle)
  }

}
