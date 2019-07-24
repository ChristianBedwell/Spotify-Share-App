package com.example.spotifyauthentication.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.spotifyauthentication.Adapters.ArtistAdapter;
import com.example.spotifyauthentication.Adapters.TrackAdapter;
import com.example.spotifyauthentication.Models.Artists.Artist;
import com.example.spotifyauthentication.Models.Tracks.Track;
import com.example.spotifyauthentication.Retrofit.GetDataService;
import com.example.spotifyauthentication.Retrofit.RetrofitInstance;
import com.example.spotifyauthentication.CustomSpinner;
import com.example.spotifyauthentication.R;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MostPopularActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private String mAccessToken;

    private EditText limitEditText, offsetEditText;
    private Button submitButton;

    private TrackAdapter trackAdapter;
    private ArtistAdapter artistAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeContainer;

    // string and integer to hold query parameters
    public String type, timeRange;

    // key for access token
    private final String TOKEN_KEY = "token";

    // define the list of items that will appear in the type and time range spinner
    String[] typeItems = {"Artists", "Tracks"};
    String[] timeRangeItems = {"None", "Short Term", "Medium Term", "Long Term"};

    // tag for debugging logcat entries
    private String TAG = MostPopularActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_popular);

        // set title of action bar from default
        getSupportActionBar().setTitle(String.format(
                Locale.US, "Top Artists and Tracks",
                com.spotify.sdk.android.authentication.BuildConfig.VERSION_NAME));

        recyclerView = findViewById(R.id.card_recycler_view);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // get parameter strings from edit text fields
                String strLimit = limitEditText.getText().toString();
                String strOffset = offsetEditText.getText().toString();

                // use parameters to build JSON request
                new getData(mAccessToken, type, timeRange, strLimit, strOffset).execute();
                swipeContainer.setRefreshing(false);
            }
        });

        // create array adapter to set items for type spinner
        ArrayAdapter typeItemsAdapter = new ArrayAdapter<>(MostPopularActivity.this,
                R.layout.spinner_item_selected, typeItems);
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
        ArrayAdapter timeRangeItemsAdapter = new ArrayAdapter<>(MostPopularActivity.this,
                R.layout.spinner_item_selected, timeRangeItems);
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
                String strLimit = limitEditText.getText().toString();
                String strOffset = offsetEditText.getText().toString();

                // use parameters to build JSON request
                new getData(mAccessToken, type, timeRange, strLimit, strOffset).execute();
                submitButton.setVisibility(View.INVISIBLE);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private class getData extends AsyncTask<Void, Void, Void> {

        // weak references for query parameters
        private WeakReference<String> weakAccessToken;
        private WeakReference<String> weakType;
        private WeakReference<String> weakTimeRange;
        private WeakReference<String> weakLimit;
        private WeakReference<String> weakOffset;

        // tag for debugging logcat entries
        private String TAG = MostPopularActivity.class.getSimpleName();

        // getData() constructor
        getData(String mAccessToken, String type, String timeRange,
                String strLimit, String strOffset) {

            weakAccessToken = new WeakReference<>(mAccessToken);
            weakType = new WeakReference<>(type);
            weakTimeRange = new WeakReference<>(timeRange);
            weakLimit = new WeakReference<>(strLimit);
            weakOffset = new WeakReference<>(strOffset);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // top artists has been selected
            if(weakType.get().equals("artists")) {
                // time range parameter is empty
                if(weakTimeRange.get().equals("")) {
                    // limit parameter is empty
                    if(weakLimit.get().equals("")) {
                        // offset parameter is empty
                        if(weakOffset.get().equals("")) {
                            // all query parameters are empty
                            requestTopArtists("Bearer " + weakAccessToken.get(),
                                    null, null, null);
                        }
                        // offset is not empty
		                else {
                            // all query parameters are empty except offset
                            requestTopArtists("Bearer " + weakAccessToken.get(),
                                    null, null, Integer.parseInt(weakOffset.get()));
                        }
                    }
                    // limit parameter is not empty
	                else {
                        // offset parameter is empty
                        if(weakOffset.get().equals("")) {
                            // all parameters are empty except limit
                            requestTopArtists("Bearer " + weakAccessToken.get(),
                                    null, Integer.parseInt(weakLimit.get()),
                                    null);
                        }
                        // offset parameter is not empty
		                else {
                            // all parameters are empty except limit and offset
                            requestTopArtists("Bearer " + weakAccessToken.get(),
                                    null, Integer.parseInt(weakLimit.get()),
                                    Integer.parseInt(weakOffset.get()));
                        }
                    }
                }
                // time range parameter is not empty
                else {
                    // limit parameter is empty
                    if(weakLimit.get().equals("")) {
                        // offset parameter is empty
                        if(weakOffset.get().equals("")) {
                            // all parameters are empty except time range
                            requestTopArtists("Bearer " + weakAccessToken.get(),
                                    weakTimeRange.get(), null, null);
                        }
                        // offset parameter is not empty
		                else {
                            // all parameters are empty except time range and offset
                            requestTopArtists("Bearer " + weakAccessToken.get(),
                                    weakTimeRange.get(), null,
                                    Integer.parseInt(weakOffset.get()));
                        }
                    }
                    // limit parameter is not empty
	                else {
                        // offset parameter is empty
                        if(weakOffset.get().equals("")) {
                            // all fields are empty except time range and limit
                            requestTopArtists("Bearer " + weakAccessToken.get(),
                                    weakTimeRange.get(), Integer.parseInt(weakLimit.get()),
                                    null);
                        }
                        // offset parameter is not empty
		                else {
                            // all parameters are full
                            requestTopArtists("Bearer " + weakAccessToken.get(),
                                    weakTimeRange.get(), Integer.parseInt(weakLimit.get()),
                                    Integer.parseInt(weakOffset.get()));
                        }
                    }
                }
            }
            // top tracks has been selected
            else if(weakType.get().equals("tracks")) {
                // time range parameter is empty
                if(weakTimeRange.get().equals("")) {
                    // limit parameter is empty
                    if(weakLimit.get().equals("")) {
                        // offset parameter is empty
                        if(weakOffset.get().equals("")) {
                            // all query parameters are empty
                            requestTopTracks("Bearer " + weakAccessToken.get(),
                                    null, null, null);
                        }
                        // offset is not empty
                        else {
                            // all query parameters are empty except offset
                            requestTopTracks("Bearer " + weakAccessToken.get(),
                                    null, null, Integer.parseInt(weakOffset.get()));
                        }
                    }
                    // limit parameter is not empty
                    else {
                        // offset parameter is empty
                        if(weakOffset.get().equals("")) {
                            // all parameters are empty except limit
                            requestTopTracks("Bearer " + weakAccessToken.get(),
                                    null, Integer.parseInt(weakLimit.get()),
                                    null);
                        }
                        // offset parameter is not empty
                        else {
                            // all parameters are empty except limit and offset
                            requestTopTracks("Bearer " + weakAccessToken.get(),
                                    null, Integer.parseInt(weakLimit.get()),
                                    Integer.parseInt(weakOffset.get()));
                        }
                    }
                }
                // time range parameter is not empty
                else {
                    // limit parameter is empty
                    if(weakLimit.get().equals("")) {
                        // offset parameter is empty
                        if(weakOffset.get().equals("")) {
                            // all parameters are empty except time range
                            requestTopTracks("Bearer " + weakAccessToken.get(),
                                    weakTimeRange.get(), null, null);
                        }
                        // offset parameter is not empty
                        else {
                            // all parameters are empty except time range and offset
                            requestTopTracks("Bearer " + weakAccessToken.get(),
                                    weakTimeRange.get(), null,
                                    Integer.parseInt(weakOffset.get()));
                        }
                    }
                    // limit parameter is not empty
                    else {
                        // offset parameter is empty
                        if(weakOffset.get().equals("")) {
                            // all fields are empty except time range and limit
                            requestTopTracks("Bearer " + weakAccessToken.get(),
                                    weakTimeRange.get(), Integer.parseInt(weakLimit.get()),
                                    null);
                        }
                        // offset parameter is not empty
                        else {
                            // all parameters are full
                            requestTopTracks("Bearer " + weakAccessToken.get(),
                                    weakTimeRange.get(), Integer.parseInt(weakLimit.get()),
                                    Integer.parseInt(weakOffset.get()));
                        }
                    }
                }
            }
            return null;
        }

        private void requestTopArtists(String mAccessToken, String timeRange,
                                       final Integer limit, Integer offset) {

            // instantiate retrofit instance with base url
            Retrofit retrofit = RetrofitInstance.getRetrofit();
            GetDataService api = retrofit.create(GetDataService.class);

            Call<Artist> call = api.getTopArtists(mAccessToken, timeRange, limit, offset);
            Log.d(TAG, call.toString());

            call.enqueue(new Callback<Artist>() {
                @Override
                public void onResponse(@NonNull Call <Artist> call, @NonNull Response <Artist> response) {
                    if (response.isSuccessful()) {
                        // successful JSON response, create JSON response body and parse details
                        if(response.body() != null) {
                            List<com.example.spotifyauthentication.Models.Artists.Item> artistItems = response.body().getItems();
                            setUpArtistRecycler(artistItems);
                        }
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

        private void requestTopTracks(String mAccessToken, String timeRange, Integer limit, Integer offset) {

            // instantiate retrofit instance with base url
            Retrofit retrofit = RetrofitInstance.getRetrofit();
            GetDataService api = retrofit.create(GetDataService.class);

            Call<Track> call = api.getTopTracks(mAccessToken, timeRange, limit, offset);
            Log.d(TAG, call.toString());

            call.enqueue(new Callback<Track>() {
                @Override
                public void onResponse(@NonNull Call <Track> call, @NonNull Response <Track> response) {
                    // successful JSON response, create JSON response body and parse details
                    if (response.isSuccessful()) {
                        if(response.body() != null) {
                            List<com.example.spotifyauthentication.Models.Tracks.Item> trackItems = response.body().getItems();
                            setUpTrackRecycler(trackItems);
                        }
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

    // set up artist recycler view using list of items
    private void setUpArtistRecycler(List<com.example.spotifyauthentication.Models.Artists.Item> items) {
        artistAdapter = new ArtistAdapter(MostPopularActivity.this, items);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(artistAdapter);
    }

    // set up artist recycler view using list of items
    private void setUpTrackRecycler(List<com.example.spotifyauthentication.Models.Tracks.Item> items) {
        trackAdapter = new TrackAdapter(MostPopularActivity.this, items);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(trackAdapter);
    }

    // retrieve access token from shared preferences
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    // refresh current results displayed to user
    public void refreshResults() {
        String strLimit = limitEditText.getText().toString();
        String strOffset = offsetEditText.getText().toString();

        // use parameters to build JSON request
        new getData(mAccessToken, type, timeRange, strLimit, strOffset).execute();
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
                if(parent.getItemAtPosition(position).toString().equals("None")) {
                    timeRange = "";
                    Log.d(TAG, "spinner item not selected");
                    break;
                }
                else if(parent.getItemAtPosition(position).toString().equals("Short Term")) {
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
