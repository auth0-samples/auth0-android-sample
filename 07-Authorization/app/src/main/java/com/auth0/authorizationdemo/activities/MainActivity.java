package com.auth0.authorizationdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.auth0.authorizationdemo.R;
import com.auth0.authorizationdemo.utils.CredentialsManager;


public class MainActivity extends AppCompatActivity {

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginAgainButton = (Button) findViewById(R.id.login_again);
        loginAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAgain();
            }
        });

        Button showSettingsButton = (Button) findViewById(R.id.toSettingsButton);
        showSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });

        final JWT jwt = new JWT(CredentialsManager.getCredentials(this).getIdToken());
        String roleClaim = "https://access.control/role";
        userRole = jwt.getClaim(roleClaim).asString();

        TextView roleText = (TextView) findViewById(R.id.userRole);
        roleText.setText(String.format("Your role: %s", userRole));
    }

    private void showSettings() {
        if (!"admin".equals(userRole)) {
            Toast.makeText(MainActivity.this, "You don't have access rights to visit this page", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void loginAgain() {
        startActivity(new Intent(this, LoginActivity.class));
        CredentialsManager.deleteCredentials(this);
        finish();
    }
}
