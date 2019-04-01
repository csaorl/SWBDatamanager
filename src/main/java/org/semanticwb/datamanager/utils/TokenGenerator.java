package org.semanticwb.datamanager.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticwb.datamanager.DataObject;
import org.semanticwb.datamanager.SWBDataSource;

/**
 *
 * @author serch
 */
public class TokenGenerator {

    private static Logger log = Logger.getLogger(TokenGenerator.class.getName());
    private static SecureRandom generator = new SecureRandom();

    /**
     * Returns a Random token of 25 bytes, it should have almost no collisions,
     * but always test for it
     *
     * @return a String token
     */
    public static String nextToken() {
        byte[] data = new byte[25];
        generator.nextBytes(data);
        return (new BigInteger(1, data)).toString(Character.MAX_RADIX);
    }

    /**
     * Returns a short Random token of 6 bytes, it might have a collision every
     * 1000 tokens
     *
     * @return a String token
     */
    public static String nextShortToken() {
        byte[] data = new byte[6];
        generator.nextBytes(data);
        return (new BigInteger(1, data)).toString(Character.MAX_RADIX);
    }

    /**
     * Returns a 25 bytes pseudo random token with an userID embedded in it
     *
     * @param userId the UserId number to embed
     * @return a String token
     */
    public static String nextTokenByUserId(String userId) {
        BigInteger userInt = new BigInteger(userId, 16);
        byte[] data = new byte[25];
        byte[] tmpRnd = new byte[12];
        byte[] tmp = userInt.toByteArray();
        generator.nextBytes(tmpRnd);
        System.arraycopy(tmpRnd, 0, data, 0, 12);
        System.arraycopy(tmp, 0, data, 25 - tmp.length, tmp.length);
        return (new BigInteger(1, data)).toString(Character.MAX_RADIX);
    }

    /**
     * Obtains the UserID number from a token with an embedded user
     *
     * @param token token with the embedded user
     * @return a String with the UserID number
     */
    public static String getUserIdFromToken(String token) {
        BigInteger tokenInt = new BigInteger(token, Character.MAX_RADIX);
        byte[] data = new byte[13];
        byte[] tmp = tokenInt.toByteArray();
        System.arraycopy(tmp, tmp.length - 13, data, 0, 13);
        return (new BigInteger(data)).toString(16);
    }

    /**
     * Returns a 25 bytes pseudo random token with an userID embedded in it,
     * making sure that token is not present in the datasource as authToken
     *
     * @param userId the UserID number to embed
     * @param ds satasource to look for existen tokens
     * @return a String token
     */
    public static String getNonExistentTokenByUserId(String userId, SWBDataSource ds) {
        try {
            String token = null;
            DataObject query = new DataObject();
            DataObject data = new DataObject();
            DataObject resp = null;
            do {
                token = nextTokenByUserId(userId);
                query.put("data", data);
                data.put("authToken", token);
                resp = ds.fetch(query);
            } while (resp.getDataObject("response").getDataList("data").size() > 0);
            return token;
        } catch (IOException ioe) {
            return null;
        }
    }

    public static String getMD5(String txt) {           
        return getHash(txt, "MD5");
    }
    
    public static String getSHA_512(String txt) {           
        return getHash(txt, "SHA-512");
    }    
    
    public static String getSHA_1(String txt) {           
        return getHash(txt, "SHA-1");
    }    
    
    public static String getHash(String txt, String algoritm) {   
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(algoritm);
            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            log.log(Level.WARNING, "getMD5", e);
        }
        return null;
    }    
}
