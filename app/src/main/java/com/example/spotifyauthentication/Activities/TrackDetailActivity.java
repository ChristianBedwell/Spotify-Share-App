package com.example.spotifyauthentication.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.example.spotifyauthentication.R;
import com.spotify.protocol.types.Track;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class TrackDetailActivity extends AppCompatActivity {

    // declare constants
    public static final String CLIENT_ID = "clientid";
    private static SpotifyAppRemote mSpotifyAppRemote;
    private static final String TAG = TrackDetailActivity.class.getSimpleName();

    private String trackUri, shareLink;
    private TextView trackName, trackYear, trackArtist;
    private ImageView trackImage;
    private Button playButton, shareButton;
    RatingBar trackPopularity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        // set title of action bar from default
        getSupportActionBar().setTitle(String.format(
                Locale.US, "View Track",
                com.spotify.sdk.android.authentication.BuildConfig.VERSION_NAME));

        // initialize the views
        trackName = (TextView) findViewById(R.id.track_detail_name);
        trackYear = (TextView) findViewById(R.id.track_detail_year);
        trackArtist = (TextView) findViewById(R.id.track_detail_artist);
        trackImage = (ImageView) findViewById(R.id.track_detail_image);
        playButton = (Button) findViewById(R.id.play_button);
        shareButton = (Button) findViewById(R.id.share_button);
        trackPopularity = (RatingBar) findViewById(R.id.track_detail_popularity);

        // get intent extras from adapter
        trackUri = getIntent().getStringExtra("track_uri");
        shareLink = getIntent().getStringExtra("track_share_link");
        trackName.setText(getIntent().getStringExtra("track_name"));
        trackYear.setText(getIntent().getStringExtra("track_year"));
        trackArtist.setText(getIntent().getStringExtra("track_artist"));
        trackPopularity.setRating((getIntent().getFloatExtra("track_popularity", 0.0f)));
        Picasso.get().load(getIntent().getStringExtra("track_image_resource")).into(trackImage);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open track with Spotify app remote
                openTrack();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send track via SMS
                shareTrack();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "Back pressed");
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void openTrack() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(getRedirectUri().toString())
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {

            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d(TAG, "Connected! Yay!");

                // App Remote is connected and interactable
                connected();
            }

            public void onFailure(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
            }
        });
    }

    // app remote is connected
    private void connected() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play(trackUri);

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            final Track track = playerState.track;
            if (track != null) {
                Log.d(TAG, track.name + " by " + track.artist.name);
            }
        });
    }

    // get redirect uri using redirect scheme and host
    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }

    // share the track uri with a contact
    public void shareTrack() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, trackName.toString() + " by " + trackArtist.toString());
        share.putExtra(Intent.EXTRA_TEXT, shareLink);

        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
