package com.example.spotifyauthentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // constants
    public static final String CLIENT_ID = "clientid";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final String EXTRA_MESSAGE = "com.example.spotifyauthentication.extra.MESSAGE";

    private String mAccessToken;

    CheckBox checkbox;
    Button buttonAuthenticate;
    ProgressBar progressBar;
    TextView progressMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(String.format(
                Locale.US, "Spotify Analytics", com.spotify.sdk.android.authentication.BuildConfig.VERSION_NAME));

        // instantiate agreement checkbox, authentication button, and status bar/message
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        buttonAuthenticate = (Button) findViewById(R.id.authentication_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressMessage = (TextView) findViewById(R.id.progressMessage);

        // by default, checkbox state is off because the user hasn't clicked the checkbox
        buttonAuthenticate.setEnabled(false);

        // by default, progress bar/message is invisible because the user hasn't clicked authenticate
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        progressMessage.setVisibility(TextView.INVISIBLE);

        // create click listener for checkbox state changes
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // checks checkbox state, used to toggle authentication button state on and off
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if user has agreed to authentication statement, enable authentication button
                if (isChecked) {
                    buttonAuthenticate.setEnabled(true);
                }
                // if user has not agreed to authentication statement, disable authentication button
                else {
                    buttonAuthenticate.setEnabled(false);
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

    public void requestToken() {
        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        // if access token is not received, show snackbar message
        if (mAccessToken == null) {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main), R.string.warning_need_token, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            snackbar.show();
        }

        // check response for equivalent access token
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
        }

        // store access token in intent extra and pass it to MostPopularActivity
        Intent intent = new Intent(getApplicationContext(), MostPopularActivity.class);
        intent.putExtra(EXTRA_MESSAGE, mAccessToken);
        startActivity(intent);
    }

    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }
}

