package com.example.spotifyauthentication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.spotifyauthentication.Database.DatabaseHandler;
import com.example.spotifyauthentication.Database.Track;
import com.example.spotifyauthentication.Fragments.TrackPlaybackFragment;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.example.spotifyauthentication.R;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.PlayerState;

import java.util.Objects;
import java.util.Random;

import static com.spotify.protocol.types.Repeat.ALL;
import static com.spotify.protocol.types.Repeat.OFF;

public class TrackDetailActivity extends AppCompatActivity {

    // declare constants
    public static final String CLIENT_ID = "cd58168e38d84c43b7433d471d1c5942";
    public static SpotifyAppRemote mSpotifyAppRemote;
    private static final String TAG = TrackDetailActivity.class.getSimpleName();

    private TrackPlaybackFragment trackPlaybackFragment;

    private String trackUri, shareLink, trackShareName, trackShareArtist;
    private TextView trackSeekBarMin, trackSeekBarMax;
    private SeekBar trackSeekBar;
    private Button skipPreviousButton, skipNextButton;
    private ToggleButton shuffleButton, repeatButton;
    private AppCompatImageButton mPlayPauseButton;
    private Toolbar toolbar;
    private int trackLimit, trackNumber, trackRepeatMode;
    private boolean trackIsPaused, shuffleEnabled, onDemandPlaybackAllowed;

    TrackProgressBar mTrackProgressBar;
    Subscription<PlayerState> mPlayerStateSubscription;
    Subscription<Capabilities> mCapabilitiesSubscription;

    // duration of the track in milliseconds
    public long trackDuration;

    // position of the track
    public long trackPlaybackPosition;

