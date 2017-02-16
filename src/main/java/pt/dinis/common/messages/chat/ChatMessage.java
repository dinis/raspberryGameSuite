package pt.dinis.common.messages.chat;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public interface ChatMessage extends GenericMessage {

    enum ChatMessageType {
        NORMAL,
        ERROR
    }

    String getMessage();
}
