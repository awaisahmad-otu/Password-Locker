package com.example.passwordlocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class CredentialAdapter extends ArrayAdapter<Credential> {
    private Context context;
    private List<Credential> credentials;
    private DBHelper dbHelper;

    public CredentialAdapter(Context context, List<Credential> credentials, DBHelper dbHelper) {
        super(context, 0, credentials);
        this.context = context;
        this.credentials = credentials;
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.credential_item, parent, false);
        }

        Credential credential = credentials.get(position);

        TextView websiteText = convertView.findViewById(R.id.website_text);
        TextView usernameText = convertView.findViewById(R.id.username_text);
        TextView passwordText = convertView.findViewById(R.id.password_text);

        websiteText.setText("Website: " + credential.getWebsite());
        usernameText.setText("Username: " + credential.getUsername());
        
        // Decrypt and display the password
        String decryptedPassword = credential.getDecryptedPassword(dbHelper);
        passwordText.setText("Password: " + (decryptedPassword != null ? decryptedPassword : "Error decrypting"));

        return convertView;
    }
} 