    @SuppressLint("SetTextI18n")
    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback = new Subscription.EventCallback<PlayerState>() {
        @Override
        public void onEvent(PlayerState playerState) {
            trackPlaybackPosition = (playerState.playbackPosition) / 1000;
            trackDuration = (playerState.track.duration) / 1000;
            trackRepeatMode = playerState.playbackOptions.repeatMode;
            trackIsPaused = playerState.isPaused;

            // if track is paused, set background of button to play
            if (trackIsPaused) {
                mPlayPauseButton.setBackgroundResource(R.drawable.ic_play_circle_black);
            }
            // else if track is playing, set background of button to pause
            else {
                mPlayPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_black);
            }

            // if track is set not to repeat, toggle repeat button off
            if(trackRepeatMode == OFF) {
                repeatButton.setChecked(false);

                // if current track is close to ending, load next track
                if(trackDuration - trackPlaybackPosition < 3
                        && trackPlaybackPosition != 0) {
                    // if shuffle is toggled, play a random track
                    if(shuffleEnabled) {
                        playRandomTrack();
                    }
                    // else if shuffle is not toggled, play the next track
                    else {
                        playNextTrack();
                    }
                }
                // else if track is the final track and is close to ending, load first track
                else if(trackDuration - trackPlaybackPosition < 5
                        && trackPlaybackPosition != 0
                        && trackNumber == trackLimit) {
                    // if shuffle is toggled, play a random track
                    if(shuffleEnabled) {
                        playRandomTrack();
                    }
                    // else if shuffle is not toggled, play the next track
                    else {
                        playFirstTrack();
                    }
                }
            }
            // else if track is set to repeat, do nothing
            else if(trackRepeatMode == ALL) {
                repeatButton.setChecked(true);
            }

            // update seek bar duration and position
            mTrackProgressBar.setDuration(trackDuration);
            mTrackProgressBar.update(trackPlaybackPosition);

            // update seek bar text view values
            trackSeekBarMin.setText(convertSecondsToMSs(trackPlaybackPosition));
            trackSeekBarMax.setText(convertSecondsToMSs(trackDuration - trackPlaybackPosition));
        }
    };

    @SuppressLint("SetTextI18n")
    private final Subscription.EventCallback<Capabilities> mCapabilitiesEventCallback = new Subscription.EventCallback<Capabilities>() {
        @Override
        public void onEvent(Capabilities capabilities) {
            onDemandPlaybackAllowed = capabilities.canPlayOnDemand;
        }
    };

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
        shuffleButton = (ToggleButton) findViewById(R.id.shuffle_button);
        skipPreviousButton = (Button) findViewById(R.id.skip_previous_button);
        skipNextButton = (Button) findViewById(R.id.skip_next_button);
        repeatButton = (ToggleButton) findViewById(R.id.repeat_button);
        mPlayPauseButton = findViewById(R.id.play_pause_button);

        // get intent extras from adapter
        trackLimit = getIntent().getIntExtra("track_limit", 0);
        trackUri = getIntent().getStringExtra("track_uri");
        shareLink = getIntent().getStringExtra("track_share_link");
        trackShareName = getIntent().getStringExtra("track_name");
        trackShareArtist = getIntent().getStringExtra("track_artist");
        trackNumber = Integer.parseInt(getIntent().getStringExtra("track_item_number"));

        // if Spotify the application is installed on Android device
        if(SpotifyAppRemote.isSpotifyInstalled(getApplicationContext())) {
            // connect the client to the Spotify app remote and subscribe to user capabilities
            connectAppRemote();

            // current user is able to play on demand
            if (onDemandPlaybackAllowed) {
                Log.d(TAG, "User can play on demand.");
                // create new instance of trackPlaybackFragment and fill fragment placeholder
                trackPlaybackFragment = newInstance(getIntent().getStringExtra("track_image_resource"),
                        getIntent().getStringExtra("track_item_number"), getIntent().getStringExtra("track_name"),
                        getIntent().getStringExtra("track_artist"));
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.track_playback_fragment_placeholder, trackPlaybackFragment);
                fragmentTransaction.commit();

                // if a configuration change has occurred, restore the position of track playback
                if(savedInstanceState != null) {
                    trackDuration = savedInstanceState.getLong("track_duration");
                    Log.d(TAG, String.valueOf(trackDuration));
                    trackPlaybackPosition = savedInstanceState.getLong("track_position");
                    Log.d(TAG, String.valueOf(trackPlaybackPosition));
                    mTrackProgressBar.setDuration(trackDuration);
                    mTrackProgressBar.update(trackPlaybackPosition);

                    // update seek bar values on UI thread
                    trackSeekBarMin.setText(convertSecondsToMSs(trackPlaybackPosition));
                    trackSeekBarMax.setText(convertSecondsToMSs(trackDuration - trackPlaybackPosition));
                }
                // if it is first time onCreate() method is called, play track from beginning
                else {
                    playTrack(trackUri);
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
                        if(mSpotifyAppRemote != null){
                            subscribeToPlayerState();
                        }
                        mHandler.postDelayed(this, 1000);
                    }
                });

                // set click listener for skip previous button
                skipPreviousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPreviousTrack();
                    }
                });

                // set click listener for skip next button
                skipNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playNextTrack();
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
                shuffleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // if shuffle button is enabled, track playback is shuffled
                        if (isChecked) {
                            if(mSpotifyAppRemote != null) {
                                shuffleEnabled = true;
                            }
                        }
                        // if shuffle button is not enabled, track playback is not shuffled
                        else {
                            if(mSpotifyAppRemote != null) {
                                shuffleEnabled = false;
                            }
                        }
                    }
                });
            }
            // current user is not able to play on demand
            else {
                Log.d(TAG, "User cannot play on demand.");
                // hide UI from user
                trackSeekBar.setVisibility(View.INVISIBLE);
                trackSeekBarMin.setVisibility(View.INVISIBLE);
                trackSeekBarMax.setVisibility(View.INVISIBLE);
                shuffleButton.setVisibility(View.INVISIBLE);
                repeatButton.setVisibility(View.INVISIBLE);
                skipNextButton.setVisibility(View.INVISIBLE);
                skipPreviousButton.setVisibility(View.INVISIBLE);
                mPlayPauseButton.setVisibility(View.INVISIBLE);

                // display toast message to user
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Your device and/or membership does not support on demand playback.",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
        // else if Spotify app is not installed on Android device
        else {
            // hide UI from user
            trackSeekBar.setVisibility(View.INVISIBLE);
            trackSeekBarMin.setVisibility(View.INVISIBLE);
            trackSeekBarMax.setVisibility(View.INVISIBLE);
            shuffleButton.setVisibility(View.INVISIBLE);
            repeatButton.setVisibility(View.INVISIBLE);
            skipNextButton.setVisibility(View.INVISIBLE);
            skipPreviousButton.setVisibility(View.INVISIBLE);
            mPlayPauseButton.setVisibility(View.INVISIBLE);

            // display toast message to user
            Toast toast = Toast.makeText(getApplicationContext(),
                    "The Spotify application is not installed on your device.",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void onPlayPauseButtonClicked(View view) {
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            if (playerState.isPaused) {
                mSpotifyAppRemote.getPlayerApi().resume();
            }
            else {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.detail_activity_in, R.anim.detail_activity_out);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSpotifyAppRemote.getPlayerApi().pause();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    // save the track duration and position on orientation change
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("track_duration", trackDuration);
        savedInstanceState.putLong("track_position", trackPlaybackPosition);
    }

    // adds items to the action bar if they are present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void connectAppRemote() {

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(getRedirectUri().toString())
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {

            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                subscribeToCapabilities();
            }

            public void onFailure(Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
            }
        });
    }

    // app remote is connected
    private void playTrack(String trackUri) {
        // play a track from the beginning
        mSpotifyAppRemote.getPlayerApi().play(trackUri);
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
    public void playFirstTrack() {
        trackNumber = 1;

        // create new instance of the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // since track is first track in list, prevent user from going to previous track
        mPlayPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_black);
        skipPreviousButton.setEnabled(false);
        skipNextButton.setEnabled(true);

        // pull results for the first track
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

        playTrack(firstTrackUri);
    }

    // load next track in recycler view
    public void playNextTrack() {
        // create new instance of the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // increment track number
        if(trackNumber < trackLimit) {
            trackNumber++;

            mPlayPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_black);
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

        playTrack(nextTrackUri);
    }

    // load previous track in recycler view
    public void playPreviousTrack() {
        // create new instance of the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // decrement track number and pull results for the corresponding track
        if(trackNumber > 1) {
            trackNumber--;

            mPlayPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_black);
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

        // pull results for the previous track
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

        playTrack(previousTrackUri);
    }

    // load random track in recycler view
    public void playRandomTrack() {
        // create new instance of the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // create random integer between 1 and limit
        Random random = new Random();
        trackNumber = random.nextInt(trackLimit) + 1;

        mPlayPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_black);
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

        // pull results for the next track
        Track track = dbHandler.getTrack(trackNumber);
        String randomTrackImageUri = track.getTrackImageUri();
        int randomTrackItemNumber = track.getTrackItemNumber();
        String randomTrackName = track.getTrackName();
        String randomTrackArtist = track.getTrackArtist();
        String randomTrackUri = track.getTrackUri();

        // create new instance of trackPlaybackFragment and fill fragment placeholder
        trackPlaybackFragment = newInstance(randomTrackImageUri, String.valueOf(randomTrackItemNumber),
                randomTrackName, randomTrackArtist);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.most_popular_activity_in, R.anim.most_popular_activity_out);
        fragmentTransaction.replace(R.id.track_playback_fragment_placeholder, trackPlaybackFragment);
        fragmentTransaction.commit();

        playTrack(randomTrackUri);
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

    // subscribe to Spotify player state
    public void subscribeToPlayerState() {

        if (mPlayerStateSubscription != null && !mPlayerStateSubscription.isCanceled()) {
            mPlayerStateSubscription.cancel();
            mPlayerStateSubscription = null;
        }

        mPlayerStateSubscription = (Subscription<PlayerState>) mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(mPlayerStateEventCallback)
                .setLifecycleCallback(new Subscription.LifecycleCallback() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "Event: start");
                    }

                    @Override
                    public void onStop() {
                        Log.d(TAG, "Event: end");
                    }
                });
    }

    // subscribe to Spotify player capabilities
    public void subscribeToCapabilities() {

        if (mCapabilitiesSubscription != null && !mCapabilitiesSubscription.isCanceled()) {
            mCapabilitiesSubscription.cancel();
            mCapabilitiesSubscription = null;
        }

        mCapabilitiesSubscription = (Subscription<Capabilities>) mSpotifyAppRemote.getUserApi()
                .subscribeToCapabilities()
                .setEventCallback(mCapabilitiesEventCallback)
                .setLifecycleCallback(new Subscription.LifecycleCallback() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "Event: start");
                    }

                    @Override
                    public void onStop() {
                        Log.d(TAG, "Event: end");
                    }
                });
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
