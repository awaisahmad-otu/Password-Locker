package com.example.passwordlocker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        credentialsListView = findViewById(R.id.credentials_list);
        credentialsList = new ArrayList<>();
        adapter = new CredentialAdapter(this, credentialsList, dbHelper);
        credentialsListView.setAdapter(adapter);

        Button addCredentialsButton = findViewById(R.id.add_credentials_button);
        addCredentialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCredentialActivity.class);
                startActivity(intent);
            }
        });

        loadCredentials();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCredentials(); // Reload credentials when returning to this activity
    }

    private void loadCredentials() {
        credentialsList.clear();
        Cursor cursor = dbHelper.getAllCredentials();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                String website = cursor.getString(cursor.getColumnIndex("website"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String encryptedPassword = cursor.getString(cursor.getColumnIndex("encrypted_password"));
                String encryptionKey = cursor.getString(cursor.getColumnIndex("encryption_key"));

                Credential credential = new Credential(id, website, username, encryptedPassword, encryptionKey);
                credentialsList.add(credential);
            } while (cursor.moveToNext());
            
            cursor.close();
        }
        
        adapter.notifyDataSetChanged();
    }
}