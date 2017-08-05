package pt.dinis.server.chat;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;
import pt.dinis.common.core.Player;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.chat.ChatMessageToClient;
import pt.dinis.common.messages.chat.ChatMessageToServer;
import pt.dinis.server.core.Dealer;
import pt.dinis.server.core.WorkerThread;

import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by diogo on 10-02-2017.
 */
public class ChatWorkerThread extends WorkerThread {

    private final static Logger logger = Logger.getLogger(ChatWorkerThread.class);

    private ChatMessage message;
    private int id;
    private Player player;

    public ChatWorkerThread(ChatMessage message, int id, Player player) {
        this.message = message;
        this.id = id;
        this.player = player;
    }

    @Override
    protected boolean working(Connection connection) {
        if (message instanceof ChatMessageToServer) {
            return message((ChatMessageToServer) message);
        } else {
            logger.warn("Unexpected message from client " + id + ": " + message);
            return false;
        }
    }

    private boolean message(ChatMessageToServer message) {

        if(message.getMessage().isEmpty()) {
            Display.alert("client " + id + " sent an empty message");
            return false;
        }

        Collection<Integer> ids = Collections.emptySet();
        switch(message.getDestiny()) {
            case ALL:
                ids = Dealer.getActiveClients();
                break;
            case ECHO:
                ids = Collections.singleton(id);
                break;
            case SERVER:
                break;
            case OTHERS:
                ids = Dealer.getActiveClients();
                ids.remove(id);
                break;
            case SPECIFIC:
                ids = Collections.singleton(message.getPerson());
                break;
        }


        if (message.getDestiny() == ChatMessageToServer.Destiny.SERVER) {
            switch (message.getType()) {
                case NORMAL:
                    Display.display("client " + id + " said: " + message.getMessage());
                    break;
                case ERROR:
                    Display.alert("client " + id + " said: " + message.getMessage());
                    break;
            }
            return true;
        }

        return Dealer.sendMessage(ids, new ChatMessageToClient(message.getMessage(), message.getType()));

    }
}
