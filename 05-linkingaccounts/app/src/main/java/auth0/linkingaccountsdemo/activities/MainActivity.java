package auth0.linkingaccountsdemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.UserIdentity;
import com.auth0.android.result.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

import auth0.linkingaccountsdemo.R;
import auth0.linkingaccountsdemo.application.App;


public class MainActivity extends AppCompatActivity {

    private Button mLinkAccountButton;
    private TextView mUsermailTextView;
    private UserProfile mUserProfile;
    private Auth0 mAuth0;
    private ListView mLinkedAccountList;


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



        mUsermailTextView = (TextView) findViewById(R.id.userEmailTitle);

        mLinkAccountButton = (Button) findViewById(R.id.linkAccountButton);
        mLinkAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkAccount();
            }
        });

        mLinkedAccountList = (ListView) findViewById(R.id.linkedAccountsList);


    }

    private void refreshScreenInformation() {
        mUsermailTextView.setText(getString(R.string.useremail) + " " + mUserProfile.getEmail());
        ImageView userPicture = (ImageView) findViewById(R.id.userPicture);
        Picasso.with(getApplicationContext()).load(mUserProfile.getPictureURL()).into(userPicture);


        ArrayAdapter<String> adapter = createSimpleAdapterWith(mUserProfile.getIdentities());
        mLinkedAccountList.setAdapter(adapter);
    }

    private ArrayAdapter<String> createSimpleAdapterWith(List<UserIdentity> identities) {
        String[] identitiesArray = new String[200];
        return new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, identities.toArray(identitiesArray));

    }

    private void linkAccount() {

    }

}
