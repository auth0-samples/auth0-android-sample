package com.auth0.samples.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserIdentity;
import com.auth0.samples.R;
import com.auth0.samples.utils.Constants;
import com.auth0.samples.utils.CredentialsManager;

import java.util.List;


public class LoginActivity extends Activity {

    private Auth0 auth0;
    private String primaryUserId;
    private boolean linkSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        auth0.setOIDCConformant(true);

        if (getIntent().getExtras() != null) {
            linkSessions = getIntent().getExtras().getBoolean(Constants.LINK_ACCOUNTS, false);
            primaryUserId = getIntent().getExtras().getString(Constants.PRIMARY_USER_ID);
            doLogin();
            return;
        }

        setContentView(R.layout.activity_login);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.auth0_domain)))
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
            if (linkSessions) {
                finish();
            }
        }

        @Override
        public void onFailure(AuthenticationException exception) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
                }
            });
            if (linkSessions) {
                finish();
            }
        }

        @Override
        public void onSuccess(@NonNull Credentials credentials) {
            if (linkSessions) {
                performLink(credentials.getIdToken());
                return;
            }

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

    private void performLink(String secondaryIdToken) {
        UsersAPIClient client = new UsersAPIClient(auth0, CredentialsManager.getCredentials(LoginActivity.this).getIdToken());
        client.link(primaryUserId, secondaryIdToken)
                .start(new BaseCallback<List<UserIdentity>, ManagementException>() {
                    @Override
                    public void onSuccess(List<UserIdentity> payload) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Accounts linked!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }

                    @Override
                    public void onFailure(ManagementException error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Account linking failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                });
    }

}


