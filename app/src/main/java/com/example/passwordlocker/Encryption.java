package com.example.passwordlocker;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import android.util.Base64;

public class Encryption {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public static String encrypt(String data, SecretKey key) throws Exception {
        // Generate a random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Initialize cipher with GCM mode
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        // Encrypt the data
        byte[] encrypted = cipher.doFinal(data.getBytes());

        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.encodeToString(combined, Base64.NO_WRAP);
    }

    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        // Decode the combined data
        byte[] combined = Base64.decode(encryptedData, Base64.NO_WRAP);

        // Extract IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // Extract encrypted data
        byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

        // Initialize cipher for decryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        // Decrypt the data
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Using AES-256
        return keyGenerator.generateKey();
    }

    public static SecretKey getKeyFromString(String keyStr) {
        byte[] decodedKey = Base64.decode(keyStr, Base64.NO_WRAP);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
