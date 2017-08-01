package com.wedriveu.vehicle.entity

/**
  * Created by Michele on 17/07/2017.
  */
case class Position(latitude: Double, longitude: Double) {

  def getDistanceInKm(position: Position): Double = {
    val earthRadius = 6372.795477598
    earthRadius *
      Math.acos(Math.sin(this.latitude) *
        Math.sin(position.latitude) +
        Math.cos(this.latitude) *
          Math.cos(position.latitude) *
          Math.cos(this.longitude - position.longitude))
  }

}