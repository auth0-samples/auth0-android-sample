package com.auth0.linkingaccountsdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserIdentity;
import com.auth0.linkingaccountsdemo.R;
import com.auth0.linkingaccountsdemo.utils.Constants;
import com.auth0.linkingaccountsdemo.utils.CredentialsManager;

import java.util.List;


public class LoginActivity extends Activity {

    private Lock mLock;
    private Auth0 mAuth0;
    private boolean mLinkSessions;
    private String mPrimaryUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            mLinkSessions = getIntent().getExtras().getBoolean(Constants.LINK_ACCOUNTS, false);
            mPrimaryUserId = getIntent().getExtras().getString(Constants.PRIMARY_USER_ID);
        }
        mAuth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        mLock = Lock.newBuilder(mAuth0, mCallback)
                .closable(mLinkSessions)
                //Add parameters to the build
                .build(this);
        startActivity(mLock.newIntent(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        mLock.onDestroy(this);
        mLock = null;
    }

    private final LockCallback mCallback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            Toast.makeText(LoginActivity.this, "Log In - Success", Toast.LENGTH_SHORT).show();

            if (mLinkSessions) {
                performLink(credentials.getIdToken());
            } else {
                CredentialsManager.saveCredentials(LoginActivity.this, credentials);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }

        @Override
        public void onCanceled() {
            Toast.makeText(LoginActivity.this, "Log In - Cancelled", Toast.LENGTH_SHORT).show();
            if (mLinkSessions) {
                finish();
            }
        }

        @Override
        public void onError(LockException error) {
            Toast.makeText(LoginActivity.this, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
            if (mLinkSessions) {
                finish();
            }
        }
    };

    private void performLink(String secondaryIdToken) {
        UsersAPIClient client = new UsersAPIClient(mAuth0, CredentialsManager.getCredentials(LoginActivity.this).getIdToken());
        client.link(mPrimaryUserId, secondaryIdToken)
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


