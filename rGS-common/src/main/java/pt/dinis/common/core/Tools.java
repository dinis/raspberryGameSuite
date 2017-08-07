package pt.dinis.common.core;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tools {
    public static String stringToMd5(String string) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(string.getBytes(), 0, string.length());

        return new BigInteger(1, md5.digest()).toString();
    }
}
