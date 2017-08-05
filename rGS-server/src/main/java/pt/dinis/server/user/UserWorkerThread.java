package pt.dinis.server.user;

import org.apache.log4j.Logger;
import pt.dinis.common.core.Display;
import pt.dinis.common.core.Player;
import pt.dinis.common.messages.user.*;
import pt.dinis.server.core.WorkerThread;
import pt.dinis.server.data.access.User;
import pt.dinis.server.exceptions.NotFoundException;
import pt.dinis.server.core.Dealer;

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
    private Player player;

    public UserWorkerThread(UserMessage message, int id, Player player) {
        this.message = message;
        this.id = id;
        this.player = player;
    }

    @Override
    protected boolean working(Connection connection) throws SQLException, NotFoundException {
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

    private boolean register(RegisterRequest message, Connection connection) throws SQLException, NotFoundException {
        logger.info("Trying to create a new client with name '" + message.getName() + "' for id " + id);
        if (User.checkUser(message.getName(), connection)) {
            return Dealer.sendMessage(Collections.singleton(id),
                    new RegisterAnswer(UserMessage.AnswerType.ERROR, null, null, "Username already taken"));
        }
        Display.info("Create a new client " + id);
        User.createUser(message.getName(), message.getPassword(), connection);
        Player player = User.getPlayer(message.getName(), connection);
        return Dealer.registerClient(id, player);
    }

    private boolean login(LoginRequest message, Connection connection) throws SQLException {
        try {
            String password = User.getPassword(message.getName(), connection);
            if (!password.equals(message.getPassword())) {
                Display.alert("Client " + id + " with name " + message.getName() + " trying to log in with wrong password");
                return Dealer.sendMessage(Collections.singleton(id),
                        new LoginAnswer(UserMessage.AnswerType.ERROR, null, null, "Wrong password"));
            }
            Display.info("Log in client " + id);
            logger.info("Log in client '" + message.getName() + "' and id " + id);
            Player player = User.getPlayer(message.getName(), connection);
            return Dealer.loginClient(id, player);
        } catch (NotFoundException e) {
            Display.alert("Client " + id + " trying to log with unknown name: " + message.getName());
            return Dealer.sendMessage(Collections.singleton(id),
                    new LoginAnswer(UserMessage.AnswerType.ERROR, null, null, "Did not found username"));
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
