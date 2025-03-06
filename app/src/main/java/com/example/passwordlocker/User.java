package com.example.passwordlocker;

public class User {
    private String email;
    private String hashedPassword;
    private String hashedSecurityAnswer;

    public User(String email, String hashedPassword, String hashedSecurityAnswer) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.hashedSecurityAnswer = hashedSecurityAnswer;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getHashedSecurityAnswer() {
        return hashedSecurityAnswer;
    }
}