package auth0.callyourapidemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import auth0.callyourapidemo.R;
import auth0.callyourapidemo.application.App;



public class MainActivity extends AppCompatActivity {

    private Button mAuthenticatedRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthenticatedRequestButton = (Button) findViewById(R.id.tokenIDButton);

        mAuthenticatedRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateWithTokenID();
            }
        });


    }

    private void authenticateWithTokenID() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "your api url";

        AuthorizationRequestObject authorizationRequest = new AuthorizationRequestObject
                (Request.Method.GET, url, App.getInstance().getUserCredentials().getIdToken(),
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse Response
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Add the request to the RequestQueue.
        queue.add(authorizationRequest);

    }

    public class AuthorizationRequestObject extends JsonObjectRequest
    {
        private String headerTokenID = null;

        public AuthorizationRequestObject(int method, String url, String tokenID, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener)
        {
            super(method, url, jsonRequest, listener, errorListener);
            headerTokenID = tokenID;
        }

        @Override
        public Map getHeaders() throws AuthFailureError {
            Map headers = new HashMap();
            headers.put("Bearer \\"+headerTokenID, "Authorization");
            return headers;
        }

    }

}
