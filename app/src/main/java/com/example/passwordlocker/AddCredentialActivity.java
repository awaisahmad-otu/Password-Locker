package com.example.passwordlocker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import javax.crypto.SecretKey;

public class AddCredentialActivity extends AppCompatActivity {
    // input fields for user to enter website, username, and password
    private EditText websiteInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button saveButton;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credential);

        // setting up the toolbar for navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // navigate back to the previous activity
            }
        });

        // initialize ui components
        websiteInput = findViewById(R.id.website_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        saveButton = findViewById(R.id.save_credential_button);
        dbHelper = new DBHelper(this);

        // setting up the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCredential();
            }
        });
    }

    // method to save the entered credential into the database
    private void saveCredential() {
        // retrieve input values
        String website = websiteInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // check if all fields are filled
        if (website.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // encrypt the password before storing it in the database
            SecretKey key = Encryption.generateKey(); // generate a secret key
            String encryptedPassword = Encryption.encrypt(password, key); // encrypt the password
            String keyString = Base64.encodeToString(key.getEncoded(), Base64.NO_WRAP); // encode key as a string

            // open the database for writing and insert the new credential
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("website", website); // add website field
            values.put("username", username); // add username field
            values.put("encrypted_password", encryptedPassword); // add encrypted password field
            values.put("encryption_key", keyString); // add encryption key field

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // handle toolbar navigation action
        return true;
    }
}
