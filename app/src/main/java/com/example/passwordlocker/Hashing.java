package com.example.passwordlocker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import android.util.Base64;

public class Hashing {
    private static final int SALT_BYTES = 24;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static String hashPassword(String password) {
        try {
            // generate a new salt
            byte[] salt = generateSalt();
            // generate hash using the password and salt
            byte[] hash = generateHash(password, salt);

            // combine salt and hash, and convert to base64
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);

            return Base64.encodeToString(combined, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException("error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // decode the stored hash
            byte[] combined = Base64.decode(storedHash, Base64.NO_WRAP);

            // extract salt and hash
            byte[] salt = new byte[SALT_BYTES];
            byte[] hash = new byte[combined.length - SALT_BYTES];
            System.arraycopy(combined, 0, salt, 0, SALT_BYTES);
            System.arraycopy(combined, SALT_BYTES, hash, 0, hash.length);

            // generate new hash with the same salt
            byte[] testHash = generateHash(password, salt);

            // compare the hashes
            return MessageDigest.isEqual(hash, testHash);
        } catch (Exception e) {
            throw new RuntimeException("error verifying password", e);
        }
    }

    private static byte[] generateSalt() {
        // generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] generateHash(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // create a pbe key specification with the given password, salt, iterations, and key length
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );
        // generate the hash using pbkdf2withhmacsha256 algorithm
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }
}
