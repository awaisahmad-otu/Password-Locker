package com.example.passwordlocker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ListView credentialsListView;
    private DBHelper dbHelper;
    private CredentialAdapter adapter;
    private List<Credential> credentialsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // set up window insets to properly handle padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialize database helper and ui components
        dbHelper = new DBHelper(this);
        credentialsListView = findViewById(R.id.credentials_list);
        credentialsList = new ArrayList<>();
        adapter = new CredentialAdapter(this, credentialsList, dbHelper);
        credentialsListView.setAdapter(adapter);

        // set up add credentials button to launch addcredentialactivity
        Button addCredentialsButton = findViewById(R.id.add_credentials_button);
        addCredentialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCredentialActivity.class);
                startActivity(intent); // start the add credential activity
            }
        });

        // set up click listener for list view items to launch credentialdetailactivity
        credentialsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Credential credential = credentialsList.get(position);
                Intent intent = new Intent(MainActivity.this, CredentialDetailActivity.class);
                intent.putExtra("credential_id", credential.getId());
                startActivity(intent);
            }
        });

        loadCredentials(); // load and display the credentials
    }

    // reload credentials when returning to this activity
    @Override
    protected void onResume() {
        super.onResume();
        loadCredentials();
    }

    // method to load credentials from the database
    private void loadCredentials() {
        credentialsList.clear();
        Cursor cursor = dbHelper.getAllCredentials(); // get all credentials from the database

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // retrieve credential details
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                String website = cursor.getString(cursor.getColumnIndex("website"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String encryptedPassword = cursor.getString(cursor.getColumnIndex("encrypted_password"));
                String encryptionKey = cursor.getString(cursor.getColumnIndex("encryption_key"));

                // create a credential object and add it to the list
                Credential credential = new Credential(id, website, username, encryptedPassword, encryptionKey);
                credentialsList.add(credential);
            } while (cursor.moveToNext());

            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }
}
