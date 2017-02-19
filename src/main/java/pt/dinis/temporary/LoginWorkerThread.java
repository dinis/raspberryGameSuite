package pt.dinis.temporary;

import pt.dinis.dataaccess.User;
import pt.dinis.exceptions.NotFoundException;
import pt.dinis.main.Display;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by diogo on 10-02-2017.
 */
public class LoginWorkerThread extends WorkerThread {

    private String name;
    private String password;
    private Boolean fail;

    public LoginWorkerThread (String name, String password, String fail) {
        this.name = name;
        this.password = password;
        this.fail = fail.toLowerCase().equals("fail");
    }

    @Override
    protected boolean working(Connection connection)
            throws SQLException, InterruptedException, NotFoundException {

        Display.display("[" + name + "] About to start");
        User.setNewUser(name, password, connection);
        Display.display("[" + name + "] Before sleeping");

        Thread.sleep(30000);

        Display.display("[" + name + "] Getting all users");
        Map<String, String> users = User.getAllNames(connection);
        Display.display("[" + name + "] All users:");
        for(Map.Entry<String, String> entry: users.entrySet()) {
            Display.cleanColor(entry.getKey() + " ::: " + entry.getValue());
        }

        Thread.sleep(30000);

        Display.display("[" + name + "] Checking itself");
        String pass = User.getPasswordWithName(name, connection);
        Display.display("[" + name + "] Check: " + pass.equals(password));

        if (fail) {
            throw new SQLException();
        }
        return pass.equals(password);
    }
}
