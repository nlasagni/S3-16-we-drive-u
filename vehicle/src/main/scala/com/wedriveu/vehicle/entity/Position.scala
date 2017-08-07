package com.wedriveu.vehicle.entity

import com.wedriveu.vehicle.shared.VehicleConstants

/**
  * @author Michele Donati on 17/07/2017.
  */
class Position(var latitude: Double, var longitude: Double) {

  def getDistanceInKm(position: Position): Double = {
    return VehicleConstants.earthRadiusInKm *
      Math.acos(Math.sin(this.latitude) *
        Math.sin(position.latitude) +
        Math.cos(this.latitude) *
          Math.cos(position.latitude) *
          Math.cos(this.longitude - position.longitude))
  }

}