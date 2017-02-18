package pt.dinis.common.messages.chat;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public abstract class ChatMessage implements GenericMessage {

    public enum ChatMessageType {
        NORMAL,
        ERROR
    }

    private ChatMessageType type;
    private String message;

    public ChatMessage(String message, ChatMessageType type) {
        if (message == null) {
            throw new IllegalArgumentException("Message can not be null");
        }
        this.message = message;
        if (type == null) {
            throw new IllegalArgumentException("Type can not be null");
        }
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public ChatMessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
