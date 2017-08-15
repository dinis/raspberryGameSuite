package pt.dinis.server.data.access;

import org.joda.time.DateTime;
import pt.dinis.common.core.Game;
import pt.dinis.common.core.GameType;
import pt.dinis.common.core.Player;
import pt.dinis.server.data.objects.GameStatus;
import pt.dinis.server.data.objects.InviteStatus;
import pt.dinis.server.exceptions.NotFoundException;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by tiago on 08-08-2017.
 */
public class InviteDB {

    public static Game createCompleteGame(Player player, GameType type, boolean publicGame, Connection connection) throws SQLException {
        Integer gameId = createGame(player.getId(), type, publicGame, connection);
        createInviteRow(player.getId(), gameId, InviteStatus.HOST, connection);
        return getOpenGame(gameId, connection);
    }

    public static void invitePlayer(Integer playerId, Game game, Connection connection) throws SQLException {
        createInviteRow(playerId, game.getId(), InviteStatus.INVITED, connection);
    }

    public static Game getOpenGame(Integer gameId, Connection connection) throws SQLException {

        String queryGame = "SELECT g.id, g.game_type, g.user_id, u.username, g.public_game, g.creation_date " +
                "FROM games AS g " +
                "JOIN users AS u ON u.id = g.user_id " +
                "WHERE g.id = ? AND status = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryGame)) {
            prepSt.setInt(1, gameId);
            prepSt.setString(2, GameStatus.OPEN.name());
            ResultSet games = prepSt.executeQuery();

            if (games.next()) {
                return new Game(games.getInt("id"), GameType.valueOf(games.getString("game_type")),
                        new Player(games.getInt("user_id"), games.getString("username")),
                        games.getBoolean("public_game"), new DateTime(games.getTimestamp("creation_date")));
            } else {
                throw new SQLException("Missing open (inviting) game");
            }
        }
    }

    public static Player getGameHost(Game game, Connection connection) throws SQLException {
        String queryGameHost = "SELECT u.id, u.username " +
                "FROM games AS g " +
                "JOIN users AS u ON u.id = g.user_id " +
                "WHERE g.id = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryGameHost)) {
            prepSt.setInt(1, game.getId());
            ResultSet players = prepSt.executeQuery();

            if (players.next()) {
                return new Player(players.getInt("id"), players.getString("username"));
            } else {
                throw new SQLException("Missing game just created");
            }
        }
    }

    public static void cancelGame(Game game, Connection connection) throws SQLException {
        String queryCancelGame = "UPDATE games SET status = ? WHERE id = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryCancelGame)) {
            prepSt.setString(1, GameStatus.CANCELED.name());
            prepSt.setInt(2, game.getId());
            int newRows = prepSt.executeUpdate();

            if (newRows != 1) {
                throw new SQLException("Canceling game failed, updated " + newRows + "games!!");
            }
        }
    }

    public static void registerInviteAnswer(Player player, Game game, Boolean answer, Connection connection)
        throws SQLException {

        InviteStatus status = answer ? InviteStatus.ACCEPTED : InviteStatus.REFUSED;
        try {
            Integer id = getInviteRow(player, game, connection);
            updateInviteRow(id, status, connection);
        } catch (NotFoundException e) {
            createInviteRow(player.getId(), game.getId(), status, connection);
        }
    }

    public static void updateInviteRow(Integer id, InviteStatus status, Connection connection) throws SQLException {
        String queryCancelGame = "UPDATE games_players SET status = ? WHERE id = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryCancelGame)) {
            prepSt.setString(1, status.name());
            prepSt.setInt(2, id);
            int newRows = prepSt.executeUpdate();

            if (newRows != 1) {
                throw new SQLException("Updating invite status failed, updated " + newRows + "games!!");
            }
        }
    }

    public static Collection<Player> getInvitedPlayers(Game game, Connection connection) throws SQLException {

        Collection<Player> players = new HashSet<>();
        String queryGamePlayers = "SELECT u.id, u.username " +
                "FROM games_players AS i " +
                "JOIN users AS u ON u.id = i.user_id " +
                "WHERE i.game_id = ? AND i.status IN (?, ?)";
        try (PreparedStatement prepSt = connection.prepareStatement(queryGamePlayers)) {
            prepSt.setInt(1, game.getId());
            prepSt.setString(2, InviteStatus.HOST.name());
            prepSt.setString(3, InviteStatus.ACCEPTED.name());
            ResultSet result = prepSt.executeQuery();

            while (result.next()) {
                players.add(new Player(result.getInt("id"), result.getString("username")));
            }
        }
        return players;
    }

    public static boolean isPlayerInvitedToGame(Game game, Player player, Connection connection) throws SQLException {

        String queryGamePlayers = "SELECT i.id " +
                "FROM games_players AS i " +
                "WHERE i.game_id = ? AND i.user_id = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryGamePlayers)) {
            prepSt.setInt(1, game.getId());
            prepSt.setInt(2, player.getId());
            ResultSet result = prepSt.executeQuery();

            while (result.next()) {
                return true;
            }
        }

        return false;
    }
    public static Collection<Game> getPrivateInvites(Player player, Connection connection) throws SQLException {
        Collection<Game> games = new HashSet<>();
        String queryInvites = "SELECT g.id, g.game_type, g.user_id, u.username, g.public_game, g.creation_date " +
                "FROM games AS g " +
                "JOIN games_players AS i ON g.id = i.game_id " +
                "JOIN users AS u ON g.user_id = u.id " +
                "WHERE i.user_id = ? AND i.status IN (?, ?) AND g.status = ? AND g.public_game = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryInvites)) {
            prepSt.setInt(1, player.getId());
            prepSt.setString(2, InviteStatus.HOST.name());
            prepSt.setString(3, InviteStatus.INVITED.name());
            prepSt.setString(4, GameStatus.OPEN.name());
            prepSt.setBoolean(5, false);
            ResultSet result = prepSt.executeQuery();

            while (result.next()) {
                games.add(new Game(result.getInt("id"), GameType.valueOf(result.getString("game_type")),
                        new Player(result.getInt("user_id"), result.getString("username")),
                        result.getBoolean("public_game"), new DateTime(result.getTimestamp("creation_date"))));
            }
        }
        return games;
    }

    public static Collection<Game> getPublicInvites(Connection connection) throws SQLException {
        Collection<Game> games = new HashSet<>();
        String queryInvites = "SELECT g.id, g.game_type, g.user_id, u.username, g.public_game, g.creation_date " +
                "FROM games AS g " +
                "JOIN users AS u ON g.user_id = u.id " +
                "WHERE g.status = ? AND g.public_game = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryInvites)) {
            prepSt.setString(1, GameStatus.OPEN.name());
            prepSt.setBoolean(2, true);
            ResultSet result = prepSt.executeQuery();

            while (result.next()) {
                games.add(new Game(result.getInt("id"), GameType.valueOf(result.getString("game_type")),
                        new Player(result.getInt("user_id"), result.getString("username")),
                        result.getBoolean("public_game"), new DateTime(result.getTimestamp("creation_date"))));
            }
        }
        return games;
    }

    private static Integer getInviteRow(Player player, Game game, Connection connection)
            throws SQLException, NotFoundException {
        String queryInvites = "SELECT id FROM games_players WHERE game_id = ? AND user_id = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryInvites)) {
            prepSt.setInt(1, game.getId());
            prepSt.setInt(2, player.getId());
            ResultSet invites = prepSt.executeQuery();

            if (invites.next()) {
                return invites.getInt("id");
            } else {
                throw new NotFoundException();
            }
        }
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

    private static Integer createGame(Integer playerId, GameType type, boolean publicGame, Connection connection) throws SQLException {

        String queryInsertGame = "INSERT INTO games (user_id, game_type, public_game, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement prepSt = connection.prepareStatement(queryInsertGame, Statement.RETURN_GENERATED_KEYS)) {
            prepSt.setInt(1, playerId);
            prepSt.setString(2, type.name());
            prepSt.setBoolean(3, publicGame);
            prepSt.setString(4, GameStatus.OPEN.name());
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