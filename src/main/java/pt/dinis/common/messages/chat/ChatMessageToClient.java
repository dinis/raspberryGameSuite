package pt.dinis.common.messages.chat;
/**
 * Created by tiago on 16-02-2017.
 */
public class ChatMessageToClient extends ChatMessage {

    private ChatMessageType type;

    public ChatMessageToClient(String message, ChatMessageType type) {
        super(message);
        this.type = type;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "ChatMessageToClient{" +
                "type=" + type +
                "} " + super.toString();
    }
}
