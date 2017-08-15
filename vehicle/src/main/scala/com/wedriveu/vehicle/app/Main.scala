package com.wedriveu.vehicle.app

import com.wedriveu.vehicle.boundary.{VehicleConfiguratorView, VehicleConfiguratorViewImpl}

/**
  * @author Michele Donati on 31/07/2017.
  */

object Main extends App {
  var vehicleConfiguratorView: VehicleConfiguratorView = new VehicleConfiguratorViewImpl
  vehicleConfiguratorView.render()

}

