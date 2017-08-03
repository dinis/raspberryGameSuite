package pt.dinis.common.messages.invite;

import pt.dinis.common.messages.user.UserMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public class ListOfPlayersRequest extends InviteMessage {

    public ListOfPlayersRequest() {
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "ListOfPlayersRequest{} " + super.toString();
    }
}
