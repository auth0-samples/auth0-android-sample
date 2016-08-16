package auth0.linkingaccountsdemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.Credentials;

import auth0.linkingaccountsdemo.R;
import auth0.linkingaccountsdemo.application.App;
import auth0.linkingaccountsdemo.utils.Constants;


public class LockActivity extends Activity {

    private Lock lock;
    private Auth0 auth0;
    private Boolean mLinkSessions;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        this.lock = Lock.newBuilder(auth0, callback)
                //Add parameters to the build
                .build();
        lock.onCreate(this);
        auth0.getAuthorizeUrl();
        mLinkSessions = getIntent().getExtras().getBoolean(Constants.LINK_ACCOUNTS, false);
        startActivity(this.lock.newIntent(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        lock.onDestroy(this);
        lock = null;
    }

    private LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            Toast.makeText(getApplicationContext(), "Log In - Success", Toast.LENGTH_SHORT).show();

            if (mLinkSessions) {
                UsersAPIClient client = new UsersAPIClient(auth0, credentials.getIdToken());
                client.link(App.getInstance().getUserCredentials().getIdToken(),
                        credentials.getIdToken());
            } else {
                App.getInstance().setUserCredentials(credentials);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
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


