package com.auth0.samples

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.lock.AuthenticationCallback
import com.auth0.android.lock.Lock
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val account = Auth0(this)
        // Create a reusable Lock instance
        lock = Lock.newBuilder(account, callback)
            // Customize Lock
            .closable(true)
            .withScheme("demo")
            .build(this)

        findViewById<Button>(R.id.button_login).setOnClickListener { launchLock() }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release Lock resources
        lock.onDestroy(this)
    }

    private fun launchLock() {
        startActivity(lock.newIntent(this))
    }

    private lateinit var lock: Lock
    private val callback = object : AuthenticationCallback() {
        override fun onError(error: AuthenticationException) {
            Snackbar.make(
                findViewById(R.id.content),
                "Error: ${error.getDescription()}",
                Snackbar.LENGTH_LONG
            )
        }

        override fun onAuthentication(credentials: Credentials) {
            val next = Intent(this@MainActivity, AuthenticatedActivity::class.java)
            startActivity(next)
            finish()
        }
    }
}