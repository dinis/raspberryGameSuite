package pt.dinis.dataaccess;

import pt.dinis.exceptions.NotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by diogo on 04-02-2017.
 */
public class User {
    //TODO
    // getDate

    public static void setPassword(String password, int id, Connection connection) throws SQLException {
        String query = "INSERT INTO users (password) VALUES (?) WHERE id = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, password);
            prepSt.setInt(2, id);
            prepSt.executeUpdate();
        }
    }

    public static void setNewUser(String username, String password, Connection connection) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, username);
            prepSt.setString(2, password);
            prepSt.executeUpdate();
        }
    }

    public static String getPassword(int id, Connection connection) throws SQLException, NotFoundException {
        String query = "SELECT password FROM users WHERE id = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setInt(1, id);
            ResultSet rs = prepSt.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            }
        }

        throw new NotFoundException();
    }

    public static String getPasswordWithName(String username, Connection connection) throws SQLException, NotFoundException {
        String query = "SELECT password FROM users WHERE username = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, username);
            ResultSet rs = prepSt.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            }
        }

        throw new NotFoundException();
    }

    public static int getId(String username, Connection connection) throws SQLException, NotFoundException {
        String query = "SELECT password FROM users WHERE username = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, username);
            ResultSet rs = prepSt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        throw new NotFoundException();
    }

    public static String getName(int id, Connection connection) throws SQLException, NotFoundException {
        String query = "SELECT username FROM users WHERE id = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setInt(1, id);
            ResultSet rs = prepSt.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }
        }

        throw new NotFoundException();
    }
}