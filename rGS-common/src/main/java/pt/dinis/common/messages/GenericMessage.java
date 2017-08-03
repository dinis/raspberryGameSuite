package pt.dinis.common.messages;

import java.io.Serializable;

/**
 * Created by tiago on 16-02-2017.
 */
public interface GenericMessage extends Serializable {

    enum Direction {SERVER_TO_CLIENT, CLIENT_TO_SERVER}

    enum AnswerType{SUCCESS, ERROR}

    Direction getDirection();

}
