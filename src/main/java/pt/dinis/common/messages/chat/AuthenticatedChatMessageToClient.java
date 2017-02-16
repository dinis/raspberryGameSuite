package pt.dinis.common.messages.chat;

import pt.dinis.common.messages.AuthenticatedMessage;

/**
 * Created by tiago on 12-02-2017.
 */
public class AuthenticatedChatMessageToClient extends ChatMessageToClient implements AuthenticatedMessage {

    String token;

    public AuthenticatedChatMessageToClient(String message, ChatMessageType type, String token) {
        super(message, type);
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "AuthenticatedChatMessageToClient{" +
                "token='" + token + '\'' +
                "} " + super.toString();
    }
}
