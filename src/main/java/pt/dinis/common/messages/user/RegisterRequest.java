package pt.dinis.common.messages.user;

/**
 * Created by tiago on 16-02-2017.
 */
public class RegisterRequest extends UserMessage {

    private String name;
    private String password;

    public RegisterRequest(String name, String password) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
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
