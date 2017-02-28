package pt.dinis.common.messages;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * Created by tiago on 27-02-2017.
 */
public class MessagesUtils {

    private static ObjectMapper mapper;

    static private ObjectMapper getJSONMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.enableDefaultTyping();

        return mapper;
    }

    static private ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = getJSONMapper();
        }
        return mapper;
    }

    static public String encode(GenericMessage message) throws JsonProcessingException {
        mapper = getMapper();

        return mapper.writeValueAsString(message);
    }

    static public GenericMessage decode(String json) throws IOException {
        mapper = getMapper();

        return mapper.readValue(json, GenericMessage.class);
    }

}
