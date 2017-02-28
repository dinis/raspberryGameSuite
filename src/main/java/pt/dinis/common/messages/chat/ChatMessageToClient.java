package pt.dinis.common.messages.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by tiago on 16-02-2017.
 */
public class ChatMessageToClient extends ChatMessage {

    @JsonCreator
    public ChatMessageToClient(@JsonProperty("message") String message, @JsonProperty("type") ChatMessageType type) {
        super(message, type);
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "ChatMessageToClient{} " + super.toString();
    }
}
