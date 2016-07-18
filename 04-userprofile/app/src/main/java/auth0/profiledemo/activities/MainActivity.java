package auth0.profiledemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.UserProfile;
import com.auth0.callback.BaseCallback;
import com.squareup.picasso.Picasso;

import auth0.profiledemo.R;
import auth0.profiledemo.application.App;


public class MainActivity extends AppCompatActivity {

    private Button mEditProfileButton;
    private Button mCancelEditionButton;
    private EditText mUsernameEditext;
    private EditText mUsermailEditext;
    private UserProfile mUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // The process to reclaim an UserProfile is preceded by an Authentication call.
        AuthenticationAPIClient client = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));
        client.tokenInfo(App.getInstance().getUserCredentials().getIdToken())
                .start(new BaseCallback<UserProfile>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mUserProfile = payload;
                                mUsernameEditext.setText(mUserProfile.getName());
                                mUsermailEditext.setText(mUserProfile.getEmail());
                                ImageView userPicture = (ImageView)findViewById(R.id.userPicture);
                                Picasso.with(getApplicationContext()).load(payload.getPictureURL()).into(userPicture);
                            }
                        });

                    }

                    @Override
                    public void onFailure(Auth0Exception error) { }
                });


        mEditProfileButton = (Button) findViewById(R.id.editButton);
        mCancelEditionButton = (Button) findViewById(R.id.cancelEditionButton);
        mUsermailEditext = (EditText) findViewById(R.id.usermailEditext);
        mUsernameEditext = (EditText) findViewById(R.id.usernameEditext);



        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        mCancelEditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelProfileEdition();
            }
        });

    }

    private void editProfile() {
        if(!mUsermailEditext.isEnabled()){
        editModeOn(true);
        }
        else{
        editModeOn(false);
        }
    }

    private void editModeOn(boolean editModeOn) {
        if(editModeOn){
            mUsermailEditext.setEnabled(true);
            mUsernameEditext.setEnabled(true);
            mEditProfileButton.setText(getString(R.string.save));
            mCancelEditionButton.setVisibility(View.VISIBLE);
        }
        else {
            mUsermailEditext.setEnabled(false);
            mUsernameEditext.setEnabled(false);
            mEditProfileButton.setText(getString(R.string.edit));
            mCancelEditionButton.setVisibility(View.GONE);
        }
    }

    private void cancelProfileEdition() {
        mUsernameEditext.setText(mUserProfile.getName());
        mUsermailEditext.setText(mUserProfile.getEmail());
        editModeOn(false);
    }
}
