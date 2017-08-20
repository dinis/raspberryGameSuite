package pt.dinis.server.data.access;

import org.joda.time.DateTime;
import pt.dinis.common.objects.*;
import pt.dinis.server.data.objects.GameStatus;
import pt.dinis.server.exceptions.NotFoundException;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by tiago on 08-08-2017.
 */
public class InviteDB {

    public static Game createGameByPlayer(Player player, GameType type, boolean publicGame, Connection connection) throws SQLException {
        Integer gameId = createGame(player.getId(), type, publicGame, connection);
        createInviteRow(player.getId(), gameId, InviteStatus.ACCEPTED, connection);
        return getOpenGame(gameId, connection);
    }

    public static void invitePlayer(Integer playerId, Game game, Connection connection) throws SQLException {
        createInviteRow(playerId, game.getId(), InviteStatus.INVITED, connection);
    }

    public static Game getOpenGame(Integer gameId, Connection connection) throws SQLException {

        String queryGame = "SELECT g.id, g.game_type, g.user_id, u.username, g.public_game, g.creation_date " +
                "FROM games AS g " +
                "JOIN users AS u ON u.id = g.user_id " +
                "WHERE g.id = ? AND g.status = ?";
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

    private static void updateInviteRow(Integer id, InviteStatus status, Connection connection) throws SQLException {
        String queryCancelInvite = "UPDATE games_players SET status = ? WHERE id = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryCancelInvite)) {
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
                "WHERE i.game_id = ? AND i.status = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryGamePlayers)) {
            prepSt.setInt(1, game.getId());
            prepSt.setString(2, InviteStatus.ACCEPTED.name());
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

    public static Collection<Invite> getPrivateInvites(Player player, Connection connection) throws SQLException {
        Map<Game, InviteStatus> games = getPrivateOpenGames(player, connection);
        Collection<Invite> invites = new HashSet<>();

        for (Map.Entry<Game, InviteStatus> entry: games.entrySet()) {
            invites.add(new Invite(entry.getKey(), entry.getValue(), getInvitedPlayers(entry.getKey(), connection)));
        }

        return invites;
    }

    public static Collection<Invite> getPublicInvites(Player player, Connection connection) throws SQLException {
        Map<Game, InviteStatus> games = getPublicOpenGames(player, connection);
        Collection<Invite> invites = new HashSet<>();

        for (Map.Entry<Game, InviteStatus> entry: games.entrySet()) {
            invites.add(new Invite(entry.getKey(), entry.getValue(), getInvitedPlayers(entry.getKey(), connection)));
        }

        return invites;
    }

    private static Map<Game, InviteStatus> getPrivateOpenGames(Player player, Connection connection) throws SQLException {
        Map<Game, InviteStatus> games = new HashMap<>();
        String queryInvites = "SELECT g.id, g.game_type, g.user_id, u.username, g.public_game, g.creation_date, i.status " +
                "FROM games AS g " +
                "JOIN games_players AS i ON g.id = i.game_id " +
                "JOIN users AS u ON g.user_id = u.id " +
                "WHERE i.user_id = ? AND g.status = ? AND g.public_game = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryInvites)) {
            prepSt.setInt(1, player.getId());
            prepSt.setString(2, GameStatus.OPEN.name());
            prepSt.setBoolean(3, false);
            ResultSet result = prepSt.executeQuery();

            while (result.next()) {
                Game game = new Game(result.getInt("id"), GameType.valueOf(result.getString("game_type")),
                        new Player(result.getInt("user_id"), result.getString("username")),
                        result.getBoolean("public_game"), new DateTime(result.getTimestamp("creation_date")));
                InviteStatus status = InviteStatus.valueOf(result.getString("status"));
                games.put(game, status);
            }
        }
        return games;
    }

    private static Map<Game, InviteStatus> getPublicOpenGames(Player player, Connection connection) throws SQLException {
        Map<Game, InviteStatus> games = new HashMap<>();
        String queryInvites = "SELECT g.id, g.game_type, g.user_id, u.username, g.public_game, g.creation_date, i.status " +
                "FROM games AS g " +
                "JOIN users AS u ON g.user_id = u.id " +
                "LEFT JOIN games_players AS i ON g.id = i.game_id AND i.user_id = ? " +
                "WHERE g.status = ? AND g.public_game = ?";
        try (PreparedStatement prepSt = connection.prepareStatement(queryInvites)) {
            prepSt.setInt(1, player.getId());
            prepSt.setString(2, GameStatus.OPEN.name());
            prepSt.setBoolean(3, true);
            ResultSet result = prepSt.executeQuery();

            while (result.next()) {
                Game game = new Game(result.getInt("id"), GameType.valueOf(result.getString("game_type")),
                        new Player(result.getInt("user_id"), result.getString("username")),
                        result.getBoolean("public_game"), new DateTime(result.getTimestamp("creation_date")));
                InviteStatus status = result.getString("status") == null ? null : InviteStatus.valueOf(result.getString("status"));
                games.put(game, status);
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