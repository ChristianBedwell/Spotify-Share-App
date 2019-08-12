package com.example.spotifyauthentication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // start authentication activity
        startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
        // close splash activity
        finish();
    }
}
