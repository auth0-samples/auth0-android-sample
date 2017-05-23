package com.auth0.samples.activities;

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
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.UserIdentity;
import com.auth0.android.result.UserProfile;
import com.auth0.samples.R;
import com.auth0.samples.utils.Constants;
import com.auth0.samples.utils.CredentialsManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Auth0 auth0;
    private UsersAPIClient usersClient;
    private TextView userEmailTextView;
    private ImageView userPicture;
    private UserProfile userProfile;
    private ListView linkedAccountList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        auth0.setOIDCConformant(true);
        usersClient = new UsersAPIClient(auth0, CredentialsManager.getCredentials(MainActivity.this).getIdToken());

        userEmailTextView = (TextView) findViewById(R.id.userEmailTitle);
        userPicture = (ImageView) findViewById(R.id.userPicture);

        Button linkAccountButton = (Button) findViewById(R.id.linkAccountButton);
        linkAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkAccount();
            }
        });

        Button loginAgainButton = (Button) findViewById(R.id.logout);
        loginAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAgain();
            }
        });

        linkedAccountList = (ListView) findViewById(R.id.linkedAccountsList);
        linkedAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UserIdentity identity = userProfile.getIdentities().get(position);
                if (isPrimaryIdentity(identity)) {
                    Toast.makeText(MainActivity.this, "You cannot unlink primary account", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.unlink_account)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                unlink(identity);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileInfo();
    }

    private void fetchProfileInfo() {
        // The process to reclaim User Information is preceded by an Authentication call.
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(auth0);
        authenticationClient.userInfo(CredentialsManager.getCredentials(MainActivity.this).getAccessToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
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
                    public void onFailure(AuthenticationException error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Profile Request Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private boolean isPrimaryIdentity(UserIdentity identity) {
        return identity.equals(userProfile.getIdentities().get(0));
    }

    private void refreshScreenInformation() {
        userEmailTextView.setText(String.format(getString(R.string.userEmail), userProfile.getEmail()));
        Picasso.with(this).load(userProfile.getPictureURL()).into(userPicture);
        if (userProfile.getIdentities() == null) {
            return;
        }

        List<String> identitiesTypes = new ArrayList<>();
        for (UserIdentity identity : userProfile.getIdentities()) {
            identitiesTypes.add(identity.getConnection());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, identitiesTypes);
        linkedAccountList.setAdapter(adapter);
    }

    private void linkAccount() {
        Intent linkAccounts = new Intent(this, LoginActivity.class);
        linkAccounts.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        linkAccounts.putExtra(Constants.LINK_ACCOUNTS, true);
        linkAccounts.putExtra(Constants.PRIMARY_USER_ID, userProfile.getId());
        startActivity(linkAccounts);
    }

    private void unlink(UserIdentity secondaryAccountIdentity) {
        usersClient.unlink(userProfile.getId(), secondaryAccountIdentity.getId(), secondaryAccountIdentity.getProvider())
                .start(new BaseCallback<List<UserIdentity>, ManagementException>() {
                    @Override
                    public void onSuccess(List<UserIdentity> payload) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Account unlinked!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        fetchProfileInfo();
                    }

                    @Override
                    public void onFailure(final ManagementException error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Account unlink failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void loginAgain() {
        CredentialsManager.deleteCredentials(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
