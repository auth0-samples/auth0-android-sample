package auth0.rulesdemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.UserProfile;
import com.auth0.callback.BaseCallback;
import com.squareup.picasso.Picasso;

import auth0.rulesdemo.R;
import auth0.rulesdemo.application.App;

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

                                // Get the country from the user profile
                                // This is included in the extra info... and must be enabled in the Auth0 rules web.
                                try {
                                    ((TextView)findViewById(R.id.userCountry)).setText(payload.getExtraInfo().get("country").toString());
                                }catch (Exception e ){
                                    Log.e("AUTH0", "Failed assigning country info... check if country rule is enabled in Auth0 web");
                                }

                            }
                        });

                    }

                    @Override
                    public void onFailure(Auth0Exception error) { }
                });

    }
}
