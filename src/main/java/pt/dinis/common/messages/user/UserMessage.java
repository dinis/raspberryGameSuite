package pt.dinis.common.messages.user;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public interface UserMessage extends GenericMessage {

    enum AnswerType{
        SUCCESS,
        ERROR
    }

}
