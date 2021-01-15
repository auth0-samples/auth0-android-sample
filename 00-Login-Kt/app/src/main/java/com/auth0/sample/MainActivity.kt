package com.auth0.sample

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.transition.Visibility
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
    private var cachedCredentials: Credentials? = null
    private var cachedUserProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        // Bind the button click with the login action
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener { loginWithBrowser() }
        binding.buttonLogout.setOnClickListener { logout() }
        binding.buttonGetMetadata.setOnClickListener { getUserMetadata() }
        binding.buttonPatchMetadata.setOnClickListener { patchUserMetadata() }
    }

    private fun updateUI() {
        binding.buttonLogout.isEnabled = cachedCredentials != null
        binding.metadataPanel.isVisible = cachedCredentials != null
        binding.buttonLogin.isEnabled = cachedCredentials == null
        binding.userProfile.isVisible = cachedCredentials != null

        binding.userProfile.text =
            "Name: ${cachedUserProfile?.name ?: ""}\n" +
            "Email: ${cachedUserProfile?.email ?: ""}"

        if (cachedUserProfile == null) {
            binding.inputEditMetadata.setText("")
        }
    }

    private fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")

            // Launch the authentication passing the callback where the results will be received
            .start(this, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
                    Snackbar.make(
                        binding.root,
                        "Failure: ${exception.getCode()}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                override fun onSuccess(credentials: Credentials?) {
                    cachedCredentials = credentials!!

                    Snackbar.make(
                        binding.root,
                        "Success: ${credentials?.accessToken}",
                        Snackbar.LENGTH_LONG
                    ).show()

                    updateUI()
                    showUserProfile()
                }
            })
    }

    private fun logout() {
        WebAuthProvider.logout(account)
                .withScheme(getString(R.string.com_auth0_scheme))
                .start(this, object: Callback<Void, AuthenticationException> {
                    override fun onSuccess(payload: Void?) {
                        // The user has been logged out!
                        cachedCredentials = null
                        cachedUserProfile = null
                        updateUI()
                    }

                    override fun onFailure(exception: AuthenticationException) {
                        updateUI()

                        Snackbar.make(
                                binding.root,
                                "Failure: ${exception.message}",
                        Snackbar.LENGTH_LONG
                        ).show()
                    }
                })
    }

    private fun showUserProfile() {
        val client = AuthenticationAPIClient(account)

        // Use the access token to call userInfo endpoint
        cachedCredentials?.accessToken?.let { accessToken ->
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
                        cachedUserProfile = profile;
                        updateUI()
                    }
        }) }
    }

    private fun getUserMetadata() {
        // Get the access token so that we can make calls to the management API
        cachedCredentials?.accessToken?.let { accessToken ->
            // Get the user ID and call the full getUser Management API endpoint, to retrieve the full profile information
            cachedUserProfile?.getId()?.let { userId ->
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

                    override fun onSuccess(userProfile: UserProfile?) {
                        cachedUserProfile = userProfile;
                        updateUI()
                        binding.inputEditMetadata.setText(cachedUserProfile?.getUserMetadata()?.get("country") as String? ?: "")
                    }
                })
            }
        }
    }

    private fun patchUserMetadata() {
        // Get the access token so that we can make calls to the management API
        cachedCredentials?.accessToken?.let { accessToken ->
            // Get the user ID and call the full getUser Management API endpoint, to retrieve the full profile information
            cachedUserProfile?.getId()?.let { userId ->
                val usersClient = UsersAPIClient(account, accessToken)
                val metadata = mapOf("country" to binding.inputEditMetadata.getText().toString())

                usersClient
                        .updateMetadata(userId, metadata)
                        .start(object: Callback<UserProfile, ManagementException> {
                            override fun onFailure(exception: ManagementException) {
                                Snackbar.make(
                                    binding.root,
                                    "Failure: ${exception.getCode()}",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }

                            override fun onSuccess(profile: UserProfile?) {
                                cachedUserProfile = profile
                                updateUI()

                                Snackbar.make(
                                        binding.root,
                                        "Successful",
                                        Snackbar.LENGTH_LONG
                                ).show()
                            }
                        })
            }
        }
    }
}