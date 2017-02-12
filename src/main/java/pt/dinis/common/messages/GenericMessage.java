package pt.dinis.common.messages;

import java.io.Serializable;

/**
 * Created by tiago on 12-02-2017.
 */
public class GenericMessage implements Serializable {

    private String message;

    public GenericMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
