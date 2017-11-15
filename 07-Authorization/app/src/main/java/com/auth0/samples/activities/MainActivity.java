package com.auth0.samples.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.CredentialsManager;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.jwt.JWT;
import com.auth0.android.result.Credentials;
import com.auth0.samples.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Use the same Claim name as defined in the Rule
    private static final String ROLES_CLAIM = "https://access.control/roles";
    private CredentialsManager credentialsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.login_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAgain();
            }
        });

        final Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);

        credentialsManager = new CredentialsManager(new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(Credentials credentials) {
                JWT idToken = new JWT(credentials.getIdToken());

                final List<String> roles = idToken.getClaim(ROLES_CLAIM).asList(String.class);
                ((TextView) findViewById(R.id.userName)).setText(idToken.getClaim("name").asString());
                ((TextView) findViewById(R.id.userEmail)).setText(idToken.getClaim("email").asString());
                Picasso.with(MainActivity.this).load(idToken.getClaim("picture").asString()).into((ImageView) findViewById(R.id.userPicture));

                findViewById(R.id.toSettingsButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (roles.isEmpty()) {
                            Toast.makeText(MainActivity.this, "The logged user doesn't have any Role. Check the Rules section of the Auth0 dashboard.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!roles.contains("admin")) {
                            Toast.makeText(MainActivity.this, "You don't have access rights to visit this page", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    }
                });
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to renew expired credentials. Please, log in again.", Toast.LENGTH_LONG).show();
                    }
                });
                loginAgain();
            }
        });
    }

    private void loginAgain() {
        startActivity(new Intent(this, LoginActivity.class));
        credentialsManager.clearCredentials();
        finish();
    }
}
