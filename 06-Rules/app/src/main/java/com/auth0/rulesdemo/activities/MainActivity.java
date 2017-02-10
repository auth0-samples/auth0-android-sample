package com.auth0.rulesdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.UserProfile;
import com.auth0.rulesdemo.R;
import com.auth0.rulesdemo.utils.CredentialsManager;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView username = (TextView) findViewById(R.id.username);
        final TextView email = (TextView) findViewById(R.id.email);
        final TextView country = (TextView) findViewById(R.id.country);
        final ImageView picture = (ImageView) findViewById(R.id.userPicture);

        Button loginAgainButton = (Button) findViewById(R.id.login_again);
        loginAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAgain();
            }
        });

        Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
<<<<<<< HEAD
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(auth0);
        authenticationClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile profile) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                username.setText(profile.getName());
                                email.setText(profile.getEmail());
                                Picasso.with(getApplicationContext()).load(profile.getPictureURL()).into(picture);

                                // Get the country from the user profile
                                // This is included in the extra info... and must be enabled in the Auth0 rules web.
                                if (!profile.getExtraInfo().containsKey("country")) {
                                    Toast.makeText(MainActivity.this, "Country not available. Check your Rules in the dashboard.", Toast.LENGTH_LONG).show();
                                    Log.e("AUTH0", "Failed assigning country info... check if country rule is enabled in Auth0 web");
                                    return;
                                }
                                country.setText(profile.getExtraInfo().get("country").toString());
                            }
                        });
=======
        auth0.setOIDCConformant(true);
        auth0.setLoggingEnabled(true);
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(auth0);
        final UsersAPIClient userClient = new UsersAPIClient(auth0, CredentialsManager.getCredentials(this).getIdToken());
        authenticationClient.userInfo(CredentialsManager.getCredentials(this).getAccessToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(UserProfile payload) {
                        String userId = (String) payload.getExtraInfo().get("sub");
                        userClient.getProfile(userId)
                                .start(new BaseCallback<UserProfile, ManagementException>() {
                                    @Override
                                    public void onSuccess(final UserProfile profile) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                username.setText(profile.getName());
                                                email.setText(profile.getEmail());
                                                Picasso.with(getApplicationContext()).load(profile.getPictureURL()).into(picture);

                                                // Get the country from the user profile
                                                // This is included in the extra info... and must be enabled in the Auth0 rules web.
                                                if (!profile.getExtraInfo().containsKey("country")) {
                                                    Toast.makeText(MainActivity.this, "Country not available. Check your Rules in the dashboard.", Toast.LENGTH_LONG).show();
                                                    Log.e("AUTH0", "Failed assigning country info... check if country rule is enabled in Auth0 web");
                                                    return;
                                                }
                                                country.setText(profile.getExtraInfo().get("country").toString());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(ManagementException error) {
                                    }
                                });
>>>>>>> use OIDC conformant endpoints
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                    }
                });
    }

    private void loginAgain() {
        startActivity(new Intent(this, LoginActivity.class));
        CredentialsManager.deleteCredentials(this);
        finish();
    }
}
