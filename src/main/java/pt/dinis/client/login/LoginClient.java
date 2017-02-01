package pt.dinis.client.login;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.dinis.main.Display;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClient {

    private static Logger logger = Logger.getLogger(LoginClient.class);

    private static final String DEFAULT_IP = "localhost";
    private static final Integer DEFAULT_PORT = 1500;

    private static LoginClientCommunication loginSocket;
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
            LoginClientScannerProtocol.protocol(message);
        }
        logger.info("Exiting session started at " + time);
        Display.info("Goodbye");
    }


    public static boolean connect(Optional<String> ip, Optional<Integer> port) {

        if(isConnected()) {
            Display.alert("Trying to connect when is already connected.");
            logger.warn("It is already connected. Nevertheless, it received an order to connect.");
            return false;
        }

        try {
            Socket socket = new Socket(ip.orElse(DEFAULT_IP), port.orElse(DEFAULT_PORT));
            loginSocket = new LoginClientCommunication(socket);
            loginSocket.start();
        } catch (IOException e) {
            logger.error("Could not open a socket", e);
            Display.alert("Could not start communication");
            return false;
        }

        Display.info("Communication started.");
        return true;
    }

    public static boolean sendMessage(String message) {
        return LoginClientCommunication.sendMessage(message);
    }

    public static boolean isConnected() {
        if(loginSocket == null) {
            return false;
        }
        return loginSocket.isConnected();
    }

    public static void close() {
        if(loginSocket != null) {
            loginSocket.close();
        }
        running = false;
    }
}
