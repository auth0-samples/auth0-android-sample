package com.auth0.sample

import android.os.Bundle    
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.Auth0Exception
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.VoidCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.auth0.sample.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var account: Auth0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //1. Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        //2. Bind the button click with the login action
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener { loginWithBrowser() }
        binding.buttonLogout.setOnClickListener { logout() }
    }

    private fun loginWithBrowser() {
        //3. Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email")
            //4. Launch the authentication passing the callback where the results will be received
            .start(this, object : Callback<Credentials, AuthenticationException> {

                override fun onFailure(exception: AuthenticationException) {
                    Snackbar.make(
                        binding.root,
                        "Failure: ${exception.getCode()}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                override fun onSuccess(credentials: Credentials?) {
                    Snackbar.make(
                        binding.root,
                        "Success: ${credentials?.accessToken}",
                        Snackbar.LENGTH_LONG
                    ).show()

                    showUserInfo(payload)
                }
            })
    }

    private fun logout() {
        WebAuthProvider.logout(account)
                .withScheme(getString(R.string.com_auth0_scheme))
                .start(this, object: VoidCallback {
                    override fun onSuccess(payload: Void?) {
                        // The user has been logged out!
                    }

                    override fun onFailure(error: Auth0Exception) {
                        Snackbar.make(
                                binding.root,
                                "Failure: ${error.message}",
                        Snackbar.LENGTH_LONG
                        ).show()
                    }
                })
    }

    private fun showUserInfo(credentials: Credentials?) {
        var client = AuthenticationAPIClient(account)

        credentials?.accessToken?.let {
            client.userInfo(it)
                .start(object : Callback<UserProfile, AuthenticationException> {
                    override fun onFailure(error: AuthenticationException) {
                        Snackbar.make(
                            binding.root,
                            "Failure: ${error.getCode()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    override fun onSuccess(payload: UserProfile?) {
                        binding.userProfile.setText(
                                "Name: ${payload?.name}\n" +
                                "Email: ${payload?.email}")
                    }
        }) }
    }

}