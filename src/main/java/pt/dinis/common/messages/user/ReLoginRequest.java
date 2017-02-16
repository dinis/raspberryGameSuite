package pt.dinis.common.messages.user;

/**
 * Created by tiago on 16-02-2017.
 */
public class ReLoginRequest implements UserMessage {

    private String token;

    public ReLoginRequest(String token) {
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
    public String toString() {
        return "ReLoginRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
