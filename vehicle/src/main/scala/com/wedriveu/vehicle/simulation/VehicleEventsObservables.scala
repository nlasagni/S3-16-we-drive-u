package com.wedriveu.vehicle.simulation

import java.math.RoundingMode

import rx.lang.scala.Observable
import java.util.concurrent.ThreadLocalRandom
import java.text.DecimalFormat

import com.wedriveu.services.shared.utilities.Log
import com.wedriveu.vehicle.entity.Position

/**
  * @author Michele Donati on 28/07/2017.
  */

/** This models the Observables that generates the events captured by vehicles. */
trait VehicleEventsObservables {
  /** This is the Observable for the event: movement and position change.
    *
    * @return Return the position for the vehicle, to reach.
    */
  def movementAndChangePositionObservable(): Observable[Position]

}

class VehicleEventsObservablesImpl extends VehicleEventsObservables {

  //These two variables indicate the bounds for the random latitude and longitude calculation. The variation estimated
  //for containing the distance in 10 - 100 kilometers is like this (from 10.0,10.0 to 11.0,11.0 results in thousands
  //of kilometers).
  val minorBoundPositionLat: Double = 44.1343417
  val maxBoundPositionLat: Double = 44.1565639
  val minorBoundPositionLon: Double = 12.2363402
  val maxBoundPositionLon: Double = 12.2585623

  var randomLatitudeDestination: Double = .0
  var randomLongitudeDestination: Double = .0

  override def movementAndChangePositionObservable(): Observable[Position] = {
    Observable(
      subscriber => {
        new Thread(new Runnable() {
          def run(): Unit = {
            while(true) {
              if (subscriber.isUnsubscribed) {
                subscriber.onCompleted()
                return
              }
              //This event will be triggered by a server instruction
              randomLatitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat)
              randomLongitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon)
              Log.log("Position to reach: " + randomLatitudeDestination + " , " + randomLongitudeDestination)
              subscriber.onNext(new Position(randomLatitudeDestination, randomLongitudeDestination ))
              Thread.sleep(1500000) //this is temporary
            }
          }
        }).start()
      }
    )
  }

}