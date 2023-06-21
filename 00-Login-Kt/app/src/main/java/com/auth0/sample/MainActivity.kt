package com.auth0.sample

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.management.ManagementException
import com.auth0.android.management.UsersAPIClient
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.auth0.sample.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom

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
        GlobalScope.launch { generateIntegrityToken(generateNonce()) }
    }

    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .start(this, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    // The user has been logged out!
                    cachedCredentials = null
                    cachedUserProfile = null
                    updateUI()
                }

                override fun onFailure(exception: AuthenticationException) {
                    updateUI()
                    showSnackBar("Failure: ${exception.getCode()}")
                }
            })
    }

    private fun showUserProfile() {
        val client = AuthenticationAPIClient(account)

        // Use the access token to call userInfo endpoint.
        // In this sample, we can assume cachedCredentials has been initialized by this point.
        client.userInfo(cachedCredentials!!.accessToken!!)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
                    showSnackBar("Failure: ${exception.getCode()}")
                }

                override fun onSuccess(profile: UserProfile) {
                    cachedUserProfile = profile;
                    updateUI()
                }
            })
    }

    private fun getUserMetadata() {
        // Create the user API client
        val usersClient = UsersAPIClient(account, cachedCredentials!!.accessToken!!)

        // Get the full user profile
        usersClient.getProfile(cachedUserProfile!!.getId()!!)
            .start(object : Callback<UserProfile, ManagementException> {
                override fun onFailure(exception: ManagementException) {
                    showSnackBar("Failure: ${exception.getCode()}")
                }

                override fun onSuccess(userProfile: UserProfile) {
                    cachedUserProfile = userProfile;
                    updateUI()

                    val country = userProfile.getUserMetadata()["country"] as String?
                    binding.inputEditMetadata.setText(country)
                }
            })
    }

    private fun patchUserMetadata() {
        val usersClient = UsersAPIClient(account, cachedCredentials!!.accessToken!!)
        val metadata = mapOf("country" to binding.inputEditMetadata.text.toString())

        usersClient
            .updateMetadata(cachedUserProfile!!.getId()!!, metadata)
            .start(object : Callback<UserProfile, ManagementException> {
                override fun onFailure(exception: ManagementException) {
                    showSnackBar("Failure: ${exception.getCode()}")
                }

                override fun onSuccess(profile: UserProfile) {
                    cachedUserProfile = profile
                    updateUI()
                    showSnackBar("Successful")
                }
            })
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }

    /**
     * The value set in the nonce field must be correctly formatted:
     *  String
     *  URL-safe
     *  Encoded as Base64 and non-wrapping
     *  Minimum of 16 characters
     *  Maximum of 500 characters
     * https://developer.android.com/google/play/integrity/verdict#nonce
     */
    private fun generateNonce(): String {
        val random = SecureRandom()
        val bytes = ByteArray(256)
        random.nextBytes(bytes)
        val encoded = Base64.encode(
            bytes, Base64.URL_SAFE or Base64.NO_WRAP
        )
        return String(encoded)
    }

    private suspend fun generateIntegrityToken(nonceString: String) {
        // Create an instance of an IntegrityManager
        val integrityManager = IntegrityManagerFactory.create(this)

        // Use the nonce to configure a request for an integrity token
        try {
            val integrityTokenResponse: Task<IntegrityTokenResponse> =
                integrityManager.requestIntegrityToken(
                    IntegrityTokenRequest.builder()
                        .setNonce(nonceString)
                        .setCloudProjectNumber(866524548202) //This need not be set when the app is distributed through Play Store -
                        .build()
                )
            // Wait for the integrity token to be generated
            integrityTokenResponse.await()
            if (integrityTokenResponse.isSuccessful && integrityTokenResponse.result != null) {
                try {
                    val credentials = WebAuthProvider.login(account)
                        .withScheme(getString(R.string.com_auth0_scheme))
                        .withScope("openid profile email read:current_user update:current_user_metadata")
                        .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")
                        .withParameters(mapOf(
                            "integrity_token" to integrityTokenResponse.result.token(),
                            "nonce" to nonceString
                        ))
                        .await(this)
                    cachedCredentials = credentials
                    showSnackBar("Success: ${credentials.accessToken}")
                    runOnUiThread { updateUI() }
                } catch (exception: AuthenticationException) {
                    exception.printStackTrace()
                    showSnackBar("Failure: ${exception.getDescription()}")
                }
            } else {
                Log.d("Integrity Check", "requestIntegrityToken failed: " +
                        integrityTokenResponse.result.toString())
            }
        } catch (t: Throwable) {
            Log.d("Integrity Check", "requestIntegrityToken exception " + t.message)
        }

    }
}