package com.wedriveu.services.booking.app

import com.wedriveu.services.booking.boundary.BootBoundary
import com.wedriveu.services.booking.control.BootController
import com.wedriveu.services.booking.util.BootManager
import io.vertx.lang.scala.ScalaVerticle

import scala.concurrent.Future

/**
  * @author Nicola Lasagni on 19/08/2017.
  */
object ServiceBoot {

  /**
    * The [[ScalaVerticle]] name that executes the boot operations
    */
  val Verticle: String = s"scala:${classOf[ServiceBootImpl].getName}"

  private[this] class ServiceBootImpl extends ScalaVerticle with BootManager {

    override def startFuture(): Future[_] = {
      boot()
    }

    override def boot(): Future[_] = {
      vertx.deployVerticleFuture(BootBoundary.Verticle).flatMap({
        _ => vertx.deployVerticleFuture(BootController.Verticle)
      })
    }

  }

}
