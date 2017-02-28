package pt.dinis.common.messages.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by tiago on 16-02-2017.
 */
public class ReLoginRequest extends UserMessage {

    private String token;

    @JsonCreator
    public ReLoginRequest(@JsonProperty("token") String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String
    toString() {
        return "ReLoginRequest{" +
                "token='" + token + '\'' +
                "} " + super.toString();
    }
}
