package pt.dinis.communication;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.dinis.common.Display;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.main.Dealer;

import java.io.*;
import java.net.Socket;

/**
 * Created by tiago on 22-01-2017.
 */
public class ClientCommunicationThread extends Thread{

    private final static Logger logger = Logger.getLogger(ClientCommunicationThread.class);

    private Integer id;
    private Socket socket;
    private boolean running;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final DateTime time;

    public ClientCommunicationThread(Socket socket, Integer id) throws IOException {

        this.id = id;
        this.socket = socket;
        this.running = true;
        this.time = new DateTime();

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while(running) {
            try {
                GenericMessage message = (GenericMessage) in.readObject();
                logger.debug("Receiving message: " + message);
                if (!ClientCommunicationProtocol.protocol(message, id)) {
                    logger.info("Cannot process message from id " + id + ": " + message);
                }
            } catch (ClassCastException e) {
                Display.alert("Received wrong message format");
                logger.error("Received message is not of a correct class: ", e);
            } catch (EOFException e) {
                logger.warn("The connection to client " + id + " has been lost.");
                Dealer.disconnectClient(id);
            } catch (IOException | ClassNotFoundException e) {
                if(running) {
                    logger.warn("Problem receiving message", e);
                } else if (!toContinue()) {
                    Dealer.disconnectClient(id);
                }
            }
        }
    }

    public boolean close() {
        logger.info("Closing thread of client " + id + " opened at " + time.toString());

        boolean result = true;
        running = false;

        try {
            socket.close();
        } catch (IOException e) {
            logger.info("Problem closing socket of client " + id + ".", e);
            result = false;
        }

        try {
            in.close();
        } catch (IOException e) {
            logger.info("Problem closing socket input of client " + id + ".", e);
            result = false;
        }

        try {
           out.close();
        } catch (IOException | NullPointerException e) {
            logger.info("Problem closing socket output of client " + id + ".", e);
            result = false;
        }
        return result;
    }

    private boolean toContinue() {
        if(!socket.isConnected()) {
            return false;
        }

        return true;
    }

    public boolean sendMessage(GenericMessage message) {
        if (!toContinue()) {
            Dealer.disconnectClient(id);
            return false;
        }

        try {
            out.writeObject(message);
        } catch (IOException e) {
            logger.warn("Error sending message " + message, e);
            return false;
        }
        return true;
    }
}
