package pt.dinis.common.messages.user;

import pt.dinis.common.core.Player;

/**
 * Created by tiago on 16-02-2017.
 */
public class LoginAnswer extends UserMessage {

    AnswerType answer;
    String token;
    // TODO: this should be an enum
    String errorJustification;
    Player player;

    public LoginAnswer(AnswerType answer, String token, Player player, String errorJustification) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.answer = answer;
        this.token = token;
        this.player = player;
        this.errorJustification = errorJustification;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    public String getToken() {
        return token;
    }

    public Player getPlayer() {
        return player;
    }

    public String getErrorJustification() {
        return errorJustification;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "LoginAnswer{" +
                "answer=" + answer +
                ", token='" + token + '\'' +
                ", errorJustification='" + errorJustification + '\'' +
                ", player=" + player +
                "} " + super.toString();
    }
}
