package pt.dinis.common.messages.user;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by tiago on 16-02-2017.
 */
public class LogoutOrder extends UserMessage {

    @JsonCreator
    public LogoutOrder() {
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "LogoutOrder{} " + super.toString();
    }
}
