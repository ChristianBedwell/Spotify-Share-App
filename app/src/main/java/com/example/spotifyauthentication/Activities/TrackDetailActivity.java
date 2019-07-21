package com.example.spotifyauthentication.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifyauthentication.R;
import com.spotify.protocol.client.ErrorCallback;
import com.squareup.picasso.Picasso;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.Locale;

public class TrackDetailActivity extends AppCompatActivity {

    private static final String TAG = TrackDetailActivity.class.getSimpleName();
    String trackUri;
    TextView trackName, trackArtist;
    ImageView trackImage;
    Button playButton, shareButton;

    private static SpotifyAppRemote mSpotifyAppRemote;

    private final ErrorCallback mErrorCallback = throwable -> logError(throwable, "Boom!");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        // set title of action bar from default
        getSupportActionBar().setTitle(String.format(
                Locale.US, "Share Track",
                com.spotify.sdk.android.authentication.BuildConfig.VERSION_NAME));

        // initialize the views
        trackName = (TextView) findViewById(R.id.track_detail_name);
        trackArtist = (TextView) findViewById(R.id.track_detail_artist);
        trackImage = (ImageView) findViewById(R.id.track_detail_image);
        playButton = (Button) findViewById(R.id.play_button);
        shareButton = (Button) findViewById(R.id.share_button);

        trackUri = getIntent().getStringExtra("track_uri");
        trackName.setText(getIntent().getStringExtra("track_name"));
        trackArtist.setText(getIntent().getStringExtra("track_artist"));
        Picasso.get().load(getIntent().getStringExtra("image_resource")).into(trackImage);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playUri(trackUri);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void playUri(String uri) {
        mSpotifyAppRemote.getPlayerApi()
                .play(uri)
                .setResultCallback(empty -> logMessage("Play successful"))
                .setErrorCallback(mErrorCallback);
    }

    private void logMessage(String msg) {
        logMessage(msg, Toast.LENGTH_SHORT);
    }

    private void logMessage(String msg, int duration) {
        Toast.makeText(this, msg, duration).show();
        Log.d(TAG, msg);
    }

    private void logError(Throwable throwable, String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg, throwable);
    }

    private void connect(boolean showAuthView) {

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        SpotifyAppRemote.connect(getApplication(), new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(showAuthView)
                .build(), new Connector.ConnectionListener() {

            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                RemotePlayerActivity.this.onConnected();
            }

            @Override
            public void onFailure(Throwable error) {
                if (error instanceof SpotifyRemoteServiceException) {
                    if (error.getCause() instanceof SecurityException) {
                        logError(error, "SecurityException");
                    }
                    else if (error.getCause() instanceof IllegalStateException) {
                        logError(error, "IllegalStateException");
                    }
                }
                else if (error instanceof NotLoggedInException) {
                    logError(error, "NotLoggedInException");
                }
                else if (error instanceof AuthenticationFailedException) {
                    logError(error, "AuthenticationFailedException");
                }
                else if (error instanceof CouldNotFindSpotifyApp) {
                    logError(error, "CouldNotFindSpotifyApp");
                }
                else if (error instanceof LoggedOutException) {
                    logError(error, "LoggedOutException");
                }
                else if (error instanceof OfflineModeException) {
                    logError(error, "OfflineModeException");
                }
                else if (error instanceof UserNotAuthorizedException) {
                    logError(error, "UserNotAuthorizedException");
                }
                else if (error instanceof UnsupportedFeatureVersionException) {
                    logError(error, "UnsupportedFeatureVersionException");
                }
                else if (error instanceof SpotifyDisconnectedException) {
                    logError(error, "SpotifyDisconnectedException");
                }
                else if (error instanceof SpotifyConnectionTerminatedException) {
                    logError(error, "SpotifyConnectionTerminatedException");
                }
                else {
                    logError(error, String.format("Connection failed: %s", error));
                }
                RemotePlayerActivity.this.onDisconnected();
            }
        });
    }
}
