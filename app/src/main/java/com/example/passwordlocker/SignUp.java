package com.example.passwordlocker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText securityQuestionEditText;
    private EditText securityAnswerEditText;
    private Button signupButton;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        securityQuestionEditText = findViewById(R.id.security_question);
        securityAnswerEditText = findViewById(R.id.security_answer);
        signupButton = findViewById(R.id.signup_button);
        dbHelper = new DBHelper(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String securityAnswer = securityAnswerEditText.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty() || securityAnswer.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user already exists
        if (dbHelper.checkUser(email)) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Hash password and security answer
            String hashedPassword = Hashing.hashPassword(password);
            String hashedSecurityAnswer = Hashing.hashPassword(securityAnswer);

            // Add user to database
            long result = dbHelper.addUser(email, hashedPassword, hashedSecurityAnswer);

            if (result != -1) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                // Navigate back to SignIn
                Intent intent = new Intent(SignUp.this, SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear activity stack
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error during registration", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
