package pt.dinis.common.messages.user;

/**
 * Created by tiago on 16-02-2017.
 */
public class LogoutRequest implements UserMessage {

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "LogoutRequest{}";
    }
}
