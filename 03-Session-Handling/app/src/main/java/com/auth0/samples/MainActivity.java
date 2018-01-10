package com.auth0.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;


public class MainActivity extends AppCompatActivity {

    private TextView credentialsView;
    private SecureCredentialsManager credentialsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        credentialsView = (TextView) findViewById(R.id.credentials);
        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));
        showToken();
    }

    private void showToken() {
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(final Credentials credentials) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        credentialsView.setText(credentials.getAccessToken());
                    }
                });
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Session Expired, please Log In", Toast.LENGTH_SHORT).show();
                    }
                });
                logout();
            }
        });
    }

    private void logout() {
        credentialsManager.clearCredentials();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
