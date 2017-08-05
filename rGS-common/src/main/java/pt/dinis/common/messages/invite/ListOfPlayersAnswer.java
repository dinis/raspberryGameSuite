package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Player;

import java.util.Collection;

/**
 * Created by tiago on 16-02-2017.
 */
public class ListOfPlayersAnswer extends InviteMessage {

    AnswerType answer;
    // TODO: justification should be an enum
    String errorJustification;
    Collection<Player> players;

    public ListOfPlayersAnswer(AnswerType answer, String errorJustification, Collection<Player> players) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.answer = answer;
        this.errorJustification = errorJustification;
        this.players = players;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    public String getErrorJustification() {
        return errorJustification;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "ListOfPlayersAnswer{" +
                "answer=" + answer +
                ", errorJustification='" + errorJustification + '\'' +
                ", players=" + players +
                "} " + super.toString();
    }
}
