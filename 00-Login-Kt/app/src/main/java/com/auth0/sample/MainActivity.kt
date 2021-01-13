package com.auth0.sample

import android.os.Bundle    
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.Auth0Exception
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.management.ManagementException
import com.auth0.android.management.UsersAPIClient
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
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")

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

                    showUserProfile(credentials)
                }
            })
    }

    private fun logout() {
        WebAuthProvider.logout(account)
                .withScheme(getString(R.string.com_auth0_scheme))
                .start(this, object: Callback<Void, AuthenticationException> {
                    override fun onSuccess(payload: Void?) {
                        // The user has been logged out!
                    }

                    override fun onFailure(exception: AuthenticationException) {
                        Snackbar.make(
                                binding.root,
                                "Failure: ${exception.message}",
                        Snackbar.LENGTH_LONG
                        ).show()
                    }
                })
    }

    private fun showUserProfile(credentials: Credentials?) {
        val client = AuthenticationAPIClient(account)

        // Use the access token to call userInfo endpoint
        credentials?.accessToken?.let { accessToken ->
            client.userInfo(accessToken)
                .start(object : Callback<UserProfile, AuthenticationException> {
                    override fun onFailure(exception: AuthenticationException) {
                        Snackbar.make(
                            binding.root,
                            "Failure: ${exception.getCode()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    override fun onSuccess(profile: UserProfile?) {
                        binding.userProfile.text =
                            "Name: ${profile?.name}\n" +
                            "Email: ${profile?.email}"

                        // Get the user ID and call the full getUser Management API endpoint, to retrieve the full profile information
                        profile?.getId()?.let { userId ->
                            // Create the user API client
                            val usersClient = UsersAPIClient(account, accessToken)

                            // Get the full user profile
                            usersClient.getProfile(userId).start(object: Callback<UserProfile, ManagementException> {
                                override fun onFailure(exception: ManagementException) {
                                    Snackbar.make(
                                        binding.root,
                                        "Failure: ${exception.getCode()}",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }

                                override fun onSuccess(payload: UserProfile?) {
                                    // Display the "country" field, if one appears in the metadata
                                    binding.userMeta.text = payload?.getUserMetadata()?.get("country") as String? ?: ""
                                }
                            })
                        }
                    }
        }) }
    }

}