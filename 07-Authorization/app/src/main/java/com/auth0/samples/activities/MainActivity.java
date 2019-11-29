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
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.UserProfile;
import com.auth0.samples.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Use the same Claim name as defined in the Rule
    private static final String ROLES_CLAIM = "https://example.com/roles";

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
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(auth0);

        //Obtain the token from the Intent's extras
        String accessToken = getIntent().getStringExtra(LoginActivity.EXTRA_ACCESS_TOKEN);
        authenticationClient.userInfo(accessToken)
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile userInfo) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final List<String> roles = userInfo.getExtraInfo().containsKey(ROLES_CLAIM) ? (List<String>) userInfo.getExtraInfo().get(ROLES_CLAIM) : Collections.<String>emptyList();
                                ((TextView) findViewById(R.id.userName)).setText(userInfo.getName());
                                ((TextView) findViewById(R.id.userEmail)).setText(userInfo.getEmail());
                                Picasso.with(MainActivity.this).load(userInfo.getPictureURL()).into((ImageView) findViewById(R.id.userPicture));

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
                        });
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "UserInfo request failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void loginAgain() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_CLEAR_CREDENTIALS, true);
        startActivity(intent);
        finish();
    }
}
