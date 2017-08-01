package com.wedriveu.vehicle.simulation

import rx.lang.scala.Observable
import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.vehicle.entity.Position

/**
  * @author Michele Donati on 28/07/2017.
  */

/** This models the Observables that generates the events captured by vehicles. */
trait VehicleEventsObservables {
  /** This is the Observable for the event: battery drain.
    *
    * @return Returns the Observable object.
    */
  def batteryDrainObservable(): Observable[String]

  /** This is the Observable for the event: movement and position change.
    *
    * @return Return the position for the vehicle, to reach.
    */
  def movementAndChangePositionObservable(): Observable[Position]

}

class VehicleEventsObservablesImpl extends VehicleEventsObservables {

  //These two variables indicate the bounds for the random latitude and longitude calculation. The variation estimated
  //for containing the distance in 50 - 100 kilometers is like this (from 10.0,10.0 to 11.0,11.0 results in thousands
  //of kilometers).
  val minorBoundPosition: Double = 9.9999979899999
  val maxBoundPosition: Double = 10.01111111111111

  var randomLatitudeDestination: Double = .0
  var randomLongitudeDestination: Double = .0

  override def batteryDrainObservable(): Observable[String] = {
    Observable(
      subscriber => {
        new Thread(new Runnable() {
          def run(): Unit = {
            while(true) {
              if (subscriber.isUnsubscribed) {
                subscriber.onCompleted()
                return
              }
              subscriber.onNext("Drain battery and move")
              Thread.sleep(1000) //this indicates the interval of time we want to generate the events
            }
          }
        }).start()
      }
    )
  }

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
              randomLatitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPosition, maxBoundPosition)
              randomLongitudeDestination = ThreadLocalRandom.current().nextDouble(minorBoundPosition, maxBoundPosition)
              subscriber.onNext(new Position(randomLatitudeDestination,randomLongitudeDestination ))
              Thread.sleep(1500000) //this is temporary
            }
          }
        }).start()
      }
    )
  }

}