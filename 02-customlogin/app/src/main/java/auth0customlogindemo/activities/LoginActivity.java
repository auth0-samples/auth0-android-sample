package auth0customlogindemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Credentials;
import com.auth0.callback.BaseCallback;

import auth0customlogindemo.R;


/**
 * Created by emi on 27/6/16.
 */

public class LoginActivity extends Activity{

        private EditText mEmailEditext;
        private EditText mPasswordEditext;
        private Button mLoginButton;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // Bind your views
            mEmailEditext = (EditText) findViewById(R.id.emailEditext);
            mPasswordEditext = (EditText) findViewById(R.id.passwordEditext);
            mLoginButton = (Button) findViewById(R.id.loginButton);

            // Add the onClick listener to the login
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login(mEmailEditext.getText().toString(), mPasswordEditext.getText().toString());
                }
            });


        }

    private void login(String email, String password) {
        Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        AuthenticationAPIClient client = new AuthenticationAPIClient(auth0);
        client.login(email, password).start(new BaseCallback<Credentials>() {
            @Override
            public void onSuccess(Credentials payload) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }

            @Override
            public void onFailure(Auth0Exception error) {

            }
        });
    }
}


