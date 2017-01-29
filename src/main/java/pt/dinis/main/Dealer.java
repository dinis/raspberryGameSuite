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

    private final static Logger logger = Logger.getLogger(Dealer.class);

    private final Integer DEFAULT_PORT = 1500;

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

    public boolean start() {
      run();

      return true;
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
                try {
                    ClientCommunicationThread client = new ClientCommunicationThread(socket, id);

                    if (!addClient(id, client)) {
                        client.close();
                        removeClient(id);
                        Display.alert("Error creating client " + id);
                    } else {
                        client.start();
                        Display.display("New client " + id);
                    }
                } catch (IOException e) {
                    logger.error("Problem opening socket ", e);
                    Display.alert("Error opening communication with client.");
                }
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
        return Display.display(message);
    }

    /*
    Adds a client to our map, returns false if id existed already
     */
    private Boolean addClient(Integer id, ClientCommunicationThread clientCommunicationThread) {
        logger.info("Creating client " + id);
        if(clientCommunicationThreads.containsKey(id)) {
            logger.warn("Failed to create client " + id);
            return false;
        }
        clientCommunicationThreads.put(id, clientCommunicationThread);
        return true;
    }

    private Integer generateUniqueId() {
        if (clientCommunicationThreads.isEmpty()) {
            return 1;
        }
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

    public boolean closeClient() {
        return true;
    }

    public static Collection<Integer> getActiveClients() {
        return new HashSet(clientCommunicationThreads.keySet());
    }
}
