package com.wedriveu.services.shared.utilities;

/**
 * Created by Michele on 22/07/2017.
 */
public class Log {

    private static final String ERROR = "[ERROR] %s %s";

    public static void log(String toLog){
        System.out.println(toLog);
    }

    public static void error(String tag, String message, Throwable throwable) {
        System.out.println(String.format(ERROR, tag, message));
        throwable.printStackTrace();
    }

    public static void error(String message, Throwable throwable) {
        error("", message, throwable);
    }

}
