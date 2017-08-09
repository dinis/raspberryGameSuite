package pt.dinis.server.data.access;

import pt.dinis.common.core.Game;
import pt.dinis.common.core.GameType;
import pt.dinis.common.core.Player;
import pt.dinis.server.data.objects.GameStatus;
import pt.dinis.server.data.objects.InviteStatus;

import java.sql.*;

/**
 * Created by tiago on 08-08-2017.
 */
public class InviteDB {

    public static Game createCompleteGame(Player player, GameType type, Connection connection) throws SQLException {
        Integer gameId = createGame(player.getId(), type, connection);
        createInviteRow(player.getId(), gameId, InviteStatus.INVITING, connection);
        return getGame(gameId, connection);
    }

    public static void invitePlayer(Integer id, Game game, Connection connection) throws SQLException {
        createInviteRow(id, game.getId(), InviteStatus.INVITED, connection);
    }

    private static void createInviteRow(Integer playerId, Integer gameId,
                                        InviteStatus status, Connection connection) throws SQLException {

        String queryInsertInvite = "INSERT INTO games_players (user_id, game_id, status) VALUES (?, ?, ?)";
        try (PreparedStatement prepSt = connection.prepareStatement(queryInsertInvite)) {
            prepSt.setInt(1, playerId);
            prepSt.setInt(2, gameId);
            prepSt.setString(3, status.name());
            int newRows = prepSt.executeUpdate();

            if (newRows == 0) {
                throw new SQLException("Creating game failed, no invite row created");
            }
        }
    }

    private static Game getGame(Integer gameId, Connection connection) throws SQLException {

        String queryGame = "SELECT id, game_type FROM games WHERE id = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryGame)) {
            prepSt.setInt(1, gameId);
            ResultSet games = prepSt.executeQuery();

            if (games.next()) {
                return new Game(games.getInt("id"), GameType.valueOf(games.getString("game_type")));
            } else {
                throw new SQLException("Missing game just created");
            }
        }
    }

    private static Integer createGame(Integer playerId, GameType type, Connection connection) throws SQLException {

        String queryInsertGame = "INSERT INTO games (user_id, game_type, status) VALUES (?, ?, ?)";

        try (PreparedStatement prepSt = connection.prepareStatement(queryInsertGame, Statement.RETURN_GENERATED_KEYS)) {
            prepSt.setInt(1, playerId);
            prepSt.setString(2, type.name());
            prepSt.setString(3, GameStatus.INVITING.name());
            int newRows = prepSt.executeUpdate();

            if (newRows == 0) {
                throw new SQLException("Creating game failed, no modified row");
            }

            try (ResultSet generatedKeys = prepSt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating game failed, no ID obtained.");
                }
            }
        }
    }
}
