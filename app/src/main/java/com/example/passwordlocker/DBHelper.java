package com.example.passwordlocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import javax.crypto.SecretKey;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "passwordLocker.db";
    private static final int DATABASE_VERSION = 1;

    // table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CREDENTIALS = "credentials";

    // users columns
    private static final String KEY_EMAIL = "email";
    private static final String KEY_HASHED_PASSWORD = "hashedPassword";
    private static final String KEY_SECURITY_QUESTION = "securityQuestion";
    private static final String KEY_HASHED_SECURITY_ANSWER = "hashedSecurityAnswer";

    // credentials columns
    private static final String KEY_ID = "id";
    private static final String KEY_WEBSITE = "website";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ENCRYPTED_PASSWORD = "encrypted_password";
    private static final String KEY_ENCRYPTION_KEY = "encryption_key";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                KEY_EMAIL + " TEXT PRIMARY KEY, " +
                KEY_HASHED_PASSWORD + " TEXT, " +
                KEY_SECURITY_QUESTION + " TEXT, " +
                KEY_HASHED_SECURITY_ANSWER + " TEXT)";
        db.execSQL(createUserTable);

        String createCredentialsTable = "CREATE TABLE " + TABLE_CREDENTIALS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_WEBSITE + " TEXT, " +
                KEY_USERNAME + " TEXT, " +
                KEY_ENCRYPTED_PASSWORD + " TEXT, " +
                KEY_ENCRYPTION_KEY + " TEXT)";
        db.execSQL(createCredentialsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDENTIALS);
        onCreate(db);
    }

    // add new user
    public long addUser(String email, String hashedPassword, String securityQuestion, String hashedSecurityAnswer) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);
        values.put(KEY_HASHED_PASSWORD, hashedPassword);
        values.put(KEY_SECURITY_QUESTION, securityQuestion);
        values.put(KEY_HASHED_SECURITY_ANSWER, hashedSecurityAnswer);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // check if user exists
    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_EMAIL},
                KEY_EMAIL + "=?", new String[]{email},
                null, null, null);
        
        boolean exists = (cursor != null && cursor.getCount() > 0);
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    // get user from database
    public Cursor getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null,
                KEY_EMAIL + "=?", new String[]{email},
                null, null, null);
    }

    // get all credentials
    public Cursor getAllCredentials() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CREDENTIALS, null, null, null, null, null, KEY_WEBSITE + " ASC");
    }

    // get credential by id
    public Cursor getCredentialById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CREDENTIALS, null,
                KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
    }

    // get decrypted password for credential
    public String getDecryptedPassword(String encryptedPassword, String keyString) {
        try {
            SecretKey key = Encryption.getKeyFromString(keyString);
            return Encryption.decrypt(encryptedPassword, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // get security question for user
    public String getSecurityQuestion(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_SECURITY_QUESTION},
                KEY_EMAIL + "=?", new String[]{email},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String securityQuestion = cursor.getString(cursor.getColumnIndex(KEY_SECURITY_QUESTION));
            cursor.close();
            return securityQuestion;
        }
        return null;
    }

    // get hashed security answer for user
    public String getHashedSecurityAnswer(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_HASHED_SECURITY_ANSWER},
                KEY_EMAIL + "=?", new String[]{email},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String hashedSecurityAnswer = cursor.getString(cursor.getColumnIndex(KEY_HASHED_SECURITY_ANSWER));
            cursor.close();
            return hashedSecurityAnswer;
        }
        return null;
    }

    // Add this method to delete a credential by ID
    public boolean deleteCredentialById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CREDENTIALS, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0; // Return true if at least one row was deleted
    }
}