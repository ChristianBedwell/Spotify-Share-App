package com.example.spotifyauthentication.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.spotifyauthentication.R;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

    // declare constants
    public static final String CLIENT_ID = "cd58168e38d84c43b7433d471d1c5942";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;

    private String mAccessToken;

    // declare UI widgets
    private CheckBox checkbox;
    private Button buttonAuthenticate;
    private ProgressBar progressBar;
    private TextView progressMessage;
    private Toolbar toolbar;

    // key for access token
    private final String TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // add custom toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // instantiate agreement checkbox, authentication button, and status bar/message
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        buttonAuthenticate = (Button) findViewById(R.id.authentication_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressMessage = (TextView) findViewById(R.id.progressMessage);

        // by default, button state is off because the user hasn't clicked the checkbox
        buttonAuthenticate.setVisibility(View.INVISIBLE);

        // by default, progress bar/message is invisible because the user hasn't clicked authenticate
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        progressMessage.setVisibility(TextView.INVISIBLE);

        // create click listener for checkbox state changes
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // checks checkbox state, used to toggle authentication button state on and off
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if user has agreed to authentication statement, enable authentication button
                if (isChecked) {
                    buttonAuthenticate.setVisibility(View.VISIBLE);
                }
                // if user has not agreed to authentication statement, disable authentication button
                else {
                    buttonAuthenticate.setVisibility(View.INVISIBLE);;
                }
            }
        });

        // create click listener for authentication button clicks
        buttonAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request access token
                requestToken();

                // run a background task in webview, display progress bar and progress message
                progressBar.setVisibility(ProgressBar.VISIBLE);
                progressMessage.setVisibility(TextView.VISIBLE);
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    public void requestToken() {
        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email", "user-top-read"})
                .setCampaign("your-campaign-token")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        // get access token from authentication client response
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {

            mAccessToken = response.getAccessToken();

            // if access token is not received, show snackbar message and exit
            if (mAccessToken == null) {
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main), R.string.warning_need_token, Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                snackbar.show();
                return;
            }
        }

        // store access token in shared preferences and navigate to MostPopularActivity
        setDefaults(TOKEN_KEY, mAccessToken, this);
        Intent intent = new Intent(getApplicationContext(), MostPopularActivity.class);
        startActivity(intent);
        finish();
    }

    // set default shared preferences for app
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // get redirect uri using redirect scheme and host
    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }
}

