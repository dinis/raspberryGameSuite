package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Game;
import pt.dinis.common.core.Player;

/**
 * Created by tiago on 16-02-2017.
 */
public class RespondToInviteAnswer extends InviteMessage {

    AnswerType answer;
    Game game;
    String justificationError;

    public RespondToInviteAnswer(AnswerType answer, Game game, String justificationError) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.answer = answer;
        if (game == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.game = game;
        this.justificationError = justificationError;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerType answer) {
        this.answer = answer;
    }

    public String getJustificationError() {
        return justificationError;
    }

    public void setJustificationError(String justificationError) {
        this.justificationError = justificationError;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "RespondToInviteAnswer{" +
                "answer=" + answer +
                ", game=" + game +
                ", justificationError='" + justificationError + '\'' +
                "} " + super.toString();
    }
}
