package pt.dinis.common.messages.invite;

import pt.dinis.common.objects.Game;

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
        this.game = game;
        this.justificationError = justificationError;
    }

    public Game getGame() {
        return game;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    public String getJustificationError() {
        return justificationError;
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
