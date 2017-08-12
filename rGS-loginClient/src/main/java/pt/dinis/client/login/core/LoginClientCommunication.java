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

    public static void disconnect() {
        logger.info("Closing communication with server opened at " + time.toString());
        running = false;

        try {
            socket.close();
        } catch (IOException | NullPointerException e) {
            logger.info("Problem closing socket.");
        }

        try {
            in.close();
        } catch (IOException | NullPointerException e) {
            logger.info("Problem closing socket input.");
        }

        try {
            out.close();
        } catch (IOException | NullPointerException e) {
            logger.info("Problem closing socket output");
        }

        socket = null;
        Display.info("Disconnect");
    }

    public static void sendMessage(GenericMessage message) {

        try {
            if (!isConnected()) {
                Display.alert("Cannot send message: " + message);
            } else {
                out.writeObject(message);
            }
        } catch (IOException e) {
            Display.alert("Error sending message: " + message);
        }
    }
}
