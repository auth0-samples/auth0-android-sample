package auth0socialdemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.UserProfile;
import com.auth0.callback.BaseCallback;
import com.squareup.picasso.Picasso;

import auth0socialdemo.R;
import auth0socialdemo.application.App;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationAPIClient client = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));
        client.tokenInfo(App.getInstance().getUserCredentials().getIdToken())
                .start(new BaseCallback<UserProfile>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                ((TextView)findViewById(R.id.username)).setText(payload.getName());
                                ((TextView)findViewById(R.id.usermail)).setText(payload.getEmail());
                                ImageView userPicture = (ImageView)findViewById(R.id.userPicture);
                                Picasso.with(getApplicationContext()).load(payload.getPictureURL()).into(userPicture);
                            }
                        });

                    }

                    @Override
                    public void onFailure(Auth0Exception error) { }
                });

    }
}
