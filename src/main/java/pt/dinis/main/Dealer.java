package pt.dinis.main;

import org.apache.log4j.Logger;
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

    private final int port;
    private static boolean running = false;
    private static Map<Integer, ClientCommunicationThread> clientCommunicationThreads;
    private static ServerScanner serverScanner;
    private static ServerSocket serverSocket;
    private Integer uniqueId = 0;

    public Dealer(Integer port) {
        clientCommunicationThreads = new HashMap<Integer, ClientCommunicationThread>();
        this.port = port;
    }

    public boolean start() throws IOException {
        serverScanner = new ServerScanner();
        serverScanner.start();
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
        try {
            serverSocket = new ServerSocket(port);
            Display.info("Listening at port " + port + ".");
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
                        Display.info("New client " + id);
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

            for (Integer id: clientCommunicationThreads.keySet()) {
                disconnectClient(id);
            }

        } catch (IOException e) {
            logger.error("Problem handling server socket", e);
        } finally {
            Display.info("Exit");
        }
    }

    /*
    Sends a message to stop the server
     */
    public static void stop() {
        running = false;
        for(Integer id: getActiveClients()) {
            disconnectClient(id);
        }
        serverScanner.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Error closing server socket.",e);
        }
        Display.alert("Closing server application");
    }

    /*
    Adds a client to our map, returns false if id existed already
     */
    private static Boolean addClient(Integer id, ClientCommunicationThread clientCommunicationThread) {
        logger.info("Creating client " + id);
        if(clientCommunicationThreads.containsKey(id)) {
            logger.warn("Failed to create client " + id);
            return false;
        }
        clientCommunicationThreads.put(id, clientCommunicationThread);
        return true;
    }

    private synchronized Integer generateUniqueId() {
        return uniqueId ++;
    }

    /*
    Remove client id
     */
    private static Boolean removeClient(Integer id) {
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

    public static boolean disconnectClient(int id) {
        boolean result = true;

        if(!clientCommunicationThreads.containsKey(id)) {
            return false;
        }

        if(!clientCommunicationThreads.get(id).close()){
            logger.warn("Could not disconnect client " + id);
            result = false;
        }

        if(!removeClient(id)) {
            logger.warn("Could not remove client " + id);
            result = false;
        }

        Display.info("Disconnect client " + id);
        return result;
    }

    public static boolean loginClient(Integer id) {
        try {
            String hash = LoginManager.loginClient(id);
            return sendMessage(Collections.singleton(id), "login " + hash);
        } catch (Exception e) {
            Display.alert("Could not log in client " + id);
            sendMessage(Collections.singleton(id), "error refuse login");
            return false;
        }
    }

    public static boolean reloginClient(String hash, Integer id) {
        if (LoginManager.reloginClient(hash, id)) {
            return sendMessage(Collections.singleton(id), "success");
        } else {
            sendMessage(Collections.singleton(id), "error refuse relogin");
            return false;

    }

    public static boolean logoutClient(Integer id) {
        return LoginManager.logoutClient(id);
    }



    public static Collection<Integer> getActiveClients() {
        return new HashSet(clientCommunicationThreads.keySet());
    }
}
