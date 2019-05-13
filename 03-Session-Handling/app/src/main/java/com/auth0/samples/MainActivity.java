package com.auth0.samples;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.customtabs.CustomTabsIntent;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView credentialsView = (TextView) findViewById(R.id.credentials);
        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //Obtain the token from the Intent's extras
        String accessToken = getIntent().getStringExtra(LoginActivity.EXTRA_ACCESS_TOKEN);
        credentialsView.setText(accessToken);
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.KEY_CLEAR_CREDENTIALS, true);
        startActivity(intent);

        String returnTo = new Uri.Builder()
                .scheme("demo")
                .authority(getString(R.string.com_auth0_domain))
                .appendPath("android")
                .appendPath(getPackageName())
                .appendPath("callback")
                .build()
                .toString();

        String logoutUrl = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.com_auth0_domain))
                .appendPath("v2")
                .appendPath("logout")
                .appendQueryParameter("client_id", getString(R.string.com_auth0_client_id))
                .appendQueryParameter("returnTo", returnTo)
                .toString();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(logoutUrl));

        finish();
    }
}
