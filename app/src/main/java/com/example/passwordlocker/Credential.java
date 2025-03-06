package com.example.passwordlocker;

public class Credential {
    private long id;
    private String website;
    private String username;
    private String encryptedPassword;
    private String encryptionKey;

    public Credential(long id, String website, String username, String encryptedPassword, String encryptionKey) {
        this.id = id;
        this.website = website;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.encryptionKey = encryptionKey;
    }

    public long getId() { return id; }
    public String getWebsite() { return website; }
    public String getUsername() { return username; }
    public String getEncryptedPassword() { return encryptedPassword; }
    public String getEncryptionKey() { return encryptionKey; }

    public String getDecryptedPassword(DBHelper dbHelper) {
        return dbHelper.getDecryptedPassword(encryptedPassword, encryptionKey);
    }
} 