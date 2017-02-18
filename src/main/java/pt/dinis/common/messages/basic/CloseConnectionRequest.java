package pt.dinis.common.messages.basic;

/**
 * Created by tiago on 16-02-2017.
 */
public class CloseConnectionRequest extends BasicMessage {

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "CloseConnectionRequest{} " + super.toString();
    }
}
