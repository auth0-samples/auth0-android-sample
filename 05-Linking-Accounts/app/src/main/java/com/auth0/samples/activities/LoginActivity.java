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
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManager;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserIdentity;
import com.auth0.samples.R;
import com.auth0.samples.utils.Constants;

import java.util.List;


public class LoginActivity extends Activity {

    private static final String LOGGING_IN = "logging_in";

    private Auth0 auth0;
    private CredentialsManager credentialsManager;
    private boolean linkSessions;
    private boolean loggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        credentialsManager = new CredentialsManager(new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

        linkSessions = getIntent().getExtras() != null && getIntent().getExtras().getBoolean(Constants.LINK_ACCOUNTS, false);
        loggingIn = savedInstanceState != null && savedInstanceState.getBoolean(LOGGING_IN, false);
        if (linkSessions && !loggingIn) {
            doLogin();
            return;
        }

        if (!linkSessions && credentialsManager.hasValidCredentials()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(LOGGING_IN, loggingIn);
        super.onSaveInstanceState(outState);
    }

    private void doLogin() {
        loggingIn = true;
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withScope("openid profile email")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .start(this, callback);
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
            credentialsManager.saveCredentials(credentials);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    };

    private void performLink(final String secondaryIdToken) {
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(Credentials credentials) {
                UsersAPIClient client = new UsersAPIClient(auth0, credentials.getIdToken());
                String primaryUserId = getIntent().getExtras().getString(Constants.PRIMARY_USER_ID);
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

            @Override
            public void onFailure(CredentialsManagerException error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Failed due to expired credentials. Please, log in again to your main account.", Toast.LENGTH_LONG).show();
                    }
                });
                credentialsManager.clearCredentials();
            }
        });
    }

}


