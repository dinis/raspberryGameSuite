package pt.dinis.client.login;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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
        time = new DateTime();
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
                    Display.alert("Error receiving from server: disconnect");
                    disconnect();
                } else {
                    LoginClientCommunicationProtocol.protocol(message);
                }
            } catch (IOException e) {
                logger.warn("Problem receiving message", e);
            }
        }
    }

    public static boolean isConnected() {
        return socket != null;
    }

    public static boolean disconnect() {
        logger.info("Closing communication with server opened at " + time.toString());

        boolean result = true;
        running = false;

        try {
            socket.close();
        } catch (IOException | NullPointerException e) {
            logger.info("Problem closing socket.");
            result = false;
        }

        try {
            in.close();
        } catch (IOException | NullPointerException e) {
            logger.info("Problem closing socket input.");
            result = false;
        }

        try {
            out.close();
        } catch (NullPointerException e) {
            logger.info("Problem closing socket output");
            result = false;
        }

        socket = null;
        Display.info("Disconnect");

        return result;
    }

    public static boolean sendMessage(String message) {
        if (!isConnected()) {
            Display.alert("Cannot send message '" + message + "' ");
            return false;
        }

        out.println(message);
        return true;
    }
}
