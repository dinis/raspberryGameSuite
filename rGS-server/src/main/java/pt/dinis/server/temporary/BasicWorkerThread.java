package pt.dinis.server.temporary;

import org.apache.log4j.Logger;
import pt.dinis.common.messages.basic.BasicMessage;
import pt.dinis.common.messages.basic.CloseConnectionRequest;
import pt.dinis.server.main.Dealer;

import java.sql.Connection;

/**
 * Created by diogo on 10-02-2017.
 */
public class BasicWorkerThread extends WorkerThread {

    private final static Logger logger = Logger.getLogger(BasicWorkerThread.class);

    private BasicMessage message;
    private int id;
    private boolean isAuthenticated;

    public BasicWorkerThread(BasicMessage message, int id, boolean isAuthenticated) {
        this.message = message;
        this.id = id;
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    protected boolean working(Connection connection) {
        if (message instanceof CloseConnectionRequest) {
            close();
        } else {
            logger.warn("Unexpected message from client " + id + ": " + message);
            return false;
        }
        return true;
    }

    private void close() {
        Dealer.disconnectClient(id);
    }
}
