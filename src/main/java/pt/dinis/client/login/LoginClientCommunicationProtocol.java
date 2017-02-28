package pt.dinis.client.login;

import org.apache.log4j.Logger;
import pt.dinis.common.Display;
import pt.dinis.common.messages.GenericMessage;
import pt.dinis.common.messages.basic.BasicMessage;
import pt.dinis.common.messages.basic.CloseConnectionOrder;
import pt.dinis.common.messages.chat.ChatMessage;
import pt.dinis.common.messages.chat.ChatMessageToClient;
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
            } else {
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
            throw new NotImplementedException();
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

    private static boolean login(LoginAnswer message) {
        switch(message.getAnswer()) {
            case SUCCESS:
                Display.info("New token");
                return LoginClient.setToken(message.getToken());
            case ERROR:
                Display.alert("Login refused: " + message.getErrorJustification());
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
}
