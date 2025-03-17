package com.example.passwordlocker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CredentialDetailActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private TextView websiteText;
    private TextView usernameText;
    private TextView passwordText;
    private TextView securityQuestionText;
    private EditText securityAnswerInput;
    private Button verifyButton;
    private long credentialId;
    private String storedSecurityAnswer;
    private String securityQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential_detail);

        // setting up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // initialize ui components
        websiteText = findViewById(R.id.website_text);
        usernameText = findViewById(R.id.username_text);
        passwordText = findViewById(R.id.password_text);
        securityQuestionText = findViewById(R.id.security_question_text);
        securityAnswerInput = findViewById(R.id.security_answer_input);
        verifyButton = findViewById(R.id.verify_button);
        dbHelper = new DBHelper(this);

        // hide the username and password fields
        usernameText.setVisibility(View.GONE);
        passwordText.setVisibility(View.GONE);

        // retrieve credential id from intent extras
        credentialId = getIntent().getLongExtra("credential_id", -1);
        if (credentialId != -1) {
            loadCredentialDetails(credentialId); // load credential details based on id
        }

        // set up click listener for verify button
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySecurityAnswer(); // verify security answer entered by the user
            }
        });
    }

    // load credential details from the database
    private void loadCredentialDetails(long credentialId) {
        Cursor cursor = dbHelper.getCredentialById(credentialId);
        if (cursor != null && cursor.moveToFirst()) {
            String website = cursor.getString(cursor.getColumnIndex("website"));
            websiteText.setText("Website: " + website);

            // retrieve signed-in user's email to fetch security question and answer
            String email = SignIn.getSignedInUserEmail();
            securityQuestion = dbHelper.getSecurityQuestion(email); // get security question
            storedSecurityAnswer = dbHelper.getHashedSecurityAnswer(email); // get hashed answer

            // display security question
            securityQuestionText.setText("Security Question: " + securityQuestion);

            cursor.close();
        }
    }

    // method to verify the entered security answer
    private void verifySecurityAnswer() {
        String enteredAnswer = securityAnswerInput.getText().toString().trim();
        if (Hashing.verifyPassword(enteredAnswer, storedSecurityAnswer)) {
            displayCredentials(); // if answer matches, display credentials
        } else {
            Toast.makeText(this, "Incorrect security answer", Toast.LENGTH_SHORT).show();
        }
    }

    // method to display credentials after verification
    private void displayCredentials() {
        Cursor cursor = dbHelper.getCredentialById(credentialId);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String encryptedPassword = cursor.getString(cursor.getColumnIndex("encrypted_password"));
            String encryptionKey = cursor.getString(cursor.getColumnIndex("encryption_key"));

            // decrypt the password using the encryption key
            String decryptedPassword = dbHelper.getDecryptedPassword(encryptedPassword, encryptionKey);

            // display the username and password
            usernameText.setText("Username: " + username);
            passwordText.setText("Password: " + (decryptedPassword != null ? decryptedPassword : "Error decrypting"));
            usernameText.setVisibility(View.VISIBLE);
            passwordText.setVisibility(View.VISIBLE);

            cursor.close();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
