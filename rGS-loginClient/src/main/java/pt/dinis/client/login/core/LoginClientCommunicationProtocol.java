package pt.dinis.client.login.core;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;
import pt.dinis.common.objects.Game;
import pt.dinis.common.objects.Invite;
import pt.dinis.common.objects.Player;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.basic.BasicMessage;
import pt.dinis.common.messages.basic.CloseConnectionOrder;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.chat.ChatMessageToClient;
import pt.dinis.common.messages.invite.*;
import pt.dinis.common.messages.user.*;

/**
 * Created by tiago on 03-02-2017.
 */
public class LoginClientCommunicationProtocol {

    private final static Logger logger = Logger.getLogger(LoginClientCommunicationProtocol.class);

    public static void protocol(GenericMessage message) {

        try {
            if (message instanceof UserMessage) {
                userProtocol((UserMessage) message);
            } else if (message instanceof ChatMessage) {
                chatProtocol((ChatMessage) message);
            } else if (message instanceof BasicMessage) {
                basicProtocol((BasicMessage) message);
            } else if (message instanceof InviteMessage) {
                inviteProtocol((InviteMessage) message);
            } else {
                Display.alert("Unexpected message: " + message);
                logger.warn("Unexpected message from server: " + message);
            }
        } catch (Exception e) {
            Display.alert("error in message '" + message + "' from server.");
            logger.error("Error interpreting message '" + message + "'", e);
        }
    }

    private static void userProtocol(UserMessage message) {
        if (message instanceof LoginAnswer) {
            login((LoginAnswer) message);
        } else if (message instanceof RegisterAnswer) {
            register((RegisterAnswer) message);
        } else if (message instanceof LogoutOrder) {
            logout();
        } else if (message instanceof ReLoginAnswer) {
            relogin((ReLoginAnswer) message);
        }
    }

    private static void basicProtocol(BasicMessage message) {
        if (message instanceof CloseConnectionOrder) {
            close();
        } else {
            logger.warn("Unexpected message from server: " + message);
        }
    }

    private static void chatProtocol(ChatMessage message) {
        if (message instanceof ChatMessageToClient) {
            message(message.getMessage(), message.getType());
        } else {
            logger.warn("Unexpected message from server: " + message);
        }
    }

    private static void inviteProtocol(InviteMessage message) {
        if (message instanceof ListOfPlayersAnswer) {
            listOfPlayers((ListOfPlayersAnswer) message);
        } else if (message instanceof ListOfInvitesAnswer) {
            listOfInvites((ListOfInvitesAnswer) message);
        } else if (message instanceof BroadcastInvite) {
            broadcastInvite((BroadcastInvite) message);
        } else if (message instanceof DeliverInvite) {
            deliverInvite((DeliverInvite) message);
        } else if (message instanceof InviteAnswer) {
            inviteAnswer((InviteAnswer) message);
        } else if (message instanceof BroadcastResponseToInvite) {
            broadcastAnswer((BroadcastResponseToInvite) message);
        } else if (message instanceof RespondToInviteAnswer) {
            responseAnswer((RespondToInviteAnswer) message);
        } else {
            Display.alert("Unknown message: " + message.toString());
        }
    }

    private static void login(LoginAnswer message) {
        switch(message.getAnswer()) {
            case SUCCESS:
                Display.info("New token");
                LoginClient.setMe(message.getPlayer());
                LoginClient.setToken(message.getToken());
                break;
            case ERROR:
                Display.alert("Login refused: " + message.getErrorJustification());
                break;
        }
    }

    private static void register(RegisterAnswer message) {
        switch(message.getAnswer()) {
            case SUCCESS:
                Display.info("New token");
                LoginClient.setMe(message.getPlayer());
                LoginClient.setToken(message.getToken());
                break;
            case ERROR:
                Display.alert("Register refused: " + message.getErrorJustification());
        }
    }

    private static void relogin(ReLoginAnswer message) {
        switch(message.getAnswer()) {
            case SUCCESS:
                Display.info("Relogin success");
                break;
            case ERROR:
                Display.alert("Relogin refused: " + message.getErrorJustification());
                break;
        }
    }

    private static void logout() {
        Display.info("Logged out");
        LoginClient.logout();
    }

    private static void close() {
        Display.alert("Sent out by server");
        LoginClient.disconnect();
    }

    private static void message(String message, ChatMessage.ChatMessageType type) {
        switch(type) {
            case NORMAL:
                Display.display(message);
                break;
            case ERROR:
                Display.alert(message);
                break;
        }
    }

    private static void listOfPlayers(ListOfPlayersAnswer message) {
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
    }

    private static void listOfInvites(ListOfInvitesAnswer message) {
        switch (message.getAnswer()) {
            case SUCCESS:
                Display.info("List of invites: ");
                for (Invite invite: message.getInvites()) {
                    Display.cleanColor(invite.toString());
                }
                break;
            case ERROR:
                failOperation(message, message.getErrorJustification(), null);
                break;
        }
    }

    private static void inviteAnswer(InviteAnswer message) {
        switch (message.getAnswer()) {
            case SUCCESS:
                Display.info("New game created: " + message.getGame());
                break;
            case ERROR:
                failOperation(message, message.getErrorJustification(), message.getGame());
                break;
        }
    }

    private static void deliverInvite(DeliverInvite message) {
        Display.info("You've been invited to: " + message.getGame());
    }

    private static void broadcastInvite(BroadcastInvite message) {
        Display.info("A new game have been created: " + message.getGame());
    }

    private static void responseAnswer(RespondToInviteAnswer message) {
        switch (message.getAnswer()) {
            case SUCCESS:
                Display.info("Answer was registered: " + message.getGame());
                break;
            case ERROR:
                failOperation(message, message.getJustificationError(), message.getGame());
                break;
        }
    }

    private static void broadcastAnswer(BroadcastResponseToInvite message) {
        Display.info(message.getPlayer().toString() + "Answer: " + message.getAccept().toString());
        Display.cleanColor(message.getGame().toString());
    }

    private static void failOperation(GenericMessage message, String errorMessage, Object object) {
        Display.alert(message.getClass().getSimpleName() + ": " + errorMessage);
        if (object != null) {
            Display.cleanColor(object.toString());
        }
    }
}
