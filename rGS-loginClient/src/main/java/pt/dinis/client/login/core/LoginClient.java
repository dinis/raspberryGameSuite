package pt.dinis.client.login.core;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.dinis.common.core.Display;
import pt.dinis.common.core.Player;
import pt.dinis.common.messages.AuthenticatedMessage;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.core.Configurations;

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

    private static String token;
    private static Player me;

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
        logger.info("This client are now connected to server.");
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
        if (message instanceof AuthenticatedMessage) {
            logger.info("Message is already authenticated when arrived to LoginClient: " + message);
        } else {
            String tokenToSend = null;
            if (isLoggedIn()) {
                tokenToSend = getToken();
            }
            message = new AuthenticatedMessage(message, tokenToSend);
        }
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
        return token != null;
    }

    public static String getToken() {
       return token;
    }

    public static boolean setToken(String newToken) {
        token = newToken;
        return true;
    }

    public static boolean setMe(Player player) {
        if (player == null) {
            return false;
        }
        me = player;
        return true;
    }

    public static Player getMe() {
        return me;
    }

    public static boolean logout() {
        token = null;
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
