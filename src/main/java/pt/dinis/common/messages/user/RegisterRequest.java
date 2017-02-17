package pt.dinis.common.messages.user;

/**
 * Created by tiago on 16-02-2017.
 */
public class RegisterRequest extends UserMessage {

    private String name;
    private String password;

    public RegisterRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}
