package com.example.spotifyauthentication.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.spotifyauthentication.Database.DatabaseHandler;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.example.spotifyauthentication.R;
import com.spotify.protocol.types.Track;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class TrackDetailActivity extends AppCompatActivity {

    // declare constants
    public static final String CLIENT_ID = "clientid";
    public static SpotifyAppRemote mSpotifyAppRemote;
    private static final String TAG = TrackDetailActivity.class.getSimpleName();

    private String trackUri, shareLink, trackShareName, trackShareArtist;
    private TextView trackName, trackArtist, trackItemNumber;
    private ImageView trackImage;
    private SeekBar trackSeekBar;
    private Button favoriteButton, skipPreviousButton, skipNextButton, repeatButton;
    private ToggleButton playbackButton;
    private Toolbar toolbar;
    private int trackLimit, trackNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        // add custom toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().
                getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        // initialize the views
        trackName = (TextView) findViewById(R.id.track_detail_name);
        trackArtist = (TextView) findViewById(R.id.track_detail_artist);
        trackItemNumber = (TextView) findViewById(R.id.track_detail_item_number);
        trackImage = (ImageView) findViewById(R.id.track_detail_image);
        trackSeekBar = (SeekBar) findViewById(R.id.seekBar);
        favoriteButton = (Button) findViewById(R.id.favorite_button);
        skipPreviousButton = (Button) findViewById(R.id.skip_previous_button);
        skipNextButton = (Button) findViewById(R.id.skip_next_button);
        repeatButton = (Button) findViewById(R.id.repeat_button);
        playbackButton = (ToggleButton) findViewById(R.id.play_button);

        // get intent extras from adapter
        trackLimit = getIntent().getIntExtra("track_limit", 0);
        trackUri = getIntent().getStringExtra("track_uri");
        shareLink = getIntent().getStringExtra("track_share_link");
        trackShareName = getIntent().getStringExtra("track_name");
        trackShareArtist = getIntent().getStringExtra("track_artist");
        trackName.setText(getIntent().getStringExtra("track_name"));
        trackArtist.setText(getIntent().getStringExtra("track_artist"));
        trackItemNumber.setText(getIntent().getStringExtra("track_item_number"));
        trackNumber = Integer.parseInt(getIntent().getStringExtra("track_item_number"));
        Picasso.get().load(getIntent().getStringExtra("track_image_resource")).into(trackImage);

        DatabaseHandler dbHandler = new DatabaseHandler(this);

        /*// if Spotify app is installed on Android device
        if(SpotifyAppRemote.isSpotifyInstalled(getApplicationContext())) {

            // if Spotify app remote object is equal to null
            if(mSpotifyAppRemote != null) {

                mSpotifyAppRemote.getUserApi().getCapabilities().setResultCallback(capabilities -> {
                    // current user is able to play on demand
                    if (capabilities.canPlayOnDemand) {

                        // set initial state of buttons based on track number
                        if(trackNumber == 1) {
                            // if track is first track in list, prevent user from going to previous track
                            skipPreviousButton.setEnabled(false);
                        }
                        else if(trackNumber == trackLimit) {
                            // if track is last track in list, prevent user from going to next track
                            skipNextButton.setEnabled(false);
                        }

                        // set click listener for playback toggle button
                        playbackButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                // if toggle button is enabled, playback is paused
                                if (isChecked) {
                                    mSpotifyAppRemote.getPlayerApi().pause();
                                }
                                // if toggle button is not enabled, playback is started/resumed
                                else {
                                    openTrack();
                                }
                            }
                        });
                        skipPreviousButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        skipNextButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                    // current user is not able to play on demand
                    else {

                    }
                });
            }
        }
        // if Spotify app is not installed on Android device
        else {

        }*/
        // set initial state of buttons based on track number
        if(trackNumber == 1) {
            // if track is first track in list, prevent user from going to previous track
            skipPreviousButton.setEnabled(false);
        }
        else if(trackNumber == trackLimit) {
            // if track is last track in list, prevent user from going to next track
            skipNextButton.setEnabled(false);
        }

        // set click listener for playback toggle button
        playbackButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if toggle button is enabled, playback is paused
                if (isChecked) {
                    mSpotifyAppRemote.getPlayerApi().pause();
                }
                // if toggle button is not enabled, playback is started/resumed
                else {
                    openTrack();
                }
            }
        });
        skipPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        skipNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.detail_activity_in, R.anim.detail_activity_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_most_popular, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        else if(id == R.id.action_settings) {
            // open settings activity
            return true;
        }
        return super.onOptionsItemSelected(item);
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

                // app remote is connected and interactable
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
        share.putExtra(Intent.EXTRA_SUBJECT, trackShareName
                + " by " + trackShareArtist);
        share.putExtra(Intent.EXTRA_TEXT, shareLink);

        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
