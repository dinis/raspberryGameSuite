package pt.dinis.common.core;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by tiago on 22-01-2017.
 */
public class Display {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Displays all kind of messages
     * @param message to display
     */
    public static void display(String message) {
        displayWithColor(message, ANSI_GREEN);
    }

    /**
     * Displays all error or alert messages
     * @param message to display
     */
    public static void alert(String message) {
        displayWithColor(message, ANSI_RED);
    }

    /**
     * Displays information, like login, logout, successful operations
     * @param message to display
     */
    public static void info(String message) {
        displayWithColor(message, ANSI_BLUE);
    }


    private static void displayWithColor(String message, String color) {
        DateTime time = new DateTime();
        DateTimeFormatter formatter = DateTimeFormat.shortDateTime();
        System.out.println(color + formatter.print(time) + ": " + ANSI_RESET + message);
    }

    /**
     * Displays clean text, for example, list of stuff
     * @param message to display
     */
    public static void cleanColor(String message) {
        System.out.println("\t" + ANSI_PURPLE + message + ANSI_RESET);
    }
}
