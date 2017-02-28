package pt.dinis.common.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Created by tiago on 16-02-2017.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface GenericMessage extends Serializable {

    enum Direction {SERVER_TO_CLIENT, CLIENT_TO_SERVER}

    Direction getDirection();

}
