package pt.dinis.server.core;

import pt.dinis.common.core.Player;
import pt.dinis.server.exceptions.NotFoundException;

import java.math.BigInteger;
import java.util.*;
import java.security.SecureRandom;

/**
 * Created by tiago on 04-02-2017.
 */
public class LoginManager {
    private static Map<String, Integer> tokens = new HashMap<>();
    private static Map<Integer, Player> players = new HashMap<>();

    private static SecureRandom random = new SecureRandom();

    public static String loginClient(Integer connectionId, Player player) {
        String token = generateUniqueToken();
        addClient(connectionId, player, token);
        return token;
    }

    public static boolean logoutClient(Integer connectionId) {
        String token = getClientToken(connectionId);
        if (token == null) {
            return false;
        }
        removeClient(token);
        return true;
    }

    public static boolean reloginClient(Integer connectionId, String token) {
        if (tokens.containsKey(token) && players.containsKey(tokens.get(token))) {
            Player player = players.get(tokens.get(token));
            removeClient(token);
            addClient(connectionId, player, token);
            return true;
        }
        return false;
    }

    public static boolean isLogged(Integer connectionId) {
        return tokens.values().contains(connectionId);
    }

    public static String getClientToken(Integer connectionId) {
        for (Map.Entry<String, Integer> entry: tokens.entrySet()) {
            if (entry.getValue().equals(connectionId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /*
    Returns all connection ids referred here
     */
    public static Collection<Integer> getAllConnectionIds() {
        return new HashSet<Integer>(tokens.values());
    }

    /*
    Return players given a connection ids collection
     */
    public static Collection<Player> getPlayers(Collection<Integer> connectionIds) {
        Collection<Player> result = new HashSet<>();
        for (Integer id : connectionIds) {
            if (players.containsKey(id)) {
                result.add(players.get(id));
            }
        }
        return result;
    }

    /*
    Return a player given a connection id
     */
    public static Player getPlayer(Integer connectionId) throws NotFoundException {
        if (players.containsKey(connectionId)) {
            return players.get(connectionId);
        }
        throw new NotFoundException();
    }

    /*
    Return all connection ids for a given player
     */
    public static Collection<Integer> getConnectionIds(Integer playerId) {
        Collection<Integer> connectionIds = new HashSet<>();

        for (Map.Entry<Integer, Player> entry: players.entrySet()) {
            if (entry.getValue().getId().equals(playerId)) {
                connectionIds.add(entry.getKey());
            }
        }
        return connectionIds;
    }

    /*
    Return all connection ids for given players
     */
    public static Collection<Integer> getConnectionIds(Collection<Integer> playerIds) {
        Collection<Integer> connectionIds = new HashSet<>();

        for (Map.Entry<Integer, Player> entry: players.entrySet()) {
            if (playerIds.contains(entry.getValue().getId())) {
                connectionIds.add(entry.getKey());
            }
        }
        return connectionIds;
    }

    private static String generateUniqueToken() {
        String token;
        do {
            token = new BigInteger(130, random).toString(32);
        } while (token == null || tokens.containsKey(token));
        return token;
    }

    private static void addClient(Integer connectionId, Player player, String token) {
        if (isLogged(connectionId)) {
            tokens.remove(getClientToken(connectionId));
        }
        tokens.put(token, connectionId);
        players.put(connectionId, player);
    }

    private static void removeClient(String token) {
        Integer connectionId = tokens.get(token);
        tokens.remove(token);
        if (connectionId != null) {
            players.remove(connectionId);
        }
    }
}
