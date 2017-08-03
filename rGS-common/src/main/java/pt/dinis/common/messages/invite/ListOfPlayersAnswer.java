package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Player;

import java.util.List;

/**
 * Created by tiago on 16-02-2017.
 */
public class ListOfPlayersAnswer extends InviteMessage {

    AnswerType answer;
    // TODO: justification should be an enum
    String errorJustification;
    List<Player> players;

    public ListOfPlayersAnswer(AnswerType answer, String errorJustification, List<Player> players) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.answer = answer;
        this.errorJustification = errorJustification;
        if (players == null) {
            throw new IllegalArgumentException("Players cannot be null");
        }
        this.players = players;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerType answer) {
        this.answer = answer;
    }

    public String getErrorJustification() {
        return errorJustification;
    }

    public void setErrorJustification(String errorJustification) {
        this.errorJustification = errorJustification;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
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
