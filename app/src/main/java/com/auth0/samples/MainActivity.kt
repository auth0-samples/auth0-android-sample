package com.auth0.samples

import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.samples.ui.theme.Auth0androidsampleTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    // highlight-start account-setup
    private lateinit var account: Auth0
    // highlight-end account-setup

    private var credentials by mutableStateOf<Credentials?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // highlight-start account-setup
        val clientId = getString(R.string.com_auth0_client_id)
        val domain = getString(R.string.com_auth0_domain)
        account = Auth0.getInstance(clientId, domain)
        // highlight-end account-setup

        setContent {
            Auth0androidsampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        // highlight-start app
                        if (credentials == null) {
                            Button(onClick = { login("signup") }) {
                                Text("Sign Up")
                            }
                            Button(onClick = { login() }) {
                                Text("Log In")
                            }
                        } else {
                            Text("Logged in as ${credentials?.user?.email}")
                            Text(credentials?.let {
                                JSONObject(String(Base64.decode(it.idToken.split(".")[1], Base64.URL_SAFE))).toString(2)
                            } ?: "{}")
                            Button(onClick = { logout() }) {
                                Text("Log Out")
                            }
                        }
                        // highlight-end app
                    }
                }
            }
        }
    }

    // highlight-start login
    private fun login(screenHint: String? = null) {
        val builder = WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email offline_access")
        if (screenHint != null) {
            builder.withParameters(mapOf("screen_hint" to screenHint))
        }
        builder.start(this, object : Callback<Credentials, AuthenticationException> {
            override fun onSuccess(result: Credentials) {
                credentials = result
            }
            override fun onFailure(error: AuthenticationException) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    // highlight-end login

    // highlight-start logout
    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .start(this, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    credentials = null
                }
                override fun onFailure(error: AuthenticationException) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    // highlight-end logout
}
