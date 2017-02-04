package pt.dinis.dataaccess.atomicaccess;

import pt.dinis.exceptions.NotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by diogo on 04-02-2017.
 */
public class User {
    public static String getPassword(String username, Connection connection) throws SQLException, NotFoundException {
        String query = "SELECT password FROM users WHERE name = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, username);
            ResultSet rs = prepSt.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            }
        }

        throw new NotFoundException();
    }

    public static void register(String username, String password, Connection connection) throws SQLException {
        String query = "INSERT INTO users (name, password) VALUES (?, ?)";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setString(1, username);
            prepSt.setString(1, password);
            prepSt.executeUpdate();
        }
    }

    public static int getId(String username, Connection connection) throws SQLException, NotFoundException {
        String query = "SELECT password FROM users WHERE name = ?";

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
        String query = "SELECT password FROM users WHERE name = ?";

        try (PreparedStatement prepSt = connection.prepareStatement(query)) {
            prepSt.setInt(1, id);
            ResultSet rs = prepSt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        }

        throw new NotFoundException();
    }
}
