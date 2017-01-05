package com.auth0.customlogindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginAgainButton = (Button) findViewById(R.id.login_again);
        loginAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAgain();
            }
        });

        Toast.makeText(MainActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
    }

    private void loginAgain() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
