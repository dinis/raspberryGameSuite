package pt.dinis.common.messages.invite;

import pt.dinis.common.objects.Invite;

import java.util.Collection;

/**
 * Created by tiago on 16-02-2017.
 */
public class ListOfInvitesAnswer extends InviteMessage {

    AnswerType answer;
    // TODO: justification should be an enum
    String errorJustification;
    Collection<Invite> invites;

    public ListOfInvitesAnswer(AnswerType answer, String errorJustification, Collection<Invite> invites) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.answer = answer;
        this.errorJustification = errorJustification;
        this.invites = invites;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    public String getErrorJustification() {
        return errorJustification;
    }

    public Collection<Invite> getInvites() {
        return invites;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "ListOfInvitesAnswer{" +
                "answer=" + answer +
                ", errorJustification='" + errorJustification + '\'' +
                ", invites=" + invites +
                "} " + super.toString();
    }
}
