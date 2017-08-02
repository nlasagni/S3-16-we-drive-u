package com.wedriveu.vehicle.entity

import java.math.BigInteger
import java.security.SecureRandom

/**
  * Created by Michele on 02/08/2017.
  */
class VehicleCreator(battery: Double, doBreak: Boolean, doNotBreak: Boolean) {
  val randomNumber: SecureRandom = new SecureRandom()
  var randomPlate: String = randomLicensePlateGenerator()


  private def randomLicensePlateGenerator(): String ={
    new BigInteger(130, randomNumber).toString(32)
  }

}
