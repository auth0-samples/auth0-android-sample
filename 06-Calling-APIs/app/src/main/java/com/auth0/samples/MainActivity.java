package com.auth0.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "YOUR API URL";

    private String accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button callAPIWithTokenButton = findViewById(R.id.callAPIWithTokenButton);
        Button callAPIWithoutTokenButton = findViewById(R.id.callAPIWithoutTokenButton);
        Button loginWithTokenButton = findViewById(R.id.loginButton);
        callAPIWithTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI(true);
            }
        });
        callAPIWithoutTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI(false);
            }
        });
        loginWithTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        accessToken = getIntent().getStringExtra(LoginActivity.EXTRA_ACCESS_TOKEN);
    }

    private void callAPI(boolean sendToken) {
        final Request.Builder reqBuilder = new Request.Builder()
                .get()
                .url(API_URL);
        if (sendToken) {
            if (accessToken == null) {
                Toast.makeText(MainActivity.this, "Token not found. Log in first.", Toast.LENGTH_SHORT).show();
                return;
            }
            reqBuilder.addHeader("Authorization", "Bearer " + accessToken);
        }

        OkHttpClient client = new OkHttpClient();
        Request request = reqBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "An error occurred" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "API call success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "API call failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_CLEAR_CREDENTIALS, true);
        startActivity(intent);
        finish();

    }
}
