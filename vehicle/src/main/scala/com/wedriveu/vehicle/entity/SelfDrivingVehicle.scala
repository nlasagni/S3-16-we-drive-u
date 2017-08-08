package com.wedriveu.vehicle.entity

import java.util.concurrent.locks.{Condition, ReentrantLock}

import com.wedriveu.vehicle.shared.VehicleConstants


/**
  * @author Michele Donati on 28/07/2017.
  */

class SelfDrivingVehicle(var plate: String,
                         private var state: String,
                         var position: Position,
                         var battery: Double,
                         var speed: Double){

  val mutex: ReentrantLock = new ReentrantLock()
  val barrier: Condition = mutex.newCondition()

  def getState(): String = {
    try {
      mutex.lock()
      return state
    }
    finally {
      barrier.signalAll()
      mutex.unlock()
    }
  }

  def setState(newState: String): Unit = {
    try {
      mutex.lock()
      state = newState
    }
    finally {
      barrier.signalAll()
      mutex.unlock()
    }
  }

  def checkVehicleIsStolenAndSetBroken(): Boolean = {
    mutex.lock()
    if(state.equals(VehicleConstants.stateStolen)){
      barrier.signalAll()
      mutex.unlock()
      return false
    }
    else {
      state = VehicleConstants.stateBroken
      barrier.signalAll()
      mutex.unlock()
      return true
    }
  }

  def checkVehicleIsBrokenOrStolenAndSetRecharging(): Boolean = {
    mutex.lock()
    if((state.equals(VehicleConstants.stateBroken)) || (state.equals(VehicleConstants.stateStolen))) {
      barrier.signalAll()
      mutex.unlock()
      return false
    }
    else {
      state = VehicleConstants.stateRecharging
      barrier.signalAll()
      mutex.unlock()
      return true
    }
  }

}




