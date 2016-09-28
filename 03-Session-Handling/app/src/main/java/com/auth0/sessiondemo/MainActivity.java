package com.auth0.sessiondemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Delegation;
import com.auth0.sessiondemo.utils.CredentialsManager;


public class MainActivity extends AppCompatActivity {

    private AuthenticationAPIClient mAuthenticationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthenticationClient = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));

        Button refreshTokenButton = (Button) findViewById(R.id.refreshTokenButton);
        Button idTokenButton = (Button) findViewById(R.id.tokenIDButton);
        Button logoutButton = (Button) findViewById(R.id.logout);

        refreshTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewIDWithRefreshToken();
            }
        });
        idTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewIDWithOldIDToken();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void getNewIDWithOldIDToken() {
        String idToken = CredentialsManager.getCredentials(this).getIdToken();
        mAuthenticationClient.delegationWithIdToken(idToken).start(new BaseCallback<Delegation, AuthenticationException>() {
            @Override
            public void onSuccess(final Delegation payload) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "New idToken: " + payload.getIdToken(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(AuthenticationException error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to get the new idToken", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void getNewIDWithRefreshToken() {
        String refreshToken = CredentialsManager.getCredentials(this).getRefreshToken();
        mAuthenticationClient.delegationWithRefreshToken(refreshToken).start(new BaseCallback<Delegation, AuthenticationException>() {
            @Override
            public void onSuccess(final Delegation payload) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "New idToken: " + payload.getIdToken(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(AuthenticationException error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to get the new idToken", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void logout() {
        CredentialsManager.deleteCredentials(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
