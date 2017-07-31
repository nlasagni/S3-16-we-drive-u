package com.wedriveu.vehicle.simulation

import rx.lang.scala.Observable

/**
  * Created by Michele on 28/07/2017.
  */
trait VehicleEventsObservables {
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