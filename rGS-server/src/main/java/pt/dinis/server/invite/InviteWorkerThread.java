package pt.dinis.server.invite;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;
import pt.dinis.common.core.Player;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.chat.ChatMessageToClient;
import pt.dinis.common.messages.invite.*;
import pt.dinis.common.messages.user.*;
import pt.dinis.server.core.Dealer;
import pt.dinis.server.core.WorkerThread;
import pt.dinis.server.data.access.User;
import pt.dinis.server.exceptions.NotFoundException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

/**
 * Created by diogo on 10-02-2017.
 */
public class InviteWorkerThread extends WorkerThread {

    private final static Logger logger = Logger.getLogger(InviteWorkerThread.class);

    private InviteMessage message;
    private int id;
    private Player player;

    public InviteWorkerThread(InviteMessage message, int id, Player player) {
        this.message = message;
        this.id = id;
        this.player = player;
    }

    @Override
    protected boolean working(Connection connection) throws SQLException, NotFoundException {
        if (message instanceof ListOfPlayersRequest) {
            return listOfPlayers((ListOfPlayersRequest) message);
        } else if (message instanceof ListOfInvitesRequest) {
            return listOfInvites((ListOfInvitesRequest) message);
        } else if (message instanceof Invite) {
            return invite((Invite) message);
        } else if (message instanceof RespondToInvite) {
            return respondToInvite((RespondToInvite) message);
        } else {
            logger.warn("Unexpected message from client " + id + ": " + message);
            return false;
        }
    }

    private boolean listOfPlayers(ListOfPlayersRequest request) {

        if (player == null) {
            return Dealer.sendMessage(Collections.singletonList(id),
                new ListOfPlayersAnswer(GenericMessage.AnswerType.ERROR, "No Authentication", null));
        }
        return Dealer.sendMessage(Collections.singletonList(id),
                new ListOfPlayersAnswer(GenericMessage.AnswerType.SUCCESS, null, Dealer.getActivePlayers()));

    }

    private boolean listOfInvites(ListOfInvitesRequest request) {
        Display.alert(request.toString());
        return Dealer.sendMessage(Collections.singletonList(id),
                new ChatMessageToClient("Not implemented yet", ChatMessage.ChatMessageType.ERROR));
    }

    private boolean invite(Invite request) {
        Display.alert(request.toString());
        return Dealer.sendMessage(Collections.singletonList(id),
                new ChatMessageToClient("Not implemented yet", ChatMessage.ChatMessageType.ERROR));
    }

    private boolean respondToInvite(RespondToInvite request) {
        Display.alert(request.toString());
        return Dealer.sendMessage(Collections.singletonList(id),
                new ChatMessageToClient("Not implemented yet", ChatMessage.ChatMessageType.ERROR));
    }
}
