package pt.dinis.common.messages.basic;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public class CloseConnectionRequest implements BasicMessage {

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "CloseConnectionRequest{}";
    }
}
