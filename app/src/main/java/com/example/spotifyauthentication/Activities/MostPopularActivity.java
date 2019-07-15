package com.example.spotifyauthentication.Activities;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Spinner;

import com.example.spotifyauthentication.CustomSpinner;
import com.example.spotifyauthentication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MostPopularActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private Call mCall;

    private Spinner typeSpinner, timeRangeSpinner;
    private EditText limitEditText, offsetEditText;
    private Button submitButton;

    // string and integer to hold query parameters
    public String type, timeRange, limit, offset = "";

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
                String limitParam = limitEditText.getText().toString();
                String offsetParam = offsetEditText.getText().toString();

                // if limit edit text is not empty, retrieve limit value
                if(!limitParam.equals("")) {
                    limit = "&limit=" + limitParam;
                }
                // else if limit edit text is empty, set limit to empty string
                else {
                    limit = "";
                }

                // if offset edit text is not empty, retrieve offset value
                if(!offsetParam.equals("")) {
                    offset = "&offset=" + offsetParam;
                }
                // else if offset edit text is empty, set offset to empty string
                else {
                    offset = "";
                }

                // use parameters to build JSON request
                buildRequest(type, timeRange, limit, offset);
            }
        });
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public void buildRequest(String type, String time_range, String limit, String offset) {
        HttpUrl endpoint = new HttpUrl.Builder()
                .scheme("https")
                .host("api.spotify.com")
                .addPathSegments("v1")
                .addPathSegment("me")
                .addPathSegment("top")
                .addPathSegment(type)
                .build();

       String url = endpoint.toString() + time_range + limit + offset;
        // build the request object using the http get url and the access token
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();

        Log.d(TAG, request.toString());

        // if there is an existing call, cancel it and create a new one
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // failed to fetch data, throw exception
                Log.e(TAG, "Failed to fetch data: " + e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    // successful JSON reponse, create JSON response body and parse details
                    final JSONObject jsonResponse = new JSONObject(response.body().string());
                    parseJSON(jsonResponse.toString(3));
                }
                catch (JSONException e) {
                    // failed to parse data, throw exception
                    Log.e(TAG, "Failed to parse data: " + e.getMessage());
                }
            }
        });
    }

    // parse JSON string and obtains useful information
    private void parseJSON(String jsonString) {
        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray items = reader.getJSONArray("items");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // retrieve access token from shared preferences
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    // refresh current results displayed to user
    public void refreshResults() {

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
                    timeRange = "?time_range=short_term";
                    Log.d(TAG, timeRange + " selected");
                    break;
                }
                else if(parent.getItemAtPosition(position).toString().equals("Medium Term")) {
                    timeRange = "?time_range=medium_term";
                    Log.d(TAG, timeRange + " selected");
                    break;
                }
                else if(parent.getItemAtPosition(position).toString().equals("Long Term")) {
                    timeRange = "?time_range=long_term";
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
