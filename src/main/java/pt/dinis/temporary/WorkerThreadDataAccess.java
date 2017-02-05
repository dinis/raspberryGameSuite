package pt.dinis.temporary;

import org.apache.log4j.Logger;
import pt.dinis.dataaccess.DBConnection;
import java.sql.SQLException;

/**
 * Created by diogo on 05-02-2017.
 */
public abstract class WorkerThreadDataAccess extends Thread {
    private final static Logger logger = Logger.getLogger(WorkerThreadDataAccess.class);

    private DBConnection connection;
    private String message;
    private Integer id;

    public WorkerThreadDataAccess(String message, int id) {
        // here we will need to put all the work that have to be donne.
        this.message = message;
        this.id = id;
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

        //things to do with or without data access

        try {
            connection.closeConnection();
        } catch (SQLException e) {
            logger.warn("Can't close the database connection.");
        }
    }

    public abstract boolean worker() throws SQLException;

}
