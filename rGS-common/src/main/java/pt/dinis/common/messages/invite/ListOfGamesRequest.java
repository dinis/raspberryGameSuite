package pt.dinis.common.messages.invite;

/**
 * Created by tiago on 16-02-2017.
 */
public class ListOfGamesRequest extends InviteMessage {

    public ListOfGamesRequest() {
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "ListOfGamesRequest{} " + super.toString();
    }
}
