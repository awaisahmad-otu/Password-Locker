package com.example.passwordlocker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
    private Button signinButton;
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
        signinButton = findViewById(R.id.signin_button);
        dbHelper = new DBHelper(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignIn();
            }
        });
    }

    // add user to database
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String securityQuestion = securityQuestionEditText.getText().toString().trim();
        String securityAnswer = securityAnswerEditText.getText().toString().trim();

        // ensure all fields are filled
        if (email.isEmpty() || password.isEmpty() || securityQuestion.isEmpty() || securityAnswer.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure the email is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        // hash password and security answer using hash class
        String hashedPassword = Hashing.hashPassword(password);
        String hashedSecurityAnswer = Hashing.hashPassword(securityAnswer);

        // register user
        long userId = dbHelper.addUser(email, hashedPassword, securityQuestion, hashedSecurityAnswer);
        if (userId != -1) {
            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show();
        }
    }

    // navigate back to sign in screen
    private void navigateToSignIn() {
        Intent intent = new Intent(SignUp.this, SignIn.class);
        startActivity(intent);
        finish();
    }
}
