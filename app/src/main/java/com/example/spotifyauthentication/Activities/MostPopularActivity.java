package com.example.spotifyauthentication.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.spotifyauthentication.Models.Artists.Artist;
import com.example.spotifyauthentication.Models.Tracks.Track;
import com.example.spotifyauthentication.Retrofit.GetDataService;
import com.example.spotifyauthentication.Retrofit.RetrofitInstance;
import com.example.spotifyauthentication.CustomSpinner;
import com.example.spotifyauthentication.R;

import java.lang.ref.WeakReference;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MostPopularActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String mAccessToken;

    private EditText limitEditText, offsetEditText;
    private Button submitButton;

    // string and integer to hold query parameters
    public String type, timeRange;
    public int limit, offset;

    // key for access token
    private final String TOKEN_KEY = "token";

    // define the list of items that will appear in the type and time range spinner
    String[] typeItems = {"Artists", "Tracks"};
    String[] timeRangeItems = {"Medium Term", "Short Term", "Long Term"};

    // tag for debugging logcat entries
    private String TAG = MostPopularActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_popular);

        // set title of action bar from default
        getSupportActionBar().setTitle(String.format(
                Locale.US, "Spotify Analytics", com.spotify.sdk.android.authentication.BuildConfig.VERSION_NAME));

        // create array adapter to set items for type spinner
        ArrayAdapter typeItemsAdapter = new ArrayAdapter<>(MostPopularActivity.this, R.layout.spinner_item_selected, typeItems);
        typeItemsAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // instantiate type spinner and set item selected listener
        final CustomSpinner typeSpinner = (CustomSpinner) findViewById(R.id.typeSpinner);
        typeSpinner.setAdapter(typeItemsAdapter);
        typeSpinner.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            public void onSpinnerOpened(AppCompatSpinner typeSpinner) {
                typeSpinner.setSelected(true);
            }
            public void onSpinnerClosed(AppCompatSpinner typeSpinner) {
                typeSpinner.setSelected(false);
            }
        });
        typeSpinner.setOnItemSelectedListener(this);

        // create array adapter to set items for time range spinner
        ArrayAdapter timeRangeItemsAdapter = new ArrayAdapter<>(MostPopularActivity.this, R.layout.spinner_item_selected, timeRangeItems);
        timeRangeItemsAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // instantiate time range spinner and set item selected listener
        final CustomSpinner timeRangeSpinner = (CustomSpinner) findViewById(R.id.timeRangeSpinner);
        timeRangeSpinner.setAdapter(timeRangeItemsAdapter);
        timeRangeSpinner.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            public void onSpinnerOpened(AppCompatSpinner timeRangeSpinner) {
                timeRangeSpinner.setSelected(true);
            }
            public void onSpinnerClosed(AppCompatSpinner timeRangeSpinner) {
                timeRangeSpinner.setSelected(false);
            }
        });
        timeRangeSpinner.setOnItemSelectedListener(this);

        // instantiate edit text for limit
        limitEditText = (EditText) findViewById(R.id.limitEditText);
        offsetEditText = (EditText) findViewById(R.id.offsetEditText);

        // instantiate button for query submission
        submitButton = (Button) findViewById(R.id.submit_button);

        // retrieve access token from shared preferences and store it
        mAccessToken = getDefaults(TOKEN_KEY, this);

        // create on click listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get parameter strings from edit text fields
                String limitParam = limitEditText.getText().toString();
                String offsetParam = offsetEditText.getText().toString();

                // if limit edit text is not empty, retrieve limit value
                if(!limitParam.equals("")) {
                    limit = Integer.parseInt(limitParam);
                }

                // if offset edit text is not empty, retrieve offset value
                if(!offsetParam.equals("")) {
                    offset = Integer.parseInt(offsetParam);
                }

                // use parameters to build JSON request
                getData data = new getData(timeRange, type, limit, offset, mAccessToken);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private static class getData extends AsyncTask<Void, Void, Void> {

        // weak references for query parameters
        private WeakReference<String> weakTimeRange;
        private WeakReference<String> weakType;
        private WeakReference<Integer> weakLimit;
        private WeakReference<Integer> weakOffset;
        private WeakReference<String> weakAccessToken;

        // tag for debugging logcat entries
        private String TAG = MostPopularActivity.class.getSimpleName();

        getData(String timeRange, String type, int limit, int offset, String mAccessToken) {
            weakTimeRange = new WeakReference<>(timeRange);
            weakType = new WeakReference<>(type);
            weakLimit = new WeakReference<>(limit);
            weakOffset = new WeakReference<>(offset);
            weakAccessToken = new WeakReference<>(mAccessToken);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(weakType.get().equals("artists")) {
                requestTopArtists(weakTimeRange.get(), weakLimit.get(), weakOffset.get());
            }
            else if(weakType.get().equals("tracks")) {
                requestTopTracks(weakTimeRange.get(), weakLimit.get(), weakOffset.get());
            }
            return null;
        }

        private void requestTopArtists(String timeRange, int limit, int offset) {

            // instantiate retrofit instance with base url
            Retrofit retrofit = RetrofitInstance.getRetrofit();
            GetDataService api = retrofit.create(GetDataService.class);

            Call<Artist> call = api.getTopArtists(weakAccessToken.get(), timeRange, limit, offset);
            Log.d(TAG, call.toString());

            call.enqueue(new Callback<Artist>() {
                @Override
                public void onResponse(@NonNull Call <Artist> call, @NonNull Response <Artist> response) {
                    if (response.isSuccessful()) {
                        // successful JSON response, create JSON response body and parse details
                        Artist artist = response.body();
                        Log.d(TAG, response.body().toString());
                    }
                    else {
                        // failed to parse data, throw exception
                        Log.e(TAG, "Failed to parse data!");
                    }
                }
                @Override
                public void onFailure(@NonNull Call <Artist> call, @NonNull Throwable e) {
                    // failed to fetch data, throw exception
                    Log.e(TAG, "Failed to fetch data: " + e.getMessage());
                }
            });
        }

        private void requestTopTracks(String timeRange, int limit, int offset) {

            // instantiate retrofit instance with base url
            Retrofit retrofit = RetrofitInstance.getRetrofit();
            GetDataService api = retrofit.create(GetDataService.class);

            Call<Track> call = api.getTopTracks(weakAccessToken.get(), timeRange, limit, offset);
            Log.d(TAG, call.toString());

            call.enqueue(new Callback<Track>() {
                @Override
                public void onResponse(@NonNull Call <Track> call, @NonNull Response <Track> response) {
                    if (response.isSuccessful()) {
                        // successful JSON response, create JSON response body and parse details
                        Track track = response.body();
                        Log.d(TAG, response.body().toString());
                    }
                    else {
                        // failed to parse data, throw exception
                        Log.e(TAG, "Failed to parse data!");
                    }
                }
                @Override
                public void onFailure(@NonNull Call <Track> call, @NonNull Throwable e) {
                    // failed to fetch data, throw exception
                    Log.e(TAG, "Failed to fetch data: " + e.getMessage());
                }
            });
        }
    }

    // retrieve access token from shared preferences
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    // refresh current results displayed to user
    public void refreshResults() {
        // TODO Auto-generated method stub
    }

    // inflates the app bar menu and adds items to the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_most_popular, menu);
        return true;
    }

    // if refresh app bar icon is clicked, refresh query results
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshResults();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    // checks for spinner items selected for each spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.typeSpinner:
                if(parent.getItemAtPosition(position).toString().equals("Artists")) {
                    type = "artists";
                    Log.d(TAG, type + " selected");
                    break;
                }
                else if(parent.getItemAtPosition(position).toString().equals("Tracks")) {
                    type = "tracks";
                    Log.d(TAG, type + " selected");
                    break;
                }
                break;
            case R.id.timeRangeSpinner:
                if(parent.getItemAtPosition(position).toString().equals("Short Term")) {
                    timeRange = "short_term";
                    Log.d(TAG, timeRange + " selected");
                    break;
                }
                else if(parent.getItemAtPosition(position).toString().equals("Medium Term")) {
                    timeRange = "medium_term";
                    Log.d(TAG, timeRange + " selected");
                    break;
                }
                else if(parent.getItemAtPosition(position).toString().equals("Long Term")) {
                    timeRange = "long_term";
                    Log.d(TAG, timeRange + " selected");
                    break;
                }
                break;
            default:
                // Do nothing
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }
}
