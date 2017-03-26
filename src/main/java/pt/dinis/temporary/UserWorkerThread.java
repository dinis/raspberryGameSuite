package pt.dinis.temporary;

import org.apache.log4j.Logger;
import pt.dinis.common.Display;
import pt.dinis.common.messages.user.*;
import pt.dinis.data.access.User;
import pt.dinis.exceptions.NotFoundException;
import pt.dinis.main.Dealer;
import pt.dinis.main.LoginManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

/**
 * Created by diogo on 10-02-2017.
 */
public class UserWorkerThread extends WorkerThread {

    private final static Logger logger = Logger.getLogger(UserWorkerThread.class);

    private UserMessage message;
    private int id;
    private boolean isAuthenticated;

    public UserWorkerThread(UserMessage message, int id, boolean isAuthenticated) {
        this.message = message;
        this.id = id;
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    protected boolean working(Connection connection) throws SQLException {
        if (message instanceof LoginRequest) {
            login((LoginRequest) message, connection);
        } else if (message instanceof RegisterRequest) {
            return register((RegisterRequest) message, connection);
        } else if (message instanceof LogoutRequest) {
            logout();
        } else if (message instanceof ReLoginRequest) {
            relogin((ReLoginRequest) message);
        } else {
            logger.warn("Unexpected message from client " + id + ": " + message);
            return false;
        }

        return true;
    }

    private boolean register(RegisterRequest message, Connection connection) throws SQLException {
        if (User.checkUserExists(message.getName(), connection)) {
            return Dealer.sendMessage(Collections.singleton(id),
                    new RegisterAnswer(UserMessage.AnswerType.ERROR, null, "Username already taken"));
        }
        User.setNewUser(message.getName(), message.getPassword(), connection);
        String token = LoginManager.loginClient(id);
        return Dealer.sendMessage(Collections.singleton(id),
                new RegisterAnswer(UserMessage.AnswerType.SUCCESS, token, null));
    }

    private boolean login(LoginRequest message, Connection connection) throws SQLException {
        try {
            String password = User.getPasswordWithName(message.getName(), connection);
            if (!password.equals(message.getPassword())) {
                Display.alert("Client " + id + " with name " + message.getName() + " trying to log in with wrong password");
                return Dealer.sendMessage(Collections.singleton(id),
                        new LoginAnswer(UserMessage.AnswerType.ERROR, null, "Wrong password"));
            }
            Display.info("Log in client " + id);
            return Dealer.loginClient(id);
        } catch (NotFoundException e) {
            Display.alert("Client " + id + " trying to log with unknown name: " + message.getName());
            return Dealer.sendMessage(Collections.singleton(id),
                    new LoginAnswer(UserMessage.AnswerType.ERROR, null, "Did not found username"));
        }
    }

    private void relogin(ReLoginRequest message) {
        Display.info("Relog in client " + id + " with token '" + message.getToken() + "'");
        Dealer.reloginClient(id, message.getToken());
    }

    private void logout() {
        Display.info("Log out client " + id);
        Dealer.logoutClient(id);
    }
}
