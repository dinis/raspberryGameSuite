package pt.dinis.common.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import java.io.Serializable;

/**
 * Created by tiago on 16-02-2017.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeIdResolver(MessagesIdResolver.class)
public interface GenericMessage extends Serializable {

    enum Direction {SERVER_TO_CLIENT, CLIENT_TO_SERVER}

    Direction getDirection();

}
