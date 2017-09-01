package com.wedriveu.vehicle.simulation

import java.util.concurrent.ThreadLocalRandom

import com.wedriveu.shared.util.{Log, Position}
import rx.lang.scala.Observable

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

  /** This is the Observable for the event: vehicle broken.
    *
    * @return Return a simple emit of the event.
    */
  def brokenEventObservable(): Observable[String]

  /** This is the Observable for the event: vehicle stolen.
    *
    * @return Return a simple emit of the event.
    */
  def stolenEventObservable(): Observable[String]

  /** This method permits to unsubsribe the control system to vehicle broken events. */
  def unsubscribeToBrokenEvents(): Unit

  /** This method permits to unsubsribe the control system to vehicle stolen events. */
  def unsubscribeToStolenEvents(): Unit
}

class VehicleEventsObservablesImpl extends VehicleEventsObservables {

  //These two variables indicate the bounds for the random latitude and longitude calculation. The variation estimated
  //for containing the distance in 10 - 100 kilometers is like this (from 10.0,10.0 to 11.0,11.0 results in thousands
  //of kilometers).
  val minorBoundPositionLat: Double = 44.1343417
  val maxBoundPositionLat: Double = 44.1565639
  val minorBoundPositionLon: Double = 12.2363402
  val maxBoundPositionLon: Double = 12.2585623
  val startBrokenRange: Int = 12
  val endBrokenRange: Int = 17
  val startStolenRange: Int = 13
  val endStolenRange: Int = 18
  val oneSecondInMillis: Int = 1000
  val positionToReachLog: String = "Position to reach: "
  val vehicleBrokenLog: String = "Vehicle Broken Event emitted"
  val brokenEventLog: String = "Broken event"
  val vehicleStolenLog: String = "Vehicle Stolen Event emitted"
  val stolenEventLog: String = "Stolen event"

  var randomLatitudeDestination: Double = .0
  var randomLongitudeDestination: Double = .0
  var unsubscribeToMovements: Boolean = false
  var unsubscribeToBrokens: Boolean = false
  var unsubscribeToStolens: Boolean = false

  override def movementAndChangePositionObservable(): Observable[Position] = {
    Observable(
      subscriber => {
        new Thread(new Runnable() {
          def run(): Unit = {
            while (true) {
              if (unsubscribeToMovements) {
                subscriber.unsubscribe()
              }
              if (subscriber.isUnsubscribed) {
                subscriber.onCompleted()
                return
              }
              //This event will be not necessary when the service is up.
              randomLatitudeDestination =
                  ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat)
              randomLongitudeDestination =
                  ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon)
              Log.info(positionToReachLog + randomLatitudeDestination + " , " + randomLongitudeDestination)
              subscriber.onNext(new Position(randomLatitudeDestination, randomLongitudeDestination))
              Thread.sleep(1500000) //this is temporary
            }
          }
        }).start()
      }
    )
  }

  override def brokenEventObservable(): Observable[String] = {
    Observable(
      subscriber => {
        new Thread(new Runnable() {
          def run(): Unit = {
            while (true) {
              calculateRandomNumber(startBrokenRange, endBrokenRange, vehicleBrokenLog)
              if (unsubscribeToBrokens) {
                subscriber.unsubscribe()
              }
              if (subscriber.isUnsubscribed) {
                subscriber.onCompleted()
                return
              }
              subscriber.onNext(brokenEventLog)
            }
          }
        }).start()
      }
    )
  }

  override def stolenEventObservable(): Observable[String] = {
    Observable(
      subscriber => {
        new Thread(new Runnable() {
          def run(): Unit = {
            while (true) {
              calculateRandomNumber(startStolenRange, endStolenRange, vehicleStolenLog)
              if (unsubscribeToStolens) {
                subscriber.unsubscribe()
              }
              if (subscriber.isUnsubscribed) {
                subscriber.onCompleted()
                return
              }
              subscriber.onNext(stolenEventLog)
            }
          }
        }).start()
      }
    )
  }

  private def calculateRandomNumber(startRange: Int, endRange: Int, vehicleLog: String): Unit = {
    val randomNumber = new scala.util.Random
    val result = startRange + randomNumber.nextInt((endRange - startRange) + 1);
    Thread.sleep(result * oneSecondInMillis)
    Log.info(vehicleLog)
  }

  override def unsubscribeToBrokenEvents(): Unit = {
    unsubscribeToBrokens = true
  }

  override def unsubscribeToStolenEvents(): Unit = {
    unsubscribeToStolens = true
  }

}