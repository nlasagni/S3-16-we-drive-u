package com.wedriveu.vehicle.entity

import java.util.concurrent.locks.{Condition, ReentrantLock}

import com.wedriveu.shared.util.Position
import com.wedriveu.vehicle.shared.VehicleConstants

import scala.beans.BeanProperty


/**
  * @author Michele Donati on 28/07/2017.
  */

class SelfDrivingVehicle(@BeanProperty var plate: String,
                         private var state: String,
                         @BeanProperty var position: Position,
                         var battery: Double,
                         var speed: Double){

  val mutex: ReentrantLock = new ReentrantLock()
  val barrier: Condition = mutex.newCondition()

  def getState(): String = {
    try {
      mutex.lock()
      state
    }
    finally {
      releaseLock()
    }
  }

  def setState(newState: String): Unit = {
    try {
      mutex.lock()
      state = newState
    }
    finally {
      releaseLock()
    }
  }

  def checkVehicleIsStolenAndSetBroken(): Boolean = {
    mutex.lock()
    if(state.equals(VehicleConstants.stateStolen)){
      releaseLock()
      false
    }
    else {
      state = VehicleConstants.stateBroken
      releaseLock()
      true
    }
  }

  def checkVehicleIsBrokenOrStolenAndSetRecharging(): Boolean = {
    mutex.lock()
    if(state.equals(VehicleConstants.stateBroken) || state.equals(VehicleConstants.stateStolen)) {
      releaseLock()
      false
    }
    else {
      state = VehicleConstants.stateRecharging
      releaseLock()
      true
    }
  }

  private def releaseLock(): Unit = {
    barrier.signalAll()
    mutex.unlock()
  }
}




