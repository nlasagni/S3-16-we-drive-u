package com.wedriveu.services.booking.util

import scala.concurrent.Future

/** This trait is used to represent an actor that boot
  * all the needed components.
  *
  * @author Nicola Lasagni on 17/08/2017.
  *
  */
trait BootManager {

  /** Boots all the needed components.
    *
    * @return The future of all the boot operations
    */
  def boot(): Future[_]

}
