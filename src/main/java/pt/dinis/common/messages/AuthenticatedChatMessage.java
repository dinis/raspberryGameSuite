package pt.dinis.common.messages;

/**
 * Created by tiago on 12-02-2017.
 */
public class AuthenticatedChatMessage extends ChatMessage implements AuthenticatedGenericMessage {

    private String token;

    public AuthenticatedChatMessage(String message, String token) {
        super(message);
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }
}
