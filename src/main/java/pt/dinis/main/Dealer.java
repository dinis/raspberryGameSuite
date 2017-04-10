package pt.dinis.main;

import org.apache.log4j.Logger;
import pt.dinis.common.Display;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.user.LoginAnswer;
import pt.dinis.common.messages.user.ReLoginAnswer;
import pt.dinis.common.messages.user.RegisterAnswer;
import pt.dinis.common.messages.user.UserMessage;
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
        clientCommunicationThreads = new HashMap<>();
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

            clientCommunicationThreads.keySet().forEach(Dealer::disconnectClient);

        } catch (IOException e) {
            if(running) {
                logger.error("Problem handling server socket", e);
            }
        } finally {
            Display.info("Exit");
        }
    }

    /*
    Sends a message to stop the server
     */
    public static void stop() {
        logger.warn("Closing dealer.");
        running = false;
        getActiveClients().forEach(Dealer::disconnectClient);

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
    public static Boolean sendMessage(Collection<Integer> ids, GenericMessage message) {
        logger.info("Sending message [" + message + "] to clients [" + ids.toString() + "]." );
        boolean result = true;
        for (Integer id: ids) {
            if (!clientCommunicationThreads.containsKey(id)) {
                result = false;
                logger.warn("Trying to send message [" + message + "] to client " + id + ". Client do not exist.");
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
            String token = LoginManager.loginClient(id);
            return sendMessage(Collections.singleton(id), new LoginAnswer(UserMessage.AnswerType.SUCCESS, token, null));
        } catch (Exception e) {
            Display.alert("Could not log in client " + id);
            sendMessage(Collections.singleton(id), new LoginAnswer(UserMessage.AnswerType.ERROR, null, "error"));
            return false;
        }
    }

    public static boolean registerClient(Integer id) {
        try {
            String token = LoginManager.loginClient(id);
            return sendMessage(Collections.singleton(id), new RegisterAnswer(UserMessage.AnswerType.SUCCESS, token, null));
        } catch (Exception e) {
            Display.alert("Could not log in new client " + id);
            sendMessage(Collections.singleton(id), new RegisterAnswer(UserMessage.AnswerType.ERROR, null, "error"));
            return false;
        }
    }

    public static boolean reloginClient(Integer id, String token) {
        if (LoginManager.reloginClient(id, token)) {
            return sendMessage(Collections.singleton(id), new ReLoginAnswer(UserMessage.AnswerType.SUCCESS, null));
        } else {
            sendMessage(Collections.singleton(id), new ReLoginAnswer(UserMessage.AnswerType.ERROR, "error"));
            return false;
        }
    }

    public static boolean logoutClient(Integer id) {
        return LoginManager.logoutClient(id);
    }

    public static boolean isAuthenticated(Integer id, String token) {
        if (!LoginManager.isLogged(id)) {
            return false;
        }
        return LoginManager.getClientToken(id).equals(token);
    }

    public static Collection<Integer> getActiveClients() {
        return new HashSet(clientCommunicationThreads.keySet());
    }
}
