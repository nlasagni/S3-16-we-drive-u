package com.wedriveu.services.booking.app

import io.vertx.scala.core.Vertx


/**
  * @author Nicola Lasagni on 12/08/2017.
  */
object Main extends App {

  Vertx.vertx().deployVerticleFuture(ServiceBoot.Verticle)

}
