package pt.dinis.client.login;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.dinis.client.simple.SimpleClientScanner;
import pt.dinis.main.Display;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClientCommunication extends Thread {

    final static Logger logger = Logger.getLogger(LoginClientCommunication.class);

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static boolean running;
    private static DateTime time;

    public LoginClientCommunication(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        running = true;

        while(running) {
            try {
                String message = in.readLine();
                logger.debug("Receiving message " + message);
                if(message == null) {
                    close();
                    Display.alert("Communication closed by server.");
                } else {
                    // TODO: do stuff when message is logout close or login
                    Display.display(message);
                }
            } catch (IOException e) {
                logger.warn("Problem receiving message", e);
            } finally {
                if(!toContinue()) {
                    Display.alert("Connection lost");
                    close();
                }
            }
        }
    }

    private static boolean toContinue() {
        return isConnected();
    }

    public static boolean close() {
        logger.info("Closing communication with server opened at " + time.toString());

        boolean result = true;
        running = false;

        try {
            socket.close();
        } catch (IOException e) {
            logger.info("Problem closing socket.");
            result = false;
        }

        try {
            in.close();
        } catch (IOException e) {
            logger.info("Problem closing socket input.");
            result = false;
        }

        out.close();

        return result;
    }

    public static boolean sendMessage(String message) {
        if (!toContinue()) {
            close();
            Display.alert("Failed to send message " + message);
            return false;
        }

        out.println(message);
        return true;
    }

    public static boolean isConnected() {
        if(socket == null) {
            return false;
        }
        return socket.isConnected();
    }
}
