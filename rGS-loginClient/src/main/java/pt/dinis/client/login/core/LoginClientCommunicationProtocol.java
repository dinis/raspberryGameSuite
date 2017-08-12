package pt.dinis.client.login.core;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;
import pt.dinis.common.core.Game;
import pt.dinis.common.core.Player;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.basic.BasicMessage;
import pt.dinis.common.messages.basic.CloseConnectionOrder;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.chat.ChatMessageToClient;
import pt.dinis.common.messages.invite.*;
import pt.dinis.common.messages.user.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by tiago on 03-02-2017.
 */
public class LoginClientCommunicationProtocol {

    private final static Logger logger = Logger.getLogger(LoginClientCommunicationProtocol.class);

    public static boolean protocol(GenericMessage message) {

        try {
            if (message instanceof UserMessage) {
                return userProtocol((UserMessage) message);
            } else if (message instanceof ChatMessage) {
                return chatProtocol((ChatMessage) message);
            } else if (message instanceof BasicMessage) {
                return basicProtocol((BasicMessage) message);
            } else if (message instanceof InviteMessage) {
                return inviteProtocol((InviteMessage) message);
            } else {
                Display.alert("Unexpected message: " + message);
                logger.warn("Unexpected message from server: " + message);
                return false;
            }
        } catch (Exception e) {
            Display.alert("error in message '" + message + "' from server.");
            logger.error("Error interpreting message '" + message + "'", e);
            return false;
        }
    }

    private static boolean userProtocol(UserMessage message) {
        if (message instanceof LoginAnswer) {
            return login((LoginAnswer) message);
        }
        if (message instanceof RegisterAnswer) {
            return register((RegisterAnswer) message);
        }
        if (message instanceof LogoutOrder) {
            return logout();
        }
        if (message instanceof ReLoginAnswer) {
            return relogin((ReLoginAnswer) message);
        }
        return false;
    }

    private static boolean basicProtocol(BasicMessage message) {
        if (message instanceof CloseConnectionOrder) {
            return close();
        }
        logger.warn("Unexpected message from server: " + message);
        return false;
    }

    private static boolean chatProtocol(ChatMessage message) {
        if (message instanceof ChatMessageToClient) {
            return message(message.getMessage(), message.getType());
        }
        logger.warn("Unexpected message from server: " + message);
        return false;
    }

    private static boolean inviteProtocol(InviteMessage message) {
        if (message instanceof ListOfPlayersAnswer) {
            return listOfPlayers((ListOfPlayersAnswer) message);
        }
        if (message instanceof ListOfInvitesAnswer) {
            return listOfInvites((ListOfInvitesAnswer) message);
        }
        if (message instanceof BroadcastInvite) {
            return broadcastInvite((BroadcastInvite) message);
        }
        if (message instanceof DeliverInvite) {
            return deliverInvite((DeliverInvite) message);
        }
        if (message instanceof InviteAnswer) {
            return inviteAnswer((InviteAnswer) message);
        }
        if (message instanceof BroadcastResponseToInvite) {
            return broadcastAnswer((BroadcastResponseToInvite) message);
        }
        if (message instanceof RespondToInviteAnswer) {
            return responseAnswer((RespondToInviteAnswer) message);
        }

        return Display.alert("Unknown message: " + message.toString());
    }

    private static boolean login(LoginAnswer message) {
        switch(message.getAnswer()) {
            case SUCCESS:
                Display.info("New token");
                LoginClient.setMe(message.getPlayer());
                return LoginClient.setToken(message.getToken());
            case ERROR:
                Display.alert("Login refused: " + message.getErrorJustification());
                return false;
        }
        return false;
    }

    private static boolean register(RegisterAnswer message) {
        switch(message.getAnswer()) {
            case SUCCESS:
                Display.info("New token");
                LoginClient.setMe(message.getPlayer());
                return LoginClient.setToken(message.getToken());
            case ERROR:
                Display.alert("Register refused: " + message.getErrorJustification());
                return false;
        }
        return false;
    }

    private static boolean relogin(ReLoginAnswer message) {
        switch(message.getAnswer()) {
            case SUCCESS:
                Display.info("Relogin success");
                return true;
            case ERROR:
                Display.alert("Relogin refused: " + message.getErrorJustification());
                return false;
        }
        return false;
    }

    private static boolean logout() {
        Display.info("Logged out");
        return LoginClient.logout();
    }

    private static boolean close() {
        Display.alert("Sent out by server");
        return LoginClient.disconnect();
    }

    private static boolean message(String message, ChatMessage.ChatMessageType type) {
        switch(type) {
            case NORMAL:
                Display.display(message);
                break;
            case ERROR:
                Display.alert(message);
                break;
        }
        return true;
    }

    private static boolean listOfPlayers(ListOfPlayersAnswer message) {
        switch (message.getAnswer()) {
            case SUCCESS:
                Display.info("List of players: ");
                for (Player player: message.getPlayers()) {
                    Display.cleanColor(player.toString());
                }
                break;
            case ERROR:
                failOperation(message, message.getErrorJustification(), null);
                break;
        }
        return true;
    }

    private static boolean listOfInvites(ListOfInvitesAnswer message) {
        switch (message.getAnswer()) {
            case SUCCESS:
                Display.info("List of invites: ");
                for (Game game: message.getGames()) {
                    Display.cleanColor(game.toString());
                }
                break;
            case ERROR:
                failOperation(message, message.getErrorJustification(), null);
                break;
        }
        return true;
    }

    private static boolean inviteAnswer(InviteAnswer message) {
        switch (message.getAnswer()) {
            case SUCCESS:
                Display.info("New game created: " + message.getGame());
                break;
            case ERROR:
                failOperation(message, message.getErrorJustification(), message.getGame());
                break;
        }
        return true;
    }

    private static boolean deliverInvite(DeliverInvite message) {
        Display.info("You've been invited to: " + message.getGame());
        return true;
    }

    private static boolean broadcastInvite(BroadcastInvite message) {
        Display.info("A new game have been created: " + message.getGame());
        return true;
    }

    private static boolean responseAnswer(RespondToInviteAnswer message) {
        switch (message.getAnswer()) {
            case SUCCESS:
                Display.info("Answer was registered: " + message.getGame());
                break;
            case ERROR:
                failOperation(message, message.getJustificationError(), message.getGame());
                break;
        }
        return true;
    }

    private static boolean broadcastAnswer(BroadcastResponseToInvite message) {
        Display.info(message.getPlayer().toString() + "Answer: " + message.getAccept().toString());
        Display.cleanColor(message.getGame().toString());
        return true;
    }

    private static void failOperation(GenericMessage message, String errorMessage, Object object) {
        Display.alert(message.getClass().getSimpleName() + ": " + errorMessage);
        if (object != null) {
            Display.cleanColor(object.toString());
        }
    }
}
