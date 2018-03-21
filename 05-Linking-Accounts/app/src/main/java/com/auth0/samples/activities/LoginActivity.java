package com.auth0.samples.activities;

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
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserIdentity;
import com.auth0.samples.R;

import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private Auth0 auth0;
    private SecureCredentialsManager credentialsManager;
    private static final String KEY_LOG_IN_IN_PROGRESS = "com.auth0.LOG_IN_IN_PROGRESS";

    /**
     * Required when setting up Local Authentication in the Credential Manager
     * Refer to SecureCredentialsManager#requireAuthentication method for more information.
     */
    @SuppressWarnings("unused")
    private static final int CODE_DEVICE_AUTHENTICATION = 22;
    public static final String KEY_LINK_ACCOUNTS = "com.auth0.LINK_ACCOUNTS";
    public static final String KEY_PRIMARY_USER_ID = "com.auth0.PRIMARY_USER_ID";
    public static final String KEY_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS";
    public static final String KEY_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN";
    public static final String KEY_ID_TOKEN = "com.auth0.ID_TOKEN";

    private boolean linkSessions;
    private boolean loggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup CredentialsManager
        auth0 = new Auth0(this);
        auth0.setLoggingEnabled(true);
        auth0.setOIDCConformant(true);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

        //Optional - Uncomment the next line to use:
        //Require device authentication before obtaining the credentials
        //credentialsManager.requireAuthentication(this, CODE_DEVICE_AUTHENTICATION, getString(R.string.request_credentials_title), null);

        // Check if the activity was launched after a logout
        if (getIntent().getBooleanExtra(KEY_CLEAR_CREDENTIALS, false)) {
            credentialsManager.clearCredentials();
        }

        linkSessions = getIntent().getBooleanExtra(KEY_LINK_ACCOUNTS, false);
        loggingIn = savedInstanceState != null && savedInstanceState.getBoolean(KEY_LOG_IN_IN_PROGRESS, false);

        if (!linkSessions && credentialsManager.hasValidCredentials()) {
            // Obtain the existing credentials and move to the next activity
            credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
                @Override
                public void onSuccess(final Credentials credentials) {
                    showNextActivity(credentials);
                }

                @Override
                public void onFailure(CredentialsManagerException error) {
                    //Authentication cancelled by the user. Exit the app
                    finish();
                }
            });
            return;
        }

        setContentView(R.layout.activity_login);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        // Check if an account linking was requested
        if (linkSessions && !loggingIn) {
            loginButton.setText(R.string.link_account);
            //Auto log in but allow to retry if authentication is cancelled
            doLogin();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_LOG_IN_IN_PROGRESS, loggingIn);
        super.onSaveInstanceState(outState);
    }

    /**
     * Override required when setting up Local Authentication in the Credential Manager
     * Refer to SecureCredentialsManager#requireAuthentication method for more information.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (credentialsManager.checkAuthenticationResult(requestCode, resultCode)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showNextActivity(Credentials credentials) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(KEY_ACCESS_TOKEN, credentials.getAccessToken());
        intent.putExtra(KEY_ID_TOKEN, credentials.getIdToken());
        startActivity(intent);
        finish();
    }

    private void doLogin() {
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/api/v2/", getString(R.string.com_auth0_domain)))
                .withScope("openid profile email offline_access read:current_user update:current_user_identities")
                .start(this, webCallback);
    }

    private final AuthCallback webCallback = new AuthCallback() {
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
            showNextActivity(credentials);
        }
    };

    private void performLink(final String secondaryIdToken) {
        UsersAPIClient client = new UsersAPIClient(auth0, getIntent().getExtras().getString(LoginActivity.KEY_ACCESS_TOKEN));
        String primaryUserId = getIntent().getExtras().getString(LoginActivity.KEY_PRIMARY_USER_ID);
        client.link(primaryUserId, secondaryIdToken)
                .start(new BaseCallback<List<UserIdentity>, ManagementException>() {
                    @Override
                    public void onSuccess(List<UserIdentity> payload) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(com.auth0.samples.activities.LoginActivity.this, "Accounts linked!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }

                    @Override
                    public void onFailure(ManagementException error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(com.auth0.samples.activities.LoginActivity.this, "Account linking failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                });
    }
}