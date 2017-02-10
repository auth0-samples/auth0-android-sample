package com.auth0.profiledemo.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.UserProfile;
import com.auth0.profiledemo.R;
import com.auth0.profiledemo.utils.CredentialsManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Button mEditProfileButton;
    private Button mCancelEditionButton;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private TextView mUserCountryTextView;
    private EditText mUpdateCountryEditText;

    private UsersAPIClient mUsersClient;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        auth0.setOIDCConformant(true);

        mUsersClient = new UsersAPIClient(auth0, CredentialsManager.getCredentials(MainActivity.this).getIdToken());

        // The process to reclaim the User Information is preceded by an Authentication call.
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(auth0);
        authenticationClient.userInfo(CredentialsManager.getCredentials(this).getAccessToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {

                    @Override
                    public void onSuccess(final UserProfile info) {
                        mUserId = (String) info.getExtraInfo().get("sub");
                        mUsersClient.getProfile(mUserId)
                                .start(new BaseCallback<UserProfile, ManagementException>() {
                                    @Override
                                    public void onSuccess(final UserProfile profile) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                refreshScreenInformation(profile);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(ManagementException error) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "User Information Request Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "User Information Request Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        mEditProfileButton = (Button) findViewById(R.id.editButton);
        mCancelEditionButton = (Button) findViewById(R.id.cancelEditionButton);
        mUserNameTextView = (TextView) findViewById(R.id.userNameTitle);
        mUserEmailTextView = (TextView) findViewById(R.id.userEmailTitle);
        mUserCountryTextView = (TextView) findViewById(R.id.userCountryTitle);
        mUpdateCountryEditText = (EditText) findViewById(R.id.updateCountryEdittext);
        Button loginAgainButton = (Button) findViewById(R.id.login_again);

        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
        mCancelEditionButton.setOnClickListener(new View.OnClickListener() {
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

    private void refreshScreenInformation(UserProfile profile) {
        mUserNameTextView.setText(String.format(getString(R.string.username), profile.getName()));
        mUserEmailTextView.setText(String.format(getString(R.string.useremail), profile.getEmail()));
        ImageView userPicture = (ImageView) findViewById(R.id.userPicture);
        if (profile.getPictureURL() != null) {
            Picasso.with(this)
                    .load(profile.getPictureURL())
                    .into(userPicture);
        }

        String country = (String) profile.getUserMetadata().get("country");
        if (country != null && !country.isEmpty()) {
            mUserCountryTextView.setVisibility(View.VISIBLE);
            mUserCountryTextView.setText(String.format(getString(R.string.userCountry), country));
        }
    }

    private void editProfile() {
        if (mUserId == null) {
            return;
        }
        if (mCancelEditionButton.getVisibility() == View.GONE) {
            editModeOn(true);
        } else {
            String country = mUpdateCountryEditText.getText().toString();
            updateInformation(country);
            editModeOn(false);
        }
    }

    private void updateInformation(String countryUpdate) {
        Map<String, Object> userMetadata = new HashMap<>();
        userMetadata.put("country", countryUpdate);
        mUsersClient.updateMetadata(mUserId, userMetadata)
                .start(new BaseCallback<UserProfile, ManagementException>() {
                    @Override
                    public void onSuccess(final UserProfile profile) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                refreshScreenInformation(profile);
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

    private void editModeOn(boolean editModeOn) {
        if (editModeOn) {
            mUpdateCountryEditText.setVisibility(View.VISIBLE);
            mEditProfileButton.setText(getString(R.string.save));
            mCancelEditionButton.setVisibility(View.VISIBLE);
        } else {
            mEditProfileButton.setText(getString(R.string.edit));
            mUpdateCountryEditText.setText("");
            mUpdateCountryEditText.setVisibility(View.GONE);
            mCancelEditionButton.setVisibility(View.GONE);
        }
    }

    private void loginAgain() {
        CredentialsManager.deleteCredentials(MainActivity.this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
