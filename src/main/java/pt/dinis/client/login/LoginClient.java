package pt.dinis.client.login;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.dinis.common.Display;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.main.Configurations;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClient {

    private static Logger logger = Logger.getLogger(LoginClient.class);

    private static final String DEFAULT_IP = "login.client.server.host";
    private static final String DEFAULT_PORT = "login.client.server.port";

    private static String hash;

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
            try {
                LoginClientScannerProtocol.protocol(message);
            } catch (Exception e) {
                Display.alert("Error: '" + message + "'.");
                logger.error("Problem interpreting or performing '" + message + "'.", e);
            }
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
            Socket socket = new Socket(ip.orElse(Configurations.getProperty(DEFAULT_IP)),
                    port.orElse(Integer.parseInt(Configurations.getProperty(DEFAULT_PORT))));
            loginSocket = new LoginClientCommunication(socket);
            loginSocket.start();
        } catch (IOException e) {
            logger.error("Could not open a socket", e);
            Display.alert("Could not connect");
            return false;
        }

        Display.info("Connect");
        return true;
    }

    public static boolean disconnect() {
        if(!isConnected()) {
            logger.info("Trying to disconnect a socket that is already gone.");
            Display.alert("Already disconnected");
            return false;
        }

        return loginSocket.disconnect();
    }

    public static boolean sendMessage(GenericMessage message) {
        logger.debug("Client is sending message: " + message);
        return LoginClientCommunication.sendMessage(message);
    }

    public static boolean isConnected() {
        if(loginSocket == null) {
            return false;
        }
        return loginSocket.isConnected();
    }

    public static boolean isLoggedIn() {
        return hash != null;
    }

    public static String getHash() {
       return hash;
    }

    public static boolean setHash(String newHash) {
        hash = newHash;
        return true;
    }

    public static boolean logout() {
        hash = null;
        Display.info("Logout");
        return !isLoggedIn();
    }

    public static void close() {
        if(isConnected()) {
            disconnect();
        }
        running = false;
    }
}
