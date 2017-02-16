package pt.dinis.common.messages.user;

/**
 * Created by tiago on 16-02-2017.
 */
public class ReLoginAnswer implements UserMessage {

    AnswerType answer;
    // TODO: this should be an enum
    String errorJustification;

    public ReLoginAnswer(AnswerType answer, String errorJustification) {
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
                '}';
    }
}
