package com.wedriveu.services.shared.utilities;

/**
 * @author Michele
 * @author Marco Baldassarri
 * @since 01/08/2017
 */
public class Log {

    private static final String ERROR = "[ERROR] %s %s";

    public static void log(String toLog) {
        System.out.println(toLog);
    }

    public static void error(String tag, String message, Throwable throwable) {
        System.out.println(String.format(ERROR, tag, message));
        throwable.printStackTrace();
    }

    public static void info(String tag, String message) {
        System.out.println("[" + tag + "] " + message);
    }
}
