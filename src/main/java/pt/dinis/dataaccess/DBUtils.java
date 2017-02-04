package pt.dinis.dataaccess;

import pt.dinis.main.Display;
import pt.dinis.temporary.Configurations;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * Created by diogo on 04-02-2017.
 */
public class DBUtils {
    private final static Logger logger = Logger.getLogger(DBUtils.class);

    private static String username;
    private static String password;
    private static String url;

    public static boolean prepareDBAccess() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        username = Configurations.getProperty("db.user");
        password = Configurations.getProperty("db.pass");
        url = "jdbc:postgresql://" + Configurations.getProperty("hostname") + "/" + Configurations.getProperty("db.path");

        return true;
    }

    /*package*/ static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    static void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    public static void example() {
        String insertUser = "INSERT INTO users (name, password) VALUES (?, ?)";
        Connection connection = null;

        try {
            connection = openConnection();

            try (PreparedStatement prepSt = connection.prepareStatement(insertUser)) {
                prepSt.setString(1, "João Sardinha");
                prepSt.setString(2, "carapau_assado");
                int a = prepSt.executeUpdate();
            } catch (SQLException e) {
                Display.alert("Qualquer problema que desconheço porque a db não me diz.");
            }
        } catch (SQLException e) {
            Display.alert("Can't open DB connection");
        } finally {
            try {
                closeConnection(connection);
            } catch (SQLException e) {
                //nothing
            }
        }
    }

}