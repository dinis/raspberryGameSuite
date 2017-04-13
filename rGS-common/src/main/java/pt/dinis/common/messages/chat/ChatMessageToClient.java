package pt.dinis.common.messages.chat;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public class ChatMessageToClient extends ChatMessage {

    public ChatMessageToClient(String message, ChatMessageType type) {
        super(message, type);
    }

    @Override
    public GenericMessage.Direction getDirection() {
        return GenericMessage.Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "ChatMessageToClient{} " + super.toString();
    }
}
