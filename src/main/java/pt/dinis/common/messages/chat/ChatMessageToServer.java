package pt.dinis.common.messages.chat;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public class ChatMessageToServer extends ChatMessage {

    public enum Destiny {
        ALL, ECHO, SERVER, OTHERS, SPECIFIC
    }

    private ChatMessageType type;
    private Destiny destiny;
    private Integer person;

    public ChatMessageToServer(String message, ChatMessageType type, Destiny destiny, Integer person) {
        super(message);
        this.type = type;
        this.destiny = destiny;
        this.person = person;
    }

    public ChatMessageType getType() {
        return type;
    }

    public Destiny getDestiny() {
        return destiny;
    }

    public Integer getPerson() {
        return person;
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "ChatMessageToServer{" +
                "type=" + type +
                ", destiny=" + destiny +
                ", person=" + person +
                "} " + super.toString();
    }
}
