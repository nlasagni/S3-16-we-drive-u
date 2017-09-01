package com.wedriveu.mobile.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for manipulating dates.
 *
 * @author Nicola Lasagni on 26/08/2017.
 */
public class Dates {

    private static final String FORMAT = "dd/MM/yyyy HH:mm";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT, Locale.getDefault());

    /**
     * Formats a date by using {@linkplain #FORMAT}.
     *
     * @param date the date to be formatted
     * @return the formatted date
     */
    public static String format(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

}
