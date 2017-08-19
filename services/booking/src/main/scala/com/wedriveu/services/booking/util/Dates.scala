package com.wedriveu.services.booking.util

import java.text.SimpleDateFormat
import java.util.Date

/** Simple date-utility object
  *
  * @author Nicola Lasagni on 18/08/2017.
  */
object Dates {

  private val Format = "dd/MM/yyyy HH:mm"
  private val DateFormat = new SimpleDateFormat(Format)

  /** Formats a date into a predefined [[Format]]
    *
    * @param date The date to be formatted.
    * @return A [[String]] representation of the date provided.
    */
  def format(date: Date): String = {
    if (date == null) "" else DateFormat.format(date)
  }

}
