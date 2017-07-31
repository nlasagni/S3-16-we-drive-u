package com.wedriveu.vehicle.simulation

import rx.lang.scala.Observable

/**
  * Created by Michele on 28/07/2017.
  */

/** This models the Observables that generates the events captured by vehicles. */
trait VehicleEventsObservables {
  /** This is the Observable for the event: battery drain.
    *
    * @return Returns the Observable object.
    */
  def batteryDrainObservable(): Observable[String]
}

class VehicleEventsObservablesImpl extends VehicleEventsObservables {

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

}


/*

 */