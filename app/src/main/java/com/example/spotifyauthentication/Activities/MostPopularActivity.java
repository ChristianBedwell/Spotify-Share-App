package com.example.spotifyauthentication.Activities;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.spotifyauthentication.CustomSpinner;
import com.example.spotifyauthentication.Fragments.ItemsFragment;
import com.example.spotifyauthentication.InputFilterMinMax;
import com.example.spotifyauthentication.R;

import java.util.Objects;

public class MostPopularActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private String mAccessToken;

    private EditText limitEditText, offsetEditText;
    private SwipeRefreshLayout swipeContainer;
    private Toolbar toolbar;
    private TextView header;
    private ItemsFragment itemsFragment;

    // string and integer to hold query parameters
    public String type;
    public String timeRange;
    public String limit;
    public String offset;

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

        // add custom toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // retrieve access token from shared preferences and store it
        mAccessToken = getDefaults(TOKEN_KEY, this);

        // create swipe listener for swipe container
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            // get values from limit and offset edit text fields
            limit = limitEditText.getText().toString();
            offset = offsetEditText.getText().toString();

            // create new instance of itemsFragment and fill fragment placeholder
            itemsFragment = newInstance(mAccessToken, type, timeRange,
                    limit, offset);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.items_fragment_placeholder, itemsFragment);
            fragmentTransaction.commit();

            swipeContainer.setRefreshing(false);
        });

        // create array adapter to set items for type spinner
        ArrayAdapter typeItemsAdapter = new ArrayAdapter<>(MostPopularActivity.this,
                R.layout.spinner_item_selected, typeItems);
        typeItemsAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

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
        timeRangeItemsAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

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
        limitEditText.setFilters(new InputFilter[]{ new InputFilterMinMax(1, 50)});
        offsetEditText = (EditText) findViewById(R.id.offsetEditText);
        offsetEditText.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 42)});

        // instantiate header text view
        header = (TextView) findViewById(R.id.most_popular_header);

        // if a configuration change has occurred, restore the fragment placeholder
        if(savedInstanceState != null) {
            itemsFragment = (ItemsFragment) getSupportFragmentManager().findFragmentById(R.id.items_fragment_placeholder);
        }
        // if it is first time onCreate() method is called
        else {
            // instantiate default values for type and time range spinners
            type = "artists";
            timeRange = "";

            // get values from limit and offset edit text fields
            limit = limitEditText.getText().toString();
            offset = offsetEditText.getText().toString();

            // create new instance of itemsFragment and fill fragment placeholder
            itemsFragment = newInstance(mAccessToken, type, timeRange,
                    limitEditText.getText().toString(), offsetEditText.getText().toString());
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.items_fragment_placeholder, itemsFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    // retrieve access token from shared preferences
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    // checks for spinner items selected for each spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.typeSpinner:
                if(parent.getItemAtPosition(position).toString().equals("Artists")) {
                    type = "artists";
                    header.setText(getString(R.string.top_artists_header));
                    break;
                }
                else if(parent.getItemAtPosition(position).toString().equals("Tracks")) {
                    type = "tracks";
                    Log.d(TAG, type + " selected");
                    header.setText(getString(R.string.top_tracks_header));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_most_popular, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // open settings activity
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // pass user parameters to the items fragment
    public static ItemsFragment newInstance(String accessToken, String type, String timeRange, String limit, String offset) {
        ItemsFragment itemsFragment = new ItemsFragment();

        Bundle args = new Bundle();
        args.putString("access_token", accessToken);
        args.putString("type", type);
        args.putString("time_range", timeRange);
        args.putString("limit", limit);
        args.putString("offset", offset);
        itemsFragment.setArguments(args);

        return itemsFragment;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }
}
