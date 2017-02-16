package pt.dinis.common.messages.chat;

import pt.dinis.common.messages.AuthenticatedMessage;

/**
 * Created by tiago on 12-02-2017.
 */
public class AuthenticatedChatMessageToServer extends ChatMessageToServer implements AuthenticatedMessage {

    String token;

    public AuthenticatedChatMessageToServer(String message, ChatMessageType type, Destiny destiny, Integer person, String token) {
        super(message, type, destiny, person);
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "AuthenticatedChatMessageToServer{" +
                "token='" + token + '\'' +
                "} " + super.toString();
    }
}
