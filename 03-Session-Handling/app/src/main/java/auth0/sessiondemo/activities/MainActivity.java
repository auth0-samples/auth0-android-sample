package auth0.sessiondemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Delegation;
import com.auth0.callback.BaseCallback;

import auth0.sessiondemo.R;
import auth0.sessiondemo.application.App;


public class MainActivity extends AppCompatActivity {

    private Button mNewIDRefreshButton;
    private Button mNewIDTokenButton;
    private Button mLogoutButton;
    private AuthenticationAPIClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClient = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));


        mNewIDRefreshButton = (Button) findViewById(R.id.refreshTokenButton);
        mNewIDTokenButton = (Button) findViewById(R.id.tokenIDButton);

        mLogoutButton = (Button) findViewById(R.id.logout);

        mNewIDRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {getNewIDWithRefreshToken();}
        });
        mNewIDTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewIDWithOldIDToken();
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


    }

    private void getNewIDWithOldIDToken() {
        String idToken = App.getInstance().getUserCredentials().getIdToken();
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));
        authenticationClient.delegationWithIdToken(idToken).start(new BaseCallback<Delegation>() {
            @Override
            public void onSuccess(Delegation payload) {
            payload.getIdToken(); // New ID Token
            payload.getExpiresIn(); // New ID Token Expire Date
            }

            @Override
            public void onFailure(Auth0Exception error) {

            }
        });

    }

    private void getNewIDWithRefreshToken() {
        String refreshToken = App.getInstance().getUserCredentials().getRefreshToken();
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));
        authenticationClient.delegationWithRefreshToken(refreshToken).start(new BaseCallback<Delegation>() {
            @Override
            public void onSuccess(Delegation payload) {
                payload.getIdToken(); // New ID Token
                payload.getExpiresIn(); // New ID Token Expire Date
            }

            @Override
            public void onFailure(Auth0Exception error) {

            }
        });
    }


    private void logout() {
        App.getInstance().setUserCredentials(null);
        startActivity(new Intent(this, auth0.sessiondemo.activities.StartActivity.class));
    }
}
