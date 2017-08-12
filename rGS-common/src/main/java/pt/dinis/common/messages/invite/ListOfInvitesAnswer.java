package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Game;

import java.util.Collection;

/**
 * Created by tiago on 16-02-2017.
 */
public class ListOfInvitesAnswer extends InviteMessage {

    AnswerType answer;
    // TODO: justification should be an enum
    String errorJustification;
    Collection<Game> games;

    public ListOfInvitesAnswer(AnswerType answer, String errorJustification, Collection<Game> games) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.answer = answer;
        this.errorJustification = errorJustification;
        this.games = games;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    public String getErrorJustification() {
        return errorJustification;
    }

    public Collection<Game> getGames() {
        return games;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "ListOfGamesAnswer{" +
                "answer=" + answer +
                ", errorJustification='" + errorJustification + '\'' +
                ", games=" + games +
                "} " + super.toString();
    }
}
