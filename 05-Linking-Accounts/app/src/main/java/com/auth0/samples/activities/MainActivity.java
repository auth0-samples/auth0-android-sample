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
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.jwt.JWT;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserIdentity;
import com.auth0.android.result.UserProfile;
import com.auth0.samples.R;
import com.auth0.samples.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView userEmailTextView;
    private ImageView userPicture;
    private UserProfile userProfile;
    private ListView linkedAccountList;

    private Auth0 auth0;
    private SecureCredentialsManager credentialsManager;
    private UsersAPIClient usersClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

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
        fetchProfile();
    }

    private void fetchProfile() {
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {

            @Override
            public void onSuccess(Credentials credentials) {
                if (usersClient == null) {
                    usersClient = new UsersAPIClient(auth0, credentials.getIdToken());
                }
                String userId = new JWT(credentials.getIdToken()).getSubject();
                usersClient.getProfile(userId)
                        .start(new BaseCallback<UserProfile, ManagementException>() {
                            @Override
                            public void onSuccess(UserProfile fullProfile) {
                                userProfile = fullProfile;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        refreshScreenInformation();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(ManagementException error) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Profile Request Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed due to expired credentials. Please, log in again to your main account.", Toast.LENGTH_LONG).show();
                    }
                });
                loginAgain();
            }
        });
    }

    private boolean isPrimaryIdentity(UserIdentity identity) {
        return identity.equals(userProfile.getIdentities().get(0));
    }

    private void refreshScreenInformation() {
        userEmailTextView.setText(String.format(getString(R.string.userEmail), userProfile.getEmail()));
        Picasso.with(this).load(userProfile.getPictureURL()).into(userPicture);
        updateIdentities(userProfile.getIdentities());
    }

    private void updateIdentities(List<UserIdentity> identities) {
        if (identities == null) {
            return;
        }
        List<String> identitiesTypes = new ArrayList<>();
        for (UserIdentity identity : identities) {
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
                    public void onSuccess(List<UserIdentity> identities) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Account unlinked!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        updateIdentities(identities);
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
        credentialsManager.clearCredentials();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
