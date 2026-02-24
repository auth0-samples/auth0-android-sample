package com.auth0.samples

import android.os.Bundle
import android.widget.Toast
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import com.auth0.android.Auth0
import com.auth0.android.authentication.*
import com.auth0.android.authentication.storage.*
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials

class MainActivity : ComponentActivity() {
    /* highlight-start account-setup */
    // Initialized lazily because getString() requires Activity context
    private val account: Auth0 by lazy {
        Auth0.getInstance(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )
    }
    private val manager: CredentialsManager by lazy {
        CredentialsManager(AuthenticationAPIClient(account), SharedPreferencesStorage(this))
    }

    // Compose re-renders the UI when these change
    private var credentials by mutableStateOf<Credentials?>(null)
    private var isLoading by mutableStateOf(true)
    /* highlight-end account-setup */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /* highlight-start credentials-manager */
        // Restore session from disk; refreshes the access token if expired
        manager.getCredentials(object : Callback<Credentials, CredentialsManagerException> {
            override fun onSuccess(result: Credentials) {
                credentials = result
                isLoading = false
            }

            override fun onFailure(error: CredentialsManagerException) {
                isLoading = false
            }
        })
        /* highlight-end credentials-manager */

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                /* highlight-start app */
                if (isLoading) {
                    Text("Loading...")
                } else if (credentials == null) {
                    Button(onClick = { login("signup") }) { Text("Sign Up") }
                    Button(onClick = { login() }) { Text("Log In") }
                } else {
                    Text("Logged in as ${credentials?.user?.email}", fontSize = 18.sp)
                    Column {
                        Text("sub: ${credentials?.user?.getId()}")
                        Text("name: ${credentials?.user?.name}")
                        Text("email: ${credentials?.user?.email}")
                        Text("email_verified: ${credentials?.user?.isEmailVerified}")
                        Text("nickname: ${credentials?.user?.nickname}")
                        Text("picture: ${credentials?.user?.pictureURL}")
                    }
                    Button(onClick = { logout() }) { Text("Log Out") }
                }
                /* highlight-end app */
            }
        }
    }

    /* highlight-start login */
    private fun login(screenHint: String? = null) {
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            // offline_access: requests a refresh token for session persistence
            .withScope("openid profile email offline_access")
            .withParameters(buildMap { screenHint?.let { put("screen_hint", it) } })
            .start(this, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    credentials = result
                    manager.saveCredentials(result)
                }

                override fun onFailure(error: AuthenticationException) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    /* highlight-end login */

    /* highlight-start logout */
    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .start(this, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    credentials = null
                    manager.clearCredentials()
                }

                override fun onFailure(error: AuthenticationException) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    /* highlight-end logout */
}

