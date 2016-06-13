package com.auth0.passwordless.email;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;

public class RequestActivity extends Activity {

    private static final String EMAIL = "EMAIL";
    private static final String TOKEN = "TOKEN";
    private static final String PICTURE_URL = "PICTURE_URL";

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        final Intent intent = getIntent();
        final String token = intent.getStringExtra(EMAIL);
        String email = intent.getStringExtra(TOKEN);
        String url = intent.getStringExtra(PICTURE_URL);

        client = new OkHttpClient();

        TextView emailLabel = (TextView) findViewById(R.id.username_label);
        emailLabel.setText(email);
        TextView tokenLabel = (TextView) findViewById(R.id.token_label);
        tokenLabel.setText(token);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile_image);
        Picasso.with(this).load(url).into(profileImageView);
        final TextView nonSecureStatus = (TextView) findViewById(R.id.non_secured_status);
        final TextView securedStatus = (TextView) findViewById(R.id.secured_status);

        final HttpUrl pingUrl = HttpUrl.parse(getString(R.string.sample_api_base_url)).newBuilder()
                .addPathSegment("ping")
                .build();
        final Request request = new Request.Builder()
                .url(pingUrl)
                .build();
        pingWithRequest(request, new PingCallback() {
            @Override
            public void onResult(boolean success) {
                nonSecureStatus.setText(success ? R.string.tick_symbol : R.string.cross_symbol);
                nonSecureStatus.setTextColor(getResources().getColor(success ? R.color.request_success : R.color.request_fail));
            }
        });

        Button callButton = (Button) findViewById(R.id.call_api_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final HttpUrl pingUrl = HttpUrl.parse(getString(R.string.sample_api_base_url)).newBuilder()
                        .addPathSegment("secured")
                        .addPathSegment("ping")
                        .build();
                final Request request = new Request.Builder()
                        .url(pingUrl)
                        .addHeader("Bearer " + token, "Authorization")
                        .build();
                pingWithRequest(request, new PingCallback() {
                    @Override
                    public void onResult(boolean success) {
                        securedStatus.setText(success ? R.string.tick_symbol : R.string.cross_symbol);
                        securedStatus.setTextColor(getResources().getColor(success ? R.color.request_success : R.color.request_fail));
                    }
                });
            }
        });
    }

    private void pingWithRequest(Request request, final PingCallback callback) {
        final Handler handler = new Handler(getMainLooper());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(false);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(response.isSuccessful());
                    }
                });
            }
        });
    }


    private interface PingCallback {
        void onResult(boolean success);
    }

    public static Intent newIntent(Context context, String email, String token, String pictureUrl) {
        final Intent intent = new Intent(context, RequestActivity.class);
        intent.putExtra(EMAIL, email);
        intent.putExtra(TOKEN, token);
        intent.putExtra(PICTURE_URL, pictureUrl);
        return intent;
    }
}
