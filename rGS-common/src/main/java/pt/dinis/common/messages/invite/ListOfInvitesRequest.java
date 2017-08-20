package pt.dinis.common.messages.invite;

/**
 * Created by tiago on 16-02-2017.
 */
public class ListOfInvitesRequest extends InviteMessage {

    public ListOfInvitesRequest() {
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "ListOfInvitesRequest{} " + super.toString();
    }
}
