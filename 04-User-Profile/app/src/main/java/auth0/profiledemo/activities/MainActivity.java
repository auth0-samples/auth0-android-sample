package auth0.profiledemo.activities;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import auth0.profiledemo.R;
import auth0.profiledemo.application.App;


public class MainActivity extends AppCompatActivity {

    private Button mEditProfileButton;
    private Button mCancelEditionButton;
    private TextView mUsernameTextView;
    private TextView mUsermailTextView;
    private TextView mUserCountryTextView;
    private EditText mUpdateCountryEditext;
    private UserProfile mUserProfile;
    private Auth0 mAuth0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        // The process to reclaim an UserProfile is preceded by an Authentication call.
        AuthenticationAPIClient aClient = new AuthenticationAPIClient(mAuth0);
        aClient.tokenInfo(App.getInstance().getUserCredentials().getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mUserProfile = payload;
                                refreshScreenInformation();
                            }
                        });

                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Profile Request Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


        mEditProfileButton = (Button) findViewById(R.id.editButton);
        mCancelEditionButton = (Button) findViewById(R.id.cancelEditionButton);
        mUsernameTextView = (TextView) findViewById(R.id.userNameTitle);
        mUsermailTextView = (TextView) findViewById(R.id.userEmailTitle);
        mUserCountryTextView = (TextView) findViewById(R.id.userCountryTitle);
        mUpdateCountryEditext = (EditText) findViewById(R.id.updateCountryEdittext);

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

    }

    private void refreshScreenInformation() {
        mUsernameTextView.setText(getString(R.string.username) + " " + mUserProfile.getName());
        mUsermailTextView.setText(getString(R.string.useremail) + " " + mUserProfile.getEmail());
        ImageView userPicture = (ImageView) findViewById(R.id.userPicture);
        Picasso.with(getApplicationContext()).load(mUserProfile.getPictureURL()).into(userPicture);
        if (!mUserProfile.getUserMetadata().get("country").toString().isEmpty()) {
            mUserCountryTextView.setVisibility(View.VISIBLE);
            mUserCountryTextView.setText(getString(R.string.userCountry) + " " + mUserProfile.getUserMetadata().get("country").toString());
        }
    }

    private void editProfile() {
        if (mCancelEditionButton.getVisibility() == View.GONE) {
            editModeOn(true);
        } else {
            updateProfile(mUpdateCountryEditext.getText().toString());
            editModeOn(false);
        }
    }

    private void updateProfile(String countryUpdate) {
        UsersAPIClient userClient = new UsersAPIClient(mAuth0, App.getInstance().getUserCredentials().getIdToken());
        Map<String, Object> userMetadata = new HashMap<>();
        userMetadata.put("country", countryUpdate);
        userClient.updateMetadata(mUserProfile.getId(), userMetadata).start(new BaseCallback<UserProfile, ManagementException>() {
            @Override
            public void onSuccess(final UserProfile payload) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mUserProfile = payload;
                        refreshScreenInformation();
                    }
                });
            }

            @Override
            public void onFailure(ManagementException error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Profile Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void editModeOn(boolean editModeOn) {
        if (editModeOn) {
            mUpdateCountryEditext.setVisibility(View.VISIBLE);
            mEditProfileButton.setText(getString(R.string.save));
            mCancelEditionButton.setVisibility(View.VISIBLE);
        } else {
            mEditProfileButton.setText(getString(R.string.edit));
            mUpdateCountryEditext.setText("");
            mUpdateCountryEditext.setVisibility(View.GONE);
            mCancelEditionButton.setVisibility(View.GONE);
        }
    }
}
