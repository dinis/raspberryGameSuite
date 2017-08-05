package pt.dinis.server.communication;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;
import pt.dinis.common.core.Player;
import pt.dinis.common.messages.AuthenticatedMessage;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.basic.BasicMessage;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.invite.InviteMessage;
import pt.dinis.common.messages.user.UserMessage;
import pt.dinis.server.core.Dealer;
import pt.dinis.server.basic.BasicWorkerThread;
import pt.dinis.server.chat.ChatWorkerThread;
import pt.dinis.server.invite.InviteWorkerThread;
import pt.dinis.server.user.UserWorkerThread;
import pt.dinis.server.core.WorkerThread;

/**
 * Created by diogo on 05-02-2017.
 */
public class ClientCommunicationProtocol {

    private final static Logger logger = Logger.getLogger(ClientCommunicationProtocol.class);

    public static boolean protocol(GenericMessage message, int id) {

        if (message.getDirection() == GenericMessage.Direction.SERVER_TO_CLIENT) {
            logger.info("Server received message of type server to client: " + message);
            return false;
        }

        Player player = null;
        if (message instanceof AuthenticatedMessage) {
            AuthenticatedMessage authenticatedMessage = (AuthenticatedMessage) message;
            if (authenticatedMessage.isAuthenticated()) {
                if (Dealer.isAuthenticated(id, authenticatedMessage.getToken())) {
                    player = Dealer.getPlayer(id);
                }
            }
            message = authenticatedMessage.getMessage();
        }

        WorkerThread worker;

        try {
            if (message instanceof UserMessage) {
                worker = new UserWorkerThread((UserMessage) message, id, player);
            } else if (message instanceof ChatMessage) {
                worker = new ChatWorkerThread((ChatMessage) message, id, player);
            } else if (message instanceof BasicMessage) {
                worker = new BasicWorkerThread((BasicMessage) message, id, player);
            } else if (message instanceof InviteMessage) {
                worker = new InviteWorkerThread((InviteMessage) message, id, player);
            } else {
                logger.warn("Unexpected message from client " + id + ": " + message);
                return false;
            }
            worker.run();
            return true;
        } catch (Exception e) {
            Display.alert("Error in message from client " + id + ": " + message);
            logger.error("Error interpreting message from client " + id + ": " + message, e);
        }
        return false;
    }
}
