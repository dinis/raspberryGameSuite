package pt.dinis.main;

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

    public static boolean display(String message) {
        return displayWithColor(message, ANSI_GREEN);
    }

    public static boolean alert(String message) {
        return displayWithColor(message, ANSI_RED);
    }

    public static boolean info(String message) {
        return displayWithColor(message, ANSI_BLUE);
    }

    private static boolean displayWithColor(String message, String color) {
        DateTime time = new DateTime();
        DateTimeFormatter formatter = DateTimeFormat.shortDateTime();
        System.out.println(color + formatter.print(time) + ": " + ANSI_RESET + message);
        return true;
    }

    public static boolean clean(String message) {
        System.out.println(message);
        return true;
    }

}
