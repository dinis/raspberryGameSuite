package pt.dinis.main;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

/**
 * Created by tiago on 04-02-2017.
 */
public class LoginManager {
    private static Map<String, Integer> hashes = new HashMap<String, Integer>();

    private static SecureRandom random = new SecureRandom();

    public static String loginClient(Integer id) throws Exception {
        String hash = generateUniqueHash();
        hashes.put(hash, id);
        return hash;
    }

    public static boolean logoutClient(Integer id) {
        String hash = null;
        for(Map.Entry<String, Integer> entry: hashes.entrySet()) {
            if(entry.getValue().equals(id)) {
                hash = entry.getKey();
            }
        }
        if (hash == null) {
            return false;
        }
        hashes.remove(hash, id);
        return true;
    }

    public static boolean reloginClient(String hash, Integer id) {
        if (hashes.containsKey(hash)) {
            hashes.put(hash, id);
            return true;
        }
        return false;
    }

    private static String generateUniqueHash() {
        String hash = null;
        do {
            hash = new BigInteger(130, random).toString(32);
        } while (hash == null || hashes.containsKey(hash));
        return hash;
    }
}
