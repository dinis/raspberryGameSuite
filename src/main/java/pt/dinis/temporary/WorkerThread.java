package pt.dinis.temporary;

import org.apache.log4j.Logger;
import pt.dinis.dataaccess.DBConnection;
import pt.dinis.main.Display;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by diogo on 05-02-2017.
 */
public abstract class WorkerThread extends Thread {
    private final static Logger logger = Logger.getLogger(WorkerThread.class);

    private DBConnection connection;

    @Override
    public void run() {
        connection = new DBConnection();
        try {
            connection.openConnection();
        } catch (Exception e) {
            logger.warn("Can't open the database connection.", e);
            try {
                connection.closeConnection();
            } catch (Exception ex) {
                logger.warn("Can't close the database connection.", ex);
            }
            return;
        }

        try {
            connection.getConnection().setAutoCommit(false);

            working(connection.getConnection());

            connection.getConnection().commit();
            logger.info("New database transaction committed.");
        } catch (Exception e) {
            Display.alert("Rolling back");
            try {
                logger.warn("New database transaction not committed, rolling back.", e);
                connection.getConnection().rollback();
            } catch (SQLException e1) {
                logger.warn("Can't rollback the commit, unknown behaviour.", e);
            }
        } finally {
            try {
                connection.closeConnection();
            } catch (SQLException e) {
                logger.warn("Can't close the database connection.");
            }
        }
    }

    protected abstract boolean working(Connection connection)
            throws Exception;

}
