package com.auth0.sessiondemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.auth0.sessiondemo.R;
import com.auth0.sessiondemo.utils.CredentialsManager;


public class LockActivity extends Activity {

    private Lock mLock;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));

        if (CredentialsManager.getCredentials(this).getIdToken() == null) {

            this.mLock = Lock.newBuilder(auth0, mCallback)
                    //Add parameters to the build
                    .build();
            mLock.onCreate(this);
            startActivity(this.mLock.newIntent(this));

        } else {
            AuthenticationAPIClient aClient = new AuthenticationAPIClient(auth0);
            aClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
                    .start(new BaseCallback<UserProfile, AuthenticationException>() {
                        @Override
                        public void onSuccess(final UserProfile payload) {
                            LockActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(LockActivity.this, "Automatic Login Success", Toast.LENGTH_SHORT).show();
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }

                        @Override
                        public void onFailure(AuthenticationException error) {
                            LockActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(LockActivity.this, "Session Expired, please Log In", Toast.LENGTH_SHORT).show();
                                }
                            });
                            CredentialsManager.deleteCredentials(getApplicationContext());
                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                        }
                    });
        }
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
            Toast.makeText(getApplicationContext(), "Log In - Success", Toast.LENGTH_SHORT).show();
            CredentialsManager.saveCredentials(getApplicationContext(), credentials);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        @Override
        public void onCanceled() {
            Toast.makeText(getApplicationContext(), "Log In - Cancelled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(LockException error) {
            Toast.makeText(getApplicationContext(), "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
        }
    };

}


