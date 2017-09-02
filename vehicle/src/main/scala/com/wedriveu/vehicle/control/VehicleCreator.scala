package com.wedriveu.vehicle.control

import java.util.UUID

import com.wedriveu.shared.util.Constants
import com.wedriveu.vehicle.boundary.{VehicleStopView, VehicleStopViewImpl}
import io.vertx.core.Vertx

/**
  * @author Michele Donati on 02/08/2017.
  */

class VehicleCreator(vertx: Vertx,
  speed: Double,
  battery: Double,
  doBreak: Boolean,
  vehiclesCounter: Int,
  indexForImages: Int) extends Thread {

  override def run(): Unit = {
    val randomPlate: String = randomLicensePlateGenerator()
    val initialState: String = ""
    val imageList: Array[String] = Array(
      "http://static.allaguida.it/r/843x0/www.allaguida.it/img/Fiat-500.jpg",
      "https://www.cinquecosebelle.it/wp-content/uploads/2016/09/macchine-7-posti-fiat-freemont.jpg",
      "http://static.allaguida.it/845X0/www/allaguida/it/img/macchine-usate-bmw.jpg",
      "https://www.cinquecosebelle.it/wp-content/uploads/2016/09/macchine-7-posti-dacia-lodgy.jpg",
      "https://www.mercedes-benz.it/content/media_library/hq/hq_mpc_reference_site/passenger_cars_ng/new_cars/models" +
          "/cla-class/c117/start/mercedes-benz-cla-c117_start_1000x470_02-2016_jpg.object-Single-MEDIA.tmp" +
          "/mercedes-benz-cla_c117_start_1000x470_02-2016.jpg",
      "http://www.motorionline.com/wp-content/uploads/2016/02/Mercedes-C43-AMG-Coupe-1-1024x611.jpg"
    )

    val descriptions: Array[String] = Array(
      "Fiat 500, colore bianco, tre porte, quattro sedili, motore elettrico.",
      "Fiat Freemont, colore rosso, cinque porte, sette sedili, motore elettrico",
      "BMW 530D Touring, colore nero, cinque porte, cinque sedili, motore elettrico",
      "Dacia Lodgy, colore sabbia, cinque porte, sette sedili, motore elettrico",
      "Mercedes CLA, colore grigio argentato, cinque porte, cinque sedili, motore elettrico",
      "Mercedes C43 AMG Coupe, colore bianco, tre porte, due sedili, motore elettrico"
    )

    val stopUi: VehicleStopView = new VehicleStopViewImpl(vertx, vehiclesCounter)
    stopUi.render()

    val newVehicle: VehicleControl =
      new VehicleControlImpl(vertx,
        imageList(indexForImages),
        descriptions(indexForImages),
        randomPlate,
        initialState,
        Constants.HEAD_QUARTER,
        battery,
        speed,
        stopUi,
        false)

    newVehicle.startVehicleEngine()
    if (doBreak) {
      newVehicle.subscribeToBrokenEvents()
    }
  }

  private def randomLicensePlateGenerator(): String = {
    UUID.randomUUID.toString
  }

}
