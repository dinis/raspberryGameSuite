package pt.dinis.common.messages.user;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public abstract class UserMessage implements GenericMessage {

    public enum AnswerType{
        SUCCESS,
        ERROR
    }

    @Override
    public String toString() {
        return "UserMessage{}";
    }
}
