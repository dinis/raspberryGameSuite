package pt.dinis.common.messages;

/**
 * Created by tiago on 12-02-2017.
 */
public class ChatExampleMessage implements GenericMessage {

    private String message;

    public ChatExampleMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Direction getDirection() {
       return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "ChatExampleMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
