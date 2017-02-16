package pt.dinis.common.messages.user;

/**
 * Created by tiago on 16-02-2017.
 */
public class LogoutOrder implements UserMessage {

    public LogoutOrder() {
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "LogoutOrder{}";
    }
}
