package com.auth0.callyourapidemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.callyourapidemo.R;
import com.auth0.callyourapidemo.utils.CredentialsManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button authenticatedRequestButton = (Button) findViewById(R.id.authenticatedButton);
        Button nonAuthenticatedRequestButton = (Button) findViewById(R.id.nonAuthenticatedButton);
        Button loginAgainButton = (Button) findViewById(R.id.login_again);

        authenticatedRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateWithAccessToken(true);
            }
        });

        nonAuthenticatedRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateWithAccessToken(false);
            }
        });

        loginAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAgain();
            }
        });
    }

    /**
     * This method request should work fine, if your server configuration is ok
     * and if you send the proper idToken
     */
    private void authenticateWithAccessToken(boolean sendToken) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "YOUR API URL"; // TODO Replace this

        String accessToken = sendToken ? CredentialsManager.getCredentials(this).getAccessToken() : null;
        AuthorizationRequestObject authorizationRequest = new AuthorizationRequestObject
                (Request.Method.GET, url, accessToken,
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        Log.i("API CALL SUCCESSFUL", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                        Log.i("API CALL FAILED", error.toString());
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(authorizationRequest);
    }

    public class AuthorizationRequestObject extends JsonObjectRequest {
        private String accessToken;

        AuthorizationRequestObject(int method, String url, String accessToken, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
            this.accessToken = accessToken;
        }

        @Override
        public Map getHeaders() throws AuthFailureError {
            Map headers = new HashMap();
            if (accessToken != null) {
                headers.put("Authorization", "Bearer " + accessToken);
            }
            return headers;
        }
    }

    private void loginAgain() {
        startActivity(new Intent(this, LoginActivity.class));
        CredentialsManager.deleteCredentials(this);
        finish();
    }
}
