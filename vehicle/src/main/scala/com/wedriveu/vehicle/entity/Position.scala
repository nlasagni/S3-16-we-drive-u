package com.wedriveu.vehicle.entity

/**
  * Created by Michele on 17/07/2017.
  */
class Position(var latitude: Double, var longitude: Double) {

  def getDistanceInKm(position: Position): Double = {
    val earthRadius: Double = 6372.795477598
    return earthRadius *
      Math.acos(Math.sin(this.latitude) *
        Math.sin(position.latitude) +
        Math.cos(this.latitude) *
          Math.cos(position.latitude) *
          Math.cos(this.longitude - position.longitude))
  }

}