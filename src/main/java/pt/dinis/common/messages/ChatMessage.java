package pt.dinis.common.messages;

import java.io.Serializable;

/**
 * Created by tiago on 12-02-2017.
 */
public class ChatMessage implements GenericMessage {

    private String message;

    public ChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
