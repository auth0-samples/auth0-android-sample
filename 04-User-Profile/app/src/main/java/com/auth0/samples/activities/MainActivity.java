package com.auth0.samples.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManager;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.jwt.JWT;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.auth0.samples.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Auth0 auth0;
    private UserProfile userProfile;
    private Button editProfileButton;
    private Button cancelEditionButton;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userCountryTextView;
    private EditText updateCountryEditText;

    private CredentialsManager credentialsManager;
    private UsersAPIClient usersClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);

        credentialsManager = new CredentialsManager(new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {

            @Override
            public void onSuccess(Credentials credentials) {
                String idToken = credentials.getIdToken();
                usersClient = new UsersAPIClient(auth0, idToken);
                usersClient.getProfile(getUserId(idToken))
                        .start(new BaseCallback<UserProfile, ManagementException>() {
                            @Override
                            public void onSuccess(UserProfile profile) {
                                userProfile = profile;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        refreshScreenInformation();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(ManagementException error) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "User Profile Request Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to renew expired credentials. Please, log in again.", Toast.LENGTH_LONG).show();
                    }
                });
                loginAgain();
            }
        });


        editProfileButton = (Button) findViewById(R.id.editButton);
        cancelEditionButton = (Button) findViewById(R.id.cancelEditionButton);
        userNameTextView = (TextView) findViewById(R.id.userNameTitle);
        userEmailTextView = (TextView) findViewById(R.id.userEmailTitle);
        userCountryTextView = (TextView) findViewById(R.id.userCountryTitle);
        updateCountryEditText = (EditText) findViewById(R.id.updateCountryEdittext);
        Button loginAgainButton = (Button) findViewById(R.id.login_again);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
        cancelEditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editModeOn(false);
            }
        });
        loginAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAgain();
            }
        });
    }

    private void refreshScreenInformation() {
        userNameTextView.setText(String.format(getString(R.string.username), userProfile.getName()));
        userEmailTextView.setText(String.format(getString(R.string.useremail), userProfile.getEmail()));
        ImageView userPicture = (ImageView) findViewById(R.id.userPicture);
        if (userProfile.getPictureURL() != null) {
            Picasso.with(this)
                    .load(userProfile.getPictureURL())
                    .into(userPicture);
        }

        String country = (String) userProfile.getUserMetadata().get("country");
        if (country != null && !country.isEmpty()) {
            userCountryTextView.setVisibility(View.VISIBLE);
            userCountryTextView.setText(String.format(getString(R.string.userCountry), country));
        }
    }

    private String getUserId(String idToken) {
        return new JWT(idToken).getSubject();
    }

    private void editProfile() {
        if (userProfile == null) {
            return;
        }
        if (cancelEditionButton.getVisibility() == View.GONE) {
            editModeOn(true);
        } else {
            String country = updateCountryEditText.getText().toString();
            updateInformation(country);
            editModeOn(false);
        }
    }

    private void updateInformation(final String countryUpdate) {
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(Credentials credentials) {
                Map<String, Object> userMetadata = new HashMap<>();
                userMetadata.put("country", countryUpdate);
                UsersAPIClient usersClient = new UsersAPIClient(auth0, credentials.getIdToken());
                usersClient.updateMetadata(userProfile.getId(), userMetadata)
                        .start(new BaseCallback<UserProfile, ManagementException>() {
                            @Override
                            public void onSuccess(final UserProfile profile) {
                                userProfile = profile;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        refreshScreenInformation();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(ManagementException error) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Profile Update Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to renew expired credentials. Please, log in again.", Toast.LENGTH_LONG).show();
                    }
                });
                loginAgain();
            }
        });
    }

    private void editModeOn(boolean editModeOn) {
        if (editModeOn) {
            updateCountryEditText.setVisibility(View.VISIBLE);
            editProfileButton.setText(getString(R.string.save));
            cancelEditionButton.setVisibility(View.VISIBLE);
        } else {
            editProfileButton.setText(getString(R.string.edit));
            updateCountryEditText.setText("");
            updateCountryEditText.setVisibility(View.GONE);
            cancelEditionButton.setVisibility(View.GONE);
        }
    }

    private void loginAgain() {
        credentialsManager.clearCredentials();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
