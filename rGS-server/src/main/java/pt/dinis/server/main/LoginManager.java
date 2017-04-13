package pt.dinis.server.main;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

/**
 * Created by tiago on 04-02-2017.
 */
public class LoginManager {
    private static Map<String, Integer> tokens = new HashMap<String, Integer>();

    private static SecureRandom random = new SecureRandom();

    public static String loginClient(Integer id) {
        String token = generateUniqueToken();
        tokens.put(token, id);
        return token;
    }

    public static boolean logoutClient(Integer id) {
        String token = getClientToken(id);
        if (token == null) {
            return false;
        }
        tokens.remove(token, id);
        return true;
    }

    public static boolean reloginClient(Integer id, String token) {
        if (tokens.containsKey(token)) {
            tokens.put(token, id);
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
        Map<String, Integer> result = new HashMap<String, Integer> ();
        for (Map.Entry<String, Integer> entry: tokens.entrySet()) {
            if (!excludedIds.contains(entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private static String generateUniqueToken() {
        String token;
        do {
            token = new BigInteger(130, random).toString(32);
        } while (token == null || tokens.containsKey(token));
        return token;
    }
}
