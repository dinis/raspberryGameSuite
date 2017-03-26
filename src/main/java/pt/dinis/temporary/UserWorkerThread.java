package pt.dinis.temporary;

import org.apache.log4j.Logger;
import pt.dinis.common.Display;
import pt.dinis.common.messages.user.*;
import pt.dinis.main.Dealer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;

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
    protected boolean working(Connection connection) {
        if (message instanceof LoginRequest) {
            login((LoginRequest) message);
        } else if (message instanceof RegisterRequest) {
            Display.alert("Register is not implemented");
            // TODO to be implemented
            throw new NotImplementedException();
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

    // TODO add check name and password
    private void login(LoginRequest message) {
        Display.info("Log in client " + id);
        Dealer.loginClient(id);
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
