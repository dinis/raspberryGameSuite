package pt.dinis.client.login.core;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.dinis.common.core.Display;
import pt.dinis.common.messages.GenericMessage;

import java.io.*;
import java.net.Socket;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClientCommunication extends Thread {

    final static Logger logger = Logger.getLogger(LoginClientCommunication.class);

    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static boolean running;
    private static DateTime time;

    public LoginClientCommunication(Socket socket) throws IOException {
        this.socket = socket;
        time = new DateTime();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        running = true;

        while(running) {
            try {
                GenericMessage message = (GenericMessage) in.readObject();
                if (message.getDirection() == GenericMessage.Direction.SERVER_TO_CLIENT) {
                    logger.debug("Receiving message: " + message);
                    LoginClientCommunicationProtocol.protocol(message);
                } else {
                    logger.warn("Client got a message supposedly from client to server: " + message);
                    Display.alert("Wrong message from server");
                }
            } catch (ClassCastException e) {
                Display.alert("Received wrong message format");
                logger.error("Received message is not of a correct class: ", e);
            } catch (EOFException e) {
                Display.alert("Error receiving from server: disconnect");
                disconnect();
            } catch (IOException | ClassNotFoundException e) {
                if(running) {
                    logger.warn("Problem receiving message", e);
                }
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
        } catch (IOException | NullPointerException e) {
            logger.info("Problem closing socket output");
            result = false;
        }

        socket = null;
        Display.info("Disconnect");

        return result;
    }

    public static boolean sendMessage(GenericMessage message) {
        if (!isConnected()) {
            Display.alert("Cannot send message: " + message);
            return false;
        }

        try {
            out.writeObject(message);
        } catch (IOException e) {
            Display.alert("Error sending message: " + message);
        }
        return true;
    }
}
