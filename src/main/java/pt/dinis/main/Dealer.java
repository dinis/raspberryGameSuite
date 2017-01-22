package pt.dinis.main;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import pt.dinis.communication.ClientCommunicationThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by tiago on 21-01-2017.
 */
public class Dealer {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private final static Logger logger = Logger.getLogger(Dealer.class);

    private final Integer DEFAULT_PORT = 1707;

    private boolean running = false;
    private static Map<Integer, ClientCommunicationThread> clientCommunicationThreads;
    private final int port;

    public Dealer() {
        this(null);
    }

    public Dealer(Integer port) {
        clientCommunicationThreads = new HashMap<Integer, ClientCommunicationThread>();
        this.port = (port == null ? DEFAULT_PORT : port);
    }

    /*
    This creates a socket server to wait for a client,
    and creates a new socket and ClientCommunicationThread
    to communicate with him
     */
    public void run() {
        running = true;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            display("Listening at port " + port + ".");
            logger.info("Open Server Socket successfully at port " + port + ".");

            // Listening
            while (running) {
                Socket socket = serverSocket.accept();
                // Tests if it should be listening
                if(!running) {
                    break;
                }
                Integer id = generateUniqueId();
                ClientCommunicationThread client = new ClientCommunicationThread(socket, id);

                if(!addClient(id, client)) {
                    client.close();
                }

                client.run();
            }

            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("Problem closing server socket", e);
            }

            for (ClientCommunicationThread client: clientCommunicationThreads.values()) {
                client.close();
            }

        } catch (IOException e) {
            logger.error("Problem handling server socket", e);
        }
    }

    /*
    Sends a message to stop the server
     */
    public void stop() {
        running = false;
    }

    /*
    Displays a message in stdout
     */
    private boolean display(String message) {
        DateTime time = new DateTime();
        DateTimeFormatter formatter = DateTimeFormat.shortDateTime();
        System.out.println(ANSI_GREEN + formatter.print(time) + ": " + ANSI_RESET + message);
        return true;
    }

    /*
    Adds a client to our map, returns false if id existed already
     */
    private Boolean addClient(Integer id, ClientCommunicationThread clientCommunicationThread) {
        logger.info("Creating client " + id);
        if(clientCommunicationThreads.containsKey(id)) {
            return false;
        }
        clientCommunicationThreads.put(id, clientCommunicationThread);
        return true;
    }

    private Integer generateUniqueId() {
        return Collections.max(clientCommunicationThreads.keySet()) + 1;
    }

    /*
    Remove client id
     */
    private Boolean removeClient(Integer id) {
        logger.info("Removing client " + id);
        if(clientCommunicationThreads.containsKey(id)) {
            clientCommunicationThreads.remove(id);
            return true;
        }
        return false;
    }

    /*
    This method will send the message to every element in list ids
     */
    public static Boolean sendMessage(Collection<Integer> ids, String message) {
        logger.info("Sending message [" + message + "] to clients [" + ids.toString() + "]." );
        boolean result = true;
        for (Integer id: ids) {
            if (!clientCommunicationThreads.containsKey(id)) {
                result = false;
                logger.warn("Trying to send message [" + message + "] to client " + id + ". Client do not exist.");
                continue;
            } else {
                ClientCommunicationThread client = clientCommunicationThreads.get(id);
                if (!client.sendMessage(message)) {
                    result = false;
                    logger.warn("Sending message [" + message + "] to client " + id + " failed.");
                }
            }
        }
        return result;
    }

    public static Collection<Integer> getActiveClients() {
        return clientCommunicationThreads.keySet();
    }
}
