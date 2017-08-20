package pt.dinis.server.core;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;
import pt.dinis.common.objects.Player;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.user.LoginAnswer;
import pt.dinis.common.messages.user.ReLoginAnswer;
import pt.dinis.common.messages.user.RegisterAnswer;
import pt.dinis.common.messages.user.UserMessage;
import pt.dinis.server.communication.ClientCommunicationThread;
import pt.dinis.server.exceptions.NotFoundException;

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
                Integer connectionId = generateUniqueId();
                try {
                    ClientCommunicationThread client = new ClientCommunicationThread(socket, connectionId);

                    if (!addClient(connectionId, client)) {
                        client.close();
                        removeClient(connectionId);
                        Display.alert("Error creating client " + connectionId);
                    } else {
                        client.start();
                        Display.info("New client " + connectionId);
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
    Adds a client to our map, returns false if the connection id existed already
     */
    private static Boolean addClient(Integer connectionId, ClientCommunicationThread clientCommunicationThread) {
        logger.info("Creating client " + connectionId);
        if(clientCommunicationThreads.containsKey(connectionId)) {
            logger.warn("Failed to create client " + connectionId);
            return false;
        }
        clientCommunicationThreads.put(connectionId, clientCommunicationThread);
        return true;
    }

    private synchronized Integer generateUniqueId() {
        return uniqueId ++;
    }

    /*
    Remove client connection id
     */
    private static Boolean removeClient(Integer connectionId) {
        logger.info("Removing client " + connectionId);
        if(clientCommunicationThreads.containsKey(connectionId)) {
            clientCommunicationThreads.remove(connectionId);
            return true;
        }
        return false;
    }

    /*
    This method will send the message to every element in list ids
     */
    public static Boolean sendMessageToConnection(Collection<Integer> connectionIds, GenericMessage message) {
        logger.info("Sending message [" + message + "] to clients [" + connectionIds.toString() + "]." );
        boolean result = true;
        for (Integer connectionId: connectionIds) {
            if (!clientCommunicationThreads.containsKey(connectionId)) {
                result = false;
                logger.warn("Trying to send message [" + message + "] to client " + connectionId + ". Client do not exist.");
            } else {
                ClientCommunicationThread client = clientCommunicationThreads.get(connectionId);
                if (!client.sendMessage(message)) {
                    result = false;
                    logger.warn("Sending message [" + message + "] to client " + connectionId + " failed.");
                }
            }
        }
        return result;
    }

    /*
    Simpler method to send message to only one client
     */
    public static Boolean sendMessageToConnection(Integer connectionId, GenericMessage message) {
        return sendMessageToConnection(Collections.singleton(connectionId), message);
    }

    /*
    Method to send message to a given player
     */
    public static Boolean sendMessage(Integer playerId, GenericMessage message) {
        Collection<Integer> connectionIds = LoginManager.getConnectionIds(playerId);
        return sendMessageToConnection(connectionIds, message);
    }

    /*
    Method to send message to given players
     */
    public static Boolean sendMessage(Collection<Integer> playerIds, GenericMessage message) {
        Collection<Integer> connectionIds = LoginManager.getConnectionIds(playerIds);
        return sendMessageToConnection(connectionIds, message);
    }

    public static boolean disconnectClient(int connectionId) {
        boolean result = true;

        if(!clientCommunicationThreads.containsKey(connectionId)) {
            return false;
        }

        if(!clientCommunicationThreads.get(connectionId).close()){
            logger.warn("Could not disconnect client " + connectionId);
            result = false;
        }

        if(!removeClient(connectionId)) {
            logger.warn("Could not remove client " + connectionId);
            result = false;
        }

        Display.info("Disconnect client " + connectionId);
        return result;
    }

    public static boolean loginClient(Integer connectionId, Player player) {
        try {
            String token = LoginManager.loginClient(connectionId, player);
            return sendMessageToConnection(Collections.singleton(connectionId), new LoginAnswer(UserMessage.AnswerType.SUCCESS, token, player, null));
        } catch (Exception e) {
            Display.alert("Could not log in client " + connectionId);
            sendMessageToConnection(Collections.singleton(connectionId), new LoginAnswer(UserMessage.AnswerType.ERROR, null, null, "error"));
            return false;
        }
    }

    public static boolean registerClient(Integer connectionId, Player player) {
        try {
            String token = LoginManager.loginClient(connectionId, player);
            return sendMessageToConnection(Collections.singleton(connectionId), new RegisterAnswer(UserMessage.AnswerType.SUCCESS, token, player, null));
        } catch (Exception e) {
            Display.alert("Could not log in new client " + connectionId);
            sendMessageToConnection(Collections.singleton(connectionId), new RegisterAnswer(UserMessage.AnswerType.ERROR, null, null, "error"));
            return false;
        }
    }

    public static boolean reloginClient(Integer connectionId, String token) {
        if (LoginManager.reloginClient(connectionId, token)) {
            return sendMessageToConnection(Collections.singleton(connectionId), new ReLoginAnswer(UserMessage.AnswerType.SUCCESS, null));
        } else {
            sendMessageToConnection(Collections.singleton(connectionId), new ReLoginAnswer(UserMessage.AnswerType.ERROR, "error"));
            return false;
        }
    }

    public static boolean logoutClient(Integer connectionId) {
        return LoginManager.logoutClient(connectionId);
    }

    public static boolean isAuthenticated(Integer connectionId, String token) {
        if (!LoginManager.isLogged(connectionId)) {
            return false;
        }
        return LoginManager.getClientToken(connectionId).equals(token);
    }

    public static Player getPlayer(Integer connectionId) {
        try {
            return LoginManager.getPlayer(connectionId);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public static Collection<Player> getActivePlayers() {
        return LoginManager.getPlayers(clientCommunicationThreads.keySet());
    }

    public static Collection<Integer> getActiveClients() {
        return new HashSet(clientCommunicationThreads.keySet());
    }
}
