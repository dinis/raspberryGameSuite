package pt.dinis.server.core;

import pt.dinis.common.core.Player;

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

    public static String loginClient(Integer id, Player player) {
        String token = generateUniqueToken();
        addClient(id, player, token);
        return token;
    }

    public static boolean logoutClient(Integer id) {
        String token = getClientToken(id);
        if (token == null) {
            return false;
        }
        removeClient(token);
        return true;
    }

    public static boolean reloginClient(Integer id, String token) {
        if (tokens.containsKey(token) && players.containsKey(tokens.get(token))) {
            Player player = players.get(tokens.get(token));
            removeClient(token);
            addClient(id, player, token);
            return true;
        }
        return false;
    }

    public static boolean isLogged(Integer id) {
        return tokens.values().contains(id);
    }

    public static String getClientToken(Integer id) {
        for (Map.Entry<String, Integer> entry: tokens.entrySet()) {
            if (entry.getValue().equals(id)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Map<String, Integer> getLoggedClients(Collection<Integer> excludedIds) {
        Map<String, Integer> result = new HashMap<> ();
        for (Map.Entry<String, Integer> entry: tokens.entrySet()) {
            if (!excludedIds.contains(entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static List<Player> getPlayersFromIds(List<Integer> ids) {
        List<Player> result = new ArrayList<>();
        for (Integer id: ids) {
            if (players.containsKey(id)) {
                result.add(players.get(id));
            }
        }
        return result;
    }

    public static List<Player> getActivePlayers() {
        return getPlayersFromIds(new ArrayList<>(tokens.values()));
    }

    private static String generateUniqueToken() {
        String token;
        do {
            token = new BigInteger(130, random).toString(32);
        } while (token == null || tokens.containsKey(token));
        return token;
    }

    private static void addClient(Integer id, Player player, String token) {
        if (isLogged(id)) {
            tokens.remove(getClientToken(id));
        }
        tokens.put(token, id);
        players.put(id, player);
    }

    private static void removeClient(String token) {
        Integer id = tokens.get(token);
        tokens.remove(token);
        if (id != null) {
            players.remove(id);
        }
    }
}
