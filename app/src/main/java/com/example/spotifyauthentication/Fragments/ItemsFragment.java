package com.example.spotifyauthentication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.spotifyauthentication.Adapters.ArtistAdapter;
import com.example.spotifyauthentication.Adapters.TrackAdapter;
import com.example.spotifyauthentication.Models.Artists.Artist;
import com.example.spotifyauthentication.Models.Artists.Item;
import com.example.spotifyauthentication.Models.Tracks.Track;
import com.example.spotifyauthentication.R;
import com.example.spotifyauthentication.Retrofit.GetDataService;
import com.example.spotifyauthentication.Retrofit.RetrofitInstance;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ItemsFragment extends Fragment {

    private String TAG = ItemsFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    public TrackAdapter trackAdapter;
    public ArtistAdapter artistAdapter;

    private String mAccessToken;
    private String strType;
    private String strTimeRange;
    private String strLimit;
    private String strOffset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_items, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);

        if (getArguments() != null) {
            mAccessToken = getArguments().getString("access_token");
            strType = getArguments().getString("type");
            strTimeRange = getArguments().getString("time_range");
            strLimit = getArguments().getString("limit");
            strOffset = getArguments().getString("offset");
        }

        new ItemsFragment.getData(mAccessToken, strType, strTimeRange, strLimit, strOffset).execute();
        return rootView;
    }

    private class getData extends AsyncTask<Void, Void, Void> {

        // weak references for query parameters
        private WeakReference<String> weakAccessToken;
        private WeakReference<String> weakType;
        private WeakReference<String> weakTimeRange;
        private WeakReference<String> weakLimit;
        private WeakReference<String> weakOffset;

        // tag for debugging logcat entries
        private String TAG = ItemsFragment.class.getSimpleName();

        // getData() constructor
        public getData(String mAccessToken, String type, String timeRange,
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

        // send API request for top artists
        private void requestTopArtists(String mAccessToken, String timeRange,
                                       final Integer limit, Integer offset) {

            // instantiate retrofit instance with base url
            Retrofit retrofit = RetrofitInstance.getRetrofit();
            GetDataService api = retrofit.create(GetDataService.class);

            Call<Artist> call = api.getTopArtists(mAccessToken, timeRange, limit, offset);
            Log.d(TAG, call.toString());

            call.enqueue(new Callback<Artist>() {
                @Override
                public void onResponse(@NonNull Call <Artist> call, @NonNull Response<Artist> response) {
                    if (response.isSuccessful()) {
                        // successful JSON response, create JSON response body and parse details
                        if(response.body() != null) {
                            List<Item> artistItems = response.body().getItems();
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

        // send API request for top tracks
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
        Activity activity = getActivity();
        if(activity != null) {
            artistAdapter = new ArtistAdapter(activity, items);
            layoutManager = new GridLayoutManager(activity,
                    getResources().getInteger(R.integer.grid_column_count));
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(artistAdapter);
            runLayoutAnimation(recyclerView);
        }
    }

    // set up artist recycler view using list of items
    private void setUpTrackRecycler(List<com.example.spotifyauthentication.Models.Tracks.Item> items) {
        Activity activity = getActivity();
        if(activity != null) {
            trackAdapter = new TrackAdapter(activity, items, strLimit);
            layoutManager = new GridLayoutManager(activity,
                    getResources().getInteger(R.integer.grid_column_count));
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(trackAdapter);
            runLayoutAnimation(recyclerView);
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
