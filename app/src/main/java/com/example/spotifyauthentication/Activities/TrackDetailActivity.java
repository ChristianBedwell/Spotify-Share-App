package com.example.spotifyauthentication.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.spotifyauthentication.Database.DatabaseHandler;
import com.example.spotifyauthentication.Database.Track;
import com.example.spotifyauthentication.Fragments.TrackPlaybackFragment;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.example.spotifyauthentication.R;

import java.util.Objects;

import static com.spotify.protocol.types.Repeat.ALL;
import static com.spotify.protocol.types.Repeat.OFF;

public class TrackDetailActivity extends AppCompatActivity {

    // declare constants
    public static final String CLIENT_ID = "clientid";
    public static SpotifyAppRemote mSpotifyAppRemote;
    private static final String TAG = TrackDetailActivity.class.getSimpleName();

    private TrackPlaybackFragment trackPlaybackFragment;

    private String trackUri, shareLink, trackShareName, trackShareArtist;
    private TextView trackSeekBarMin, trackSeekBarMax;
    private SeekBar trackSeekBar;
    private Button skipPreviousButton, skipNextButton;
    private ToggleButton playbackButton, favoriteButton, repeatButton;
    private Toolbar toolbar;
    private int trackLimit, trackNumber, repeatMode;
    private boolean isPlaying, trackIsAdded;

    TrackProgressBar mTrackProgressBar;

    // duration of the track in milliseconds
    public long trackDuration;

    // position of the track
    public long trackPlaybackPosition;

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

        // initialize the text views
        trackSeekBarMin = (TextView) findViewById(R.id.timeSeekBarMin);
        trackSeekBarMax = (TextView) findViewById(R.id.timeSeekBarMax);

        // initialize the seek bar and seek bar class
        trackSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mTrackProgressBar = new TrackProgressBar(trackSeekBar);

        // initialize the buttons
        favoriteButton = (ToggleButton) findViewById(R.id.favorite_button);
        skipPreviousButton = (Button) findViewById(R.id.skip_previous_button);
        skipNextButton = (Button) findViewById(R.id.skip_next_button);
        repeatButton = (ToggleButton) findViewById(R.id.repeat_button);
        playbackButton = (ToggleButton) findViewById(R.id.play_button);

        // get intent extras from adapter
        trackLimit = getIntent().getIntExtra("track_limit", 0);
        trackUri = getIntent().getStringExtra("track_uri");
        shareLink = getIntent().getStringExtra("track_share_link");
        trackShareName = getIntent().getStringExtra("track_name");
        trackShareArtist = getIntent().getStringExtra("track_artist");
        trackNumber = Integer.parseInt(getIntent().getStringExtra("track_item_number"));

