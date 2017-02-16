package pt.dinis.common.messages.chat;
/**
 * Created by tiago on 16-02-2017.
 */
public class ChatMessageToClient implements ChatMessage {

    private String message;
    private ChatMessageType type;

    public ChatMessageToClient(String message, ChatMessageType type) {
        super();
        this.message = message;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ChatMessageType getType() {
        return type;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "ChatMessageToClient{" +
                "message='" + message + '\'' +
                ", type=" + type +
                '}';
    }
}
