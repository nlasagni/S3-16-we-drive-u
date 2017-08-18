package com.wedriveu.services.booking.app

import com.wedriveu.services.booking.boundary.BootBoundary
import io.vertx.scala.core.Vertx

import scala.util.{Failure, Success}

/**
  * @author Nicola Lasagni on 12/08/2017.
  */
object Main extends App {

  //TODO Complete boot operations
  import scala.concurrent.ExecutionContext.Implicits.global
  Vertx.vertx().deployVerticleFuture(BootBoundary.Verticle).onComplete {
    case Success(_) => print("boundary booted")
    case Failure(result) => print("boundary boot failed"); result.printStackTrace()
  }

}
