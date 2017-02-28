package pt.dinis.common.messages.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by tiago on 16-02-2017.
 */
public class RegisterAnswer extends UserMessage {

    AnswerType answer;
    String token;
    // TODO: this should be an enum
    String errorJustification;

    @JsonCreator
    public RegisterAnswer(@JsonProperty("answer") AnswerType answer, @JsonProperty("token") String token, @JsonProperty("errorJustification") String errorJustification) {
        this.answer = answer;
        this.token = token;
        this.errorJustification = errorJustification;
    }

    public AnswerType getAnswer() {
        return answer;
    }

    public String getToken() {
        return token;
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
        return "RegisterAnswer{" +
                "answer=" + answer +
                ", token='" + token + '\'' +
                ", errorJustification='" + errorJustification + '\'' +
                "} " + super.toString();
    }
}
