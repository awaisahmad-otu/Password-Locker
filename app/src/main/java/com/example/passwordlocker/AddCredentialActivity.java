package com.example.passwordlocker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import javax.crypto.SecretKey;

public class AddCredentialActivity extends AppCompatActivity {
    private EditText websiteInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button saveButton;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credential);

        websiteInput = findViewById(R.id.website_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        saveButton = findViewById(R.id.save_credential_button);
        dbHelper = new DBHelper(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCredential();
            }
        });
    }

    private void saveCredential() {
        String website = websiteInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (website.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Encrypt the password before storing
            SecretKey key = Encryption.generateKey();
            String encryptedPassword = Encryption.encrypt(password, key);
            String keyString = Base64.encodeToString(key.getEncoded(), Base64.NO_WRAP);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("website", website);
            values.put("username", username);
            values.put("encrypted_password", encryptedPassword);
            values.put("encryption_key", keyString);

            long newRowId = db.insert("credentials", null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Credential saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving credential", Toast.LENGTH_SHORT).show();
            }
            db.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error encrypting password", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
} 