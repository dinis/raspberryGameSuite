package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Game;
import pt.dinis.common.core.GameType;
import pt.dinis.common.core.Player;

import java.util.List;

/**
 * Created by tiago on 16-02-2017.
 */
public class InviteAnswer extends InviteMessage {

    AnswerType answer;
    Game game;
    // TODO: justification should be an enum
    String errorJustification;

    public InviteAnswer(AnswerType answer, Game game, String errorJustification) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.answer = answer;
        this.errorJustification = errorJustification;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return "InviteAnswer{" +
                "answer=" + answer +
                ", game=" + game +
                ", errorJustification='" + errorJustification + '\'' +
                "} " + super.toString();
    }

    public Game getGame() {
        return game;
    }

    public String getErrorJustification() {
        return errorJustification;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }
}