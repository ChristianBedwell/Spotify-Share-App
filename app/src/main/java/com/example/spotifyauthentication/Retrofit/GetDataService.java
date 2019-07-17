package com.example.spotifyauthentication.Retrofit;

import com.example.spotifyauthentication.Models.Artists.Artist;
import com.example.spotifyauthentication.Models.Tracks.Track;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("artists")
    Call<Artist> getTopArtists(
            @Header("Authorization") String accessToken,
            @Query("time_range") String timeRange,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("tracks")
    Call<Track> getTopTracks(
            @Header("Authorization") String accessToken,
            @Query("time_range") String timeRange,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}