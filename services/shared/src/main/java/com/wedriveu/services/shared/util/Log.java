package com.wedriveu.services.shared.util;

/**
 * @author Michele
 * @author Marco Baldassarri
 * @since 01/08/2017
 */
public class Log {

    private static final String INFO = "[INFO] %s %s";
    private static final String ERROR = "[ERROR] %s %s";

    public static void log(String message){
        System.out.println(message);
    }

    public static void info(String tag, String message) {
        log(String.format(INFO, tag, message));
    }

    public static void error(String tag, String message, Throwable throwable) {
        log(String.format(ERROR, tag, message));
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    public static void error(String tag, String message) {
        error(tag, message, null);
    }

}
