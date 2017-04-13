package pt.dinis.common.messages.chat;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public class ChatMessageToServer extends ChatMessage {

    public enum Destiny {
        ALL, ECHO, SERVER, OTHERS, SPECIFIC
    }

    private Destiny destiny;
    private Integer person;

    public ChatMessageToServer(String message, ChatMessageType type, Destiny destiny) {
        super(message, type);
        if (destiny == null) {
            throw new IllegalArgumentException("Destiny cannot be null");
        }
        if (destiny == Destiny.SPECIFIC) {
            throw new IllegalArgumentException("You cannot choose destiny SPECIFIC explicitly");
        }
        this.destiny = destiny;
    }

    public ChatMessageToServer(String message, ChatMessageType type, Integer person) {
        super(message, type);
        this.destiny = Destiny.SPECIFIC;
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        this.person = person;
    }

    public Destiny getDestiny() {
        return destiny;
    }

    public Integer getPerson() {
        return person;
    }

    @Override
    public GenericMessage.Direction getDirection() {
        return GenericMessage.Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "ChatMessageToServer{" +
                "destiny=" + destiny +
                ", person=" + person +
                "} " + super.toString();
    }
}
