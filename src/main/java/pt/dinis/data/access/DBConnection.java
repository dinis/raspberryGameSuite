package pt.dinis.data.access;

import pt.dinis.main.Configurations;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * Created by diogo on 04-02-2017.
 */
public class DBConnection {
    private static String username;
    private static String password;
    private static String url;

    private Connection connection;

    public static void prepareDBAccess() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        username = Configurations.getProperty("db.user");
        password = Configurations.getProperty("db.pass");
        url = "jdbc:postgresql://" + Configurations.getProperty("server.hostname") + "/" + Configurations.getProperty("db.path");
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