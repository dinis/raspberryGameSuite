package pt.dinis.common.messages.basic;

import pt.dinis.common.messages.GenericMessage;

/**
 * Created by tiago on 16-02-2017.
 */
public abstract class BasicMessage implements GenericMessage {

    @Override
    public String toString() {
        return "BasicMessage{}";
    }
}
