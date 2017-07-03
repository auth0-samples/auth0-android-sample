package com.auth0.samples;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.auth0.samples.utils.CredentialsManager;


public class LoginActivity extends AppCompatActivity {

    private Auth0 auth0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        auth0.setOIDCConformant(true);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });


        String accessToken = CredentialsManager.getCredentials(this).getAccessToken();
        if (accessToken == null) {
            return;
        }

        //If the token exists, try to fetch the associated user info
        loginButton.setEnabled(false);
        AuthenticationAPIClient aClient = new AuthenticationAPIClient(auth0);
        aClient.userInfo(accessToken)
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Automatic Login Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        loginButton.setEnabled(true);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Session Expired, please Log In", Toast.LENGTH_SHORT).show();
                            }
                        });
                        CredentialsManager.deleteCredentials(LoginActivity.this);
                    }
                });
    }

    private void doLogin() {
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.auth0_domain)))
                .withScope("openid offline_access")
                .start(this, callback);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (WebAuthProvider.resume(intent)) {
            return;
        }
        super.onNewIntent(intent);
    }

    private final AuthCallback callback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }

        @Override
        public void onFailure(AuthenticationException exception) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onSuccess(@NonNull Credentials credentials) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Log In - Success", Toast.LENGTH_SHORT).show();
                }
            });
            CredentialsManager.saveCredentials(LoginActivity.this, credentials);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    };

}


