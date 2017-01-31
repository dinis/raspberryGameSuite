package pt.dinis.client.login;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.dinis.main.Display;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClient {

    private static Logger logger = Logger.getLogger(LoginClient.class);

    private static Socket socket;
    private final DateTime time;
    private static boolean running;
    private Scanner scanner;

    public LoginClient() {
        time = new DateTime();
    }

    public void start() {
        scanner = new Scanner(System.in);
        run();
    }

    private void run() {
        Display.info("Welcome");
        running = true;
        while(running) {
            String message = scanner.nextLine();
            // TODO do whatever
        }
        logger.info("Exiting session started at " + time);
        Display.info("Goodbye");
    }


    public static boolean openCommunication() {

        if(isConnected()) {
            Display.alert("Trying to connect when is already connected.");
            logger.warn("It is already conneceted. Nevertheless, it received an order to connect.");
            return false;
        }

        try {
            socket = new Socket("localhost", 1500);
        } catch (IOException e) {
            logger.error("Could not open a socket", e);
            Display.alert("Could not open a socket");
            return false;
        }

        Display.info("Socket created.");
        return true;
    }

    public static boolean sendMessage(String message) {
        return LoginClientCommunication.sendMessage(message);
    }

    public static boolean isConnected() {
        if(socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    public static void close() {
        running = false;
    }
}
