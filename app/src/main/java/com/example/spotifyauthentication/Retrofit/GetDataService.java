package com.example.spotifyauthentication.Retrofit;

import com.example.spotifyauthentication.Models.Artists.ArtistDetails;
import com.example.spotifyauthentication.Models.Tracks.TrackDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("artists")
    Call<ArtistDetails> getTopArtists(
            @Query("time_range") String timeRange,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("tracks")
    Call<TrackDetails> getTopTracks(
            @Query("time_range") String timeRange,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}