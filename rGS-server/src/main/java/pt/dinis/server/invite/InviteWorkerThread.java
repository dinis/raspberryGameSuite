package pt.dinis.server.invite;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;
import pt.dinis.common.core.Game;
import pt.dinis.common.core.GameType;
import pt.dinis.common.core.Player;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.invite.*;
import pt.dinis.server.core.Dealer;
import pt.dinis.server.core.WorkerThread;
import pt.dinis.server.data.access.InviteDB;
import pt.dinis.server.exceptions.NotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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
            return invite((Invite) message, connection);
        } else if (message instanceof RespondToInvite) {
            return respondToInvite((RespondToInvite) message);
        } else {
            logger.warn("Unexpected message from client " + id + ": " + message);
            return false;
        }
    }

    private boolean listOfPlayers(ListOfPlayersRequest request) {
        if (player == null) {
            return Dealer.sendMessageToConnection(id,
                new ListOfPlayersAnswer(GenericMessage.AnswerType.ERROR, "No Authentication", null));
        }
        return Dealer.sendMessage(player.getId(),
                new ListOfPlayersAnswer(GenericMessage.AnswerType.SUCCESS, null, Dealer.getActivePlayers()));

    }

    private boolean listOfInvites(ListOfInvitesRequest request) {
        if (player == null) {
            return Dealer.sendMessageToConnection(id,
                    new ListOfInvitesAnswer(GenericMessage.AnswerType.ERROR, "No Authentication", null));
        }
        Display.alert(request.toString());
        // TODO: Go to DB and get list
        return Dealer.sendMessage(player.getId(),
                new ListOfInvitesAnswer(GenericMessage.AnswerType.SUCCESS, null, Collections.emptySet()));
    }

    private boolean invite(Invite request, Connection connection) throws SQLException {
        // Tests if player inviting exists
        if (player == null) {
            return Dealer.sendMessageToConnection(id,
                    new InviteAnswer(GenericMessage.AnswerType.ERROR, null, "No Authentication"));
        }

        // Create game in DB and answer to the one inviting
        Game game;
        try {
            game = InviteDB.createCompleteGame(player, request.getGame(), connection);
        } catch (SQLException e) {
            Display.alert("Error creating game: " + request.getGame() + " for player " + player);
            Dealer.sendMessage(player.getId(),
                    new InviteAnswer(GenericMessage.AnswerType.ERROR, null, "Error creating game"));
            throw e;
        }

        if (request.getPlayers() != null) {
            for (Integer id: request.getPlayers()) {
                try {
                    InviteDB.invitePlayer(id, game, connection);
                } catch (SQLException e) {
                    Display.alert("Error inviting " + id + " for game: " + request.getGame());
                    Dealer.sendMessage(player.getId(),
                            new InviteAnswer(GenericMessage.AnswerType.ERROR, null, "Error inviting " + id));
                    throw e;
                }
            }
        }

        // Alert everyone
        Boolean result = Dealer.sendMessage(player.getId(),
                new InviteAnswer(GenericMessage.AnswerType.SUCCESS, game, null));

        // Broadcast/deliver invite
        if (request.getPlayers().isEmpty()) {
            result = result && Dealer.sendMessageToConnection(Dealer.getActiveClients(),
                    new BroadcastInvite(game, player));
        } else {
            for (Integer pl: request.getPlayers()) {
                result = result && Dealer.sendMessage(pl, new DeliverInvite(game, player));
            }
        }

        return result;
    }

    private boolean respondToInvite(RespondToInvite request) {
        if (player == null) {
            return Dealer.sendMessageToConnection(id,
                    new RespondToInviteAnswer(GenericMessage.AnswerType.ERROR, null, "No Authentication"));
        }

        // TODO: go to DB and register the answer and get the game
        Game game = new Game(null, null);
        Boolean result = Dealer.sendMessage(player.getId(),
                new RespondToInviteAnswer(GenericMessage.AnswerType.SUCCESS, game, null));

        // TODO: get all players that accepted to play
        Collection<Player> opponents = new HashSet<> ();
        for (Player opponent: opponents) {
            if (opponent.getId() != player.getId()) {
                result = result && Dealer.sendMessage(opponent.getId(),
                        new BroadcastResponseToInvite(game, request.getAccept(), player));
            }
        }

        return result;
    }
}
