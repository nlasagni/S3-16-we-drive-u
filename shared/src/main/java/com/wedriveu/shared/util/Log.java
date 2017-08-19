package com.wedriveu.shared.util;

/**
 * A simple log-utility class.
 *
 * @author Michele Donati
 * @author Marco Baldassarri
 * @author Nicola Lasagni
 */
public class Log {

    private static final String INFO = "[INFO] %s %s";
    private static final String ERROR = "[ERROR] %s %s";

    /**
     * Logs an info message.
     *
     * @param message the message to be logged
     */
    public static void info(String message){
        System.out.println(message);
    }

    /**
     * Logs an info message.
     *
     * @param tag     the tag of the component that wants to log
     * @param message the message to be logged
     */
    public static void info(String tag, String message) {
        info(String.format(INFO, tag, message));
    }

    /**
     * Logs an error message.
     *
     * @param tag       the tag of the component that wants to log
     * @param message   the message to be logged
     * @param throwable the exception that cause the error logged
     */
    public static void error(String tag, String message, Throwable throwable) {
        info(String.format(ERROR, tag, message));
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    /**
     * Logs an error message.
     *
     * @param tag       the tag of the component that wants to log
     * @param throwable the exception that cause the error logged
     */
    public static void error(String tag, Throwable throwable) {
        error(tag, "", throwable);
    }

    /**
     * Logs an error message.
     *
     * @param tag     the tag of the component that wants to log
     * @param message the message to be logged
     */
    public static void error(String tag, String message) {
        error(tag, message, null);
    }

}
