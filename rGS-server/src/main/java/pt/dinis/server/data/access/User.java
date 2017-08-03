package pt.dinis.server.data.access;

import pt.dinis.common.core.Player;
import pt.dinis.server.exceptions.NotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by diogo on 04-02-2017.
 */
public class User {
    public static void createUser(String username, String password, Connection connection) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, username);
            prepSt.setString(2, password);
            prepSt.executeUpdate();
        }
    }

    public static boolean checkUser(String username, Connection connection) throws SQLException  {
        String query = "SELECT username FROM users WHERE username = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, username);
            ResultSet rs = prepSt.executeQuery();

            if (rs.next()) {
                return true;
            }

            return false;
        }
    }

    public static String getPassword(String username, Connection connection) throws SQLException, NotFoundException {
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

    public static Player getPlayer(String username, Connection connection) throws SQLException, NotFoundException {

        String query = "SELECT id, username FROM users WHERE username = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, username);
            ResultSet rs = prepSt.executeQuery();

            if (rs.next()) {
                return new Player(rs.getInt("id"), rs.getString("username"));
            }
        }

        throw new NotFoundException();
    }
}