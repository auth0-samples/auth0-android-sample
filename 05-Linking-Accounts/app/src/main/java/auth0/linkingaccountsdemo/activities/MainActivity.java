package auth0.linkingaccountsdemo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.UserIdentity;
import com.auth0.android.result.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import auth0.linkingaccountsdemo.R;
import auth0.linkingaccountsdemo.application.App;
import auth0.linkingaccountsdemo.utils.Constants;


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
        mLinkedAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String accountConnectionType = ((TextView) view).getText().toString();
                if (isNotPrimaryID(accountConnectionType)) {
                    AlertDialog.Builder unlinkAccountBuilder = new AlertDialog.Builder(MainActivity.this);
                    unlinkAccountBuilder.setMessage(R.string.unlink_account)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    unlink(getIdentityWith(accountConnectionType));
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();


                } else {
                    Toast.makeText(MainActivity.this, "You cannot unlink primary account", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private UserIdentity getIdentityWith(String accountConnectionType) {
        for(UserIdentity identity : mUserProfile.getIdentities()){
            if(identity.getConnection().equals(accountConnectionType))
                return identity;
        }
        return null;
    }

    private boolean isNotPrimaryID(String accountType) {
        return !accountType.equals(mUserProfile.getIdentities().get(0).getConnection());
    }

    private void refreshScreenInformation() {
        mUsermailTextView.setText(getString(R.string.useremail) + " " + mUserProfile.getEmail());
        ImageView userPicture = (ImageView) findViewById(R.id.userPicture);
        Picasso.with(getApplicationContext()).load(mUserProfile.getPictureURL()).into(userPicture);


        ArrayAdapter<String> adapter = createSimpleAdapterWith(mUserProfile.getIdentities());
        mLinkedAccountList.setAdapter(adapter);
    }

    private ArrayAdapter<String> createSimpleAdapterWith(List<UserIdentity> identities) {
        List<String> identitiesTypes = new ArrayList<>();
        for (UserIdentity identity : identities) {
            identitiesTypes.add(identity.getConnection());
        }
        return new ArrayAdapter<>(getBaseContext(),
                R.layout.connectionlist_item, identitiesTypes);
    }

    private void linkAccount() {
        Intent lockToLinkAccounts = new Intent(this, LockActivity.class);
        lockToLinkAccounts.putExtra(Constants.LINK_ACCOUNTS, true);
        startActivity(lockToLinkAccounts);
    }

    private void unlink(UserIdentity secondaryAccountIdentity) {
        UsersAPIClient client = new UsersAPIClient(mAuth0, App.getInstance().getUserCredentials().getIdToken());
        client.unlink(mUserProfile.getIdentities().get(0).getId(), secondaryAccountIdentity.getId(), secondaryAccountIdentity.getProvider());
    }

}
