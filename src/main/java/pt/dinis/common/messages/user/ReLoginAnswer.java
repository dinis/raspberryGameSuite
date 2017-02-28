package pt.dinis.common.messages.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by tiago on 16-02-2017.
 */
public class ReLoginAnswer extends UserMessage {

    AnswerType answer;
    // TODO: this should be an enum
    String errorJustification;

    @JsonCreator
    public ReLoginAnswer(@JsonProperty("answer") AnswerType answer, @JsonProperty("errorJustification") String errorJustification) {
        this.answer = answer;
        this.errorJustification = errorJustification;
    }

    public AnswerType getAnswer() {
        return answer;
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
        return "ReLoginAnswer{" +
                "answer=" + answer +
                ", errorJustification='" + errorJustification + '\'' +
                "} " + super.toString();
    }
}
