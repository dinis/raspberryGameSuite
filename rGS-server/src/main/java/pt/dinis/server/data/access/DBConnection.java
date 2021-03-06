package pt.dinis.server.data.access;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import pt.dinis.common.core.Configurations;

/**
 * Created by diogo on 04-02-2017.
 */
public class DBConnection {

    private final static Logger logger = Logger.getLogger(DBConnection.class);

    private static String username;
    private static String password;
    private static String url;

    private Connection connection;

    public static void prepareDBAccess() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        username = Configurations.getProperty("db.user");
        password = Configurations.getProperty("db.pass");
        url = "jdbc:postgresql://" + Configurations.getProperty("db.hostname") + "/" + Configurations.getProperty("db.path");
    }

    public static void testConnection()  throws ConnectException {
        DBConnection connection = new DBConnection();
        try {
            connection.openConnection();
        } catch (Exception e) {
            logger.warn("Can't have access to the database.", e);
            throw new ConnectException();
        }

        try {
            connection.closeConnection();
        } catch (Exception ex) {
            logger.warn("Can't close the database connection.", ex);
            throw new ConnectException();
        }
        return;
    }

    public Connection openConnection() throws SQLException {
        return connection = DriverManager.getConnection(url, username, password);
    }

    public void closeConnection() throws SQLException {
        if(!connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}