        // create new instance of trackPlaybackFragment and fill fragment placeholder
        trackPlaybackFragment = newInstance(getIntent().getStringExtra("track_image_resource"),
                getIntent().getStringExtra("track_item_number"), getIntent().getStringExtra("track_name"),
                getIntent().getStringExtra("track_artist"));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.track_playback_fragment_placeholder, trackPlaybackFragment);
        fragmentTransaction.commit();

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
        // if a configuration change has occurred, restore the position of track playback
        if(savedInstanceState != null) {
            trackDuration = savedInstanceState.getLong("track_duration");
            Log.d(TAG, String.valueOf(trackDuration));
            trackPlaybackPosition = savedInstanceState.getLong("track_position");
            Log.d(TAG, String.valueOf(trackPlaybackPosition));
            mTrackProgressBar.setDuration(trackDuration);
            mTrackProgressBar.update(trackPlaybackPosition);
            isPlaying = true;

            // update seek bar values on UI thread
            trackSeekBarMin.setText(convertSecondsToMSs(trackPlaybackPosition));
            trackSeekBarMax.setText(convertSecondsToMSs(trackDuration - trackPlaybackPosition));
        }
        // if it is first time onCreate() method is called, open track
        else {
            openTrack();
        }

        // set initial state of buttons based on track number
        if(trackNumber == 1) {
            // if track is first track in list, prevent user from going to previous track
            skipPreviousButton.setEnabled(false);
        }
        else if(trackNumber == trackLimit) {
            // if track is last track in list, prevent user from going to next track
            skipNextButton.setEnabled(false);
        }

        // update seek bar progress value every second
        Handler mHandler = new Handler();
        TrackDetailActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if(mSpotifyAppRemote != null && isPlaying){
                    // get track playback position and track duration, convert from ms to s
                    mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
                        trackPlaybackPosition = (playerState.playbackPosition) / 1000;
                        trackDuration = (playerState.track.duration) / 1000;
                        repeatMode = playerState.playbackOptions.repeatMode;
                    });

                    mTrackProgressBar.setDuration(trackDuration);
                    mTrackProgressBar.update(trackPlaybackPosition);

                    // update seek bar values on UI thread
                    trackSeekBarMin.setText(convertSecondsToMSs(trackPlaybackPosition));
                    trackSeekBarMax.setText(convertSecondsToMSs(trackDuration - trackPlaybackPosition));

                    // if track is set not to repeat, toggle repeat button off
                    if(repeatMode == OFF) {
                        repeatButton.setChecked(false);

                        // if current track is close to ending, load next track
                        if(trackDuration - trackPlaybackPosition < 3
                                && trackPlaybackPosition != 0) {
                            loadNextTrack();
                        }
                        // if track is final track and is close to ending, load first track
                        else if(trackDuration - trackPlaybackPosition < 5
                                && trackPlaybackPosition != 0
                                && trackNumber == trackLimit) {
                            loadFirstTrack();
                        }
                    }
                    // else if track is set to repeat, do nothing
                    else if(repeatMode == ALL) {
                        repeatButton.setChecked(true);
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        // set click listener for playback toggle button
        playbackButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if toggle button is enabled, playback is paused
                if (isChecked) {
                    if(mSpotifyAppRemote != null) {
                        mSpotifyAppRemote.getPlayerApi().pause();
                        isPlaying = false;
                    }
                }
                // if toggle button is not enabled, playback is started/resumed
                else {
                    if(mSpotifyAppRemote != null) {
                        mSpotifyAppRemote.getPlayerApi().resume();
                        isPlaying = true;
                    }
                }
            }
        });

        // set click listener for skip previous button
        skipPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPreviousTrack();
            }
        });

        // set click listener for skip next button
        skipNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextTrack();
            }
        });

        // set click listener for repeat toggle button
        repeatButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if repeat button is enabled, track is set to repeat
                if (isChecked) {
                    if (mSpotifyAppRemote != null) {
                        mSpotifyAppRemote.getPlayerApi().setRepeat(ALL);
                    }
                }
                // if favorite button is not enabled, track is set to not repeat
                else {
                    if (mSpotifyAppRemote != null) {
                        mSpotifyAppRemote.getPlayerApi().setRepeat(OFF);
                    }
                }
            }
        });

        // set click listener for favorite toggle button
        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if favorite button is enabled, track is removed from library
                if (isChecked) {
                    if(mSpotifyAppRemote != null) {
                        mSpotifyAppRemote.getUserApi().addToLibrary(trackUri);
                    }
                }
                // if favorite button is not enabled, track is added to library
                else {
                    if(mSpotifyAppRemote != null) {
                        mSpotifyAppRemote.getUserApi().removeFromLibrary(trackUri);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.detail_activity_in, R.anim.detail_activity_out);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("track_duration", trackDuration);
        savedInstanceState.putLong("track_position", trackPlaybackPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            // navigate to parent activity
            onBackPressed();
            return true;
        }
        else if(id == R.id.action_share_track) {
            // share track
            shareTrack();
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

                // app remote is connected and interactable
                connected(trackUri);
            }

            public void onFailure(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
            }
        });
    }

    // app remote is connected
    private void connected(String trackUri) {
        // play a track
        mSpotifyAppRemote.getPlayerApi().play(trackUri);
        Log.d(TAG, trackUri);
        isPlaying = true;

        mSpotifyAppRemote.getUserApi().getLibraryState(trackUri).setResultCallback(libraryState -> {
            trackIsAdded = libraryState.isAdded;
            Log.d(TAG, "Track is added to library: " + trackIsAdded);

            // if track is already added to library, toggle favorite button on
            if(libraryState.isAdded) {
                favoriteButton.setChecked(true);
            }
            // else if track is not added to library, toggle favorite button off
            else {
                favoriteButton.setChecked(false);
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

        // add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, trackShareName
                + " by " + trackShareArtist);
        share.putExtra(Intent.EXTRA_TEXT, shareLink);

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    // load first track in recycler view
    public void loadFirstTrack() {
        trackNumber = 1;

        // create new instance of the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // since track is first track in list, prevent user from going to previous track
        playbackButton.setChecked(false);
        skipPreviousButton.setEnabled(false);
        skipNextButton.setEnabled(true);

        // pull results for the next track
        Track track = dbHandler.getTrack(trackNumber);
        String firstTrackImageUri = track.getTrackImageUri();
        int firstTrackItemNumber = track.getTrackItemNumber();
        String firstTrackName = track.getTrackName();
        String firstTrackArtist = track.getTrackArtist();
        String firstTrackUri = track.getTrackUri();

        // create new instance of trackPlaybackFragment and fill fragment placeholder
        trackPlaybackFragment = newInstance(firstTrackImageUri, String.valueOf(firstTrackItemNumber),
                firstTrackName, firstTrackArtist);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.most_popular_activity_in, R.anim.most_popular_activity_out);
        fragmentTransaction.replace(R.id.track_playback_fragment_placeholder, trackPlaybackFragment);
        fragmentTransaction.commit();

        connected(firstTrackUri);
    }

    // load next track in recycler view
    public void loadNextTrack() {
        // create new instance of the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // increment track number
        if(trackNumber < trackLimit) {
            trackNumber++;

            playbackButton.setChecked(false);
            skipPreviousButton.setEnabled(true);
            skipNextButton.setEnabled(true);

            // reset state of buttons based on track number
            if(trackNumber == 1) {
                // if track is first track in list, prevent user from going to previous track
                skipPreviousButton.setEnabled(false);
                skipNextButton.setEnabled(true);
            }
            else if(trackNumber == trackLimit) {
                // if track is last track in list, prevent user from going to next track
                skipPreviousButton.setEnabled(true);
                skipNextButton.setEnabled(false);
            }
        }

        // pull results for the next track
        Track track = dbHandler.getTrack(trackNumber);
        String nextTrackImageUri = track.getTrackImageUri();
        int nextTrackItemNumber = track.getTrackItemNumber();
        String nextTrackName = track.getTrackName();
        String nextTrackArtist = track.getTrackArtist();
        String nextTrackUri = track.getTrackUri();

        // create new instance of trackPlaybackFragment and fill fragment placeholder
        trackPlaybackFragment = newInstance(nextTrackImageUri, String.valueOf(nextTrackItemNumber),
                nextTrackName, nextTrackArtist);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.most_popular_activity_in, R.anim.most_popular_activity_out);
        fragmentTransaction.replace(R.id.track_playback_fragment_placeholder, trackPlaybackFragment);
        fragmentTransaction.commit();

        connected(nextTrackUri);
    }

    // load previous track in recycler view
    public void loadPreviousTrack() {
        // create new instance of the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // decrement track number and pull results for the corresponding track
        if(trackNumber > 1) {
            trackNumber--;

            playbackButton.setChecked(false);
            skipPreviousButton.setEnabled(true);
            skipNextButton.setEnabled(true);

            // reset state of buttons based on track number
            if(trackNumber == 1) {
                // if track is first track in list, prevent user from going to previous track
                skipPreviousButton.setEnabled(false);
                skipNextButton.setEnabled(true);
            }
            else if(trackNumber == trackLimit) {
                // if track is last track in list, prevent user from going to next track
                skipPreviousButton.setEnabled(true);
                skipNextButton.setEnabled(false);
            }
        }

        // pull results for the next track
        Track track = dbHandler.getTrack(trackNumber);
        String previousTrackImageUri = track.getTrackImageUri();
        int previousTrackItemNumber = track.getTrackItemNumber();
        String previousTrackName = track.getTrackName();
        String previousTrackArtist = track.getTrackArtist();
        String previousTrackUri = track.getTrackUri();

        // create new instance of trackPlaybackFragment and fill fragment placeholder
        trackPlaybackFragment = newInstance(previousTrackImageUri, String.valueOf(previousTrackItemNumber),
                previousTrackName, previousTrackArtist);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.detail_activity_in, R.anim.detail_activity_out);
        fragmentTransaction.replace(R.id.track_playback_fragment_placeholder, trackPlaybackFragment);
        fragmentTransaction.commit();

        connected(previousTrackUri);
    }

    // converts seconds to minutes-seconds format
    public static String convertSecondsToMSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        return String.format("%d:%02d", m,s);
    }

    // pass user parameters to the items fragment
    public static TrackPlaybackFragment newInstance(String trackImageUri, String trackItemNumber, String trackName, String trackArtist) {
        TrackPlaybackFragment trackPlaybackFragment = new TrackPlaybackFragment();

        Bundle args = new Bundle();
        args.putString("track_image_uri", trackImageUri);
        args.putString("track_item_number", trackItemNumber);
        args.putString("track_name", trackName);
        args.putString("track_artist", trackArtist);
        trackPlaybackFragment.setArguments(args);

        return trackPlaybackFragment;
    }

    private class TrackProgressBar {

        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;

        // set change listener for track seek bar
        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

            // notification that the user has started a touch gesture
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // notification that the progress level has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            // notification that the user has finished a touch gesture
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSpotifyAppRemote.getPlayerApi().seekTo(seekBar.getProgress() * 1000);
            }
        };

        private final Runnable mSeekRunnable = new Runnable() {
            @Override
            public void run() {
                int progress = mSeekBar.getProgress();
                mSeekBar.setProgress(progress + LOOP_DURATION);
                mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
            }
        };

        private TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        private void setDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }
    }
}
