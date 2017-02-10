package pt.dinis.temporary;

import org.apache.log4j.Logger;
import pt.dinis.dataaccess.DBConnection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by diogo on 05-02-2017.
 */
public abstract class WorkerThread extends Thread {
    private final static Logger logger = Logger.getLogger(WorkerThread.class);

    private DBConnection connection;
    private List<String> words;

    public WorkerThread(List<String> words) {
        this.words = words;
    }

    @Override
    public void run() {
        connection = new DBConnection();
        try {
            connection.openConnection();
        } catch (SQLException e) {
            logger.warn("Can't open the database connection.");
            return;
        }

        try {
            connection.getConnection().setAutoCommit(false);

            working(words, connection);

            connection.getConnection().commit();
            logger.info("New database transaction committed.");
        } catch (SQLException e) {
            try {
                logger.warn("New database transaction not committed, rolling back.");
                connection.getConnection().rollback();
            } catch (SQLException e1) {
                logger.warn("Can't rollback the commit, unknown behaviour.");
            }
        } finally {
            try {
                connection.closeConnection();
            } catch (SQLException e) {
                logger.warn("Can't close the database connection.");
            }
        }


    }

    protected abstract boolean working(List<String> message, DBConnection connection);

}
