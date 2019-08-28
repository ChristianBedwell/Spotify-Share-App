package com.example.spotifyauthentication.Database;

public class Track {

    private int trackItemNumber;
    private String trackImageUri;
    private String trackName;
    private String trackArtist;
    private String trackUri;

    public Track () {
        // empty constructor
    }

    public Track(int trackItemNumber, String trackImageUri, String trackName, String trackArtist, String trackUri) {
        this.trackItemNumber = trackItemNumber;
        this.trackImageUri = trackImageUri;
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.trackUri = trackUri;
    }

    public Track(String trackImageUri, String trackName, String trackArtist, String trackUri){
        this.trackImageUri = trackImageUri;
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.trackUri = trackUri;
    }

    public int getTrackItemNumber() {
        return this.trackItemNumber;
    }

    public void setTrackItemNumber(int trackItemNumber) {
        this.trackItemNumber = trackItemNumber;
    }

    public String getTrackImageUri() {
        return this.trackImageUri;
    }

    public void setTrackImageUri(String trackImageUri) {
        this.trackImageUri = trackImageUri;
    }

    public String getTrackName() {
        return this.trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackArtist() {
        return this.trackArtist;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }

    public String getTrackUri() {
        return this.trackUri;
    }

    public void setTrackUri(String trackUri) {
        this.trackUri = trackUri;
    }
}
