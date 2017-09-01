package com.wedriveu.services.booking.control

import com.wedriveu.services.booking.util.BootManager
import io.vertx.lang.scala.ScalaVerticle

import scala.concurrent.Future

/** Creates a [[BootManager]] that boots the [[BookingController]]
  *
  * @author Nicola Lasagni on 18/08/2017.
  */
object BootController {

  /**
    * The [[ScalaVerticle]] name that executes the boot operations
    */
  val Verticle: String = s"scala:${classOf[BootControllerImpl].getName}"

  private class BootControllerImpl extends ScalaVerticle with BootManager {

    override def startFuture(): Future[_] = {
      boot()
    }

    override def boot(): Future[_] = {
      vertx.deployVerticleFuture(BookingControllerVerticle.Verticle)
    }

  }

}
