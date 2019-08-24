package com.example.spotifyauthentication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "trackPlaybackManager";
    private static final String TABLE_TRACK_PLAYBACK = "track_playback";
    private static final String KEY_TRACK_ITEM_NUMBER = "track_item_number";
    private static final String KEY_TRACK_NAME = "track_name";
    private static final String KEY_TRACK_ARTIST = "track_artist";
    private static final String KEY_TRACK_URI = "track_uri";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACK_PLAYBACK + "("
                + KEY_TRACK_ITEM_NUMBER + " INTEGER PRIMARY KEY," + KEY_TRACK_NAME + " TEXT,"
                + KEY_TRACK_ARTIST + " TEXT,"
                + KEY_TRACK_URI + " TEXT" + ")";
        db.execSQL(CREATE_TRACKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK_PLAYBACK);

        // create tables again
        onCreate(db);
    }

    // code to add a new track
    public void addTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRACK_NAME, track.getTrackName()); // track name
        values.put(KEY_TRACK_ARTIST, track.getTrackArtist()); // track artist
        values.put(KEY_TRACK_ITEM_NUMBER, track.getTrackUri()); // track uri

        // insert row
        db.insert(TABLE_TRACK_PLAYBACK, null, values);
        db.close(); // close database connection
    }

    // code to get a single track
    public Track getTrack(int trackItemNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRACK_PLAYBACK, new String[] { KEY_TRACK_ITEM_NUMBER,
                        KEY_TRACK_NAME, KEY_TRACK_ARTIST, KEY_TRACK_URI }, KEY_TRACK_ITEM_NUMBER + "=?",
                new String[] { String.valueOf(trackItemNumber) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Track track = new Track(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return track
        return track;
    }

    // code to get all tracks in a list view
    public List<Track> getAllTracks() {
        List<Track> trackList = new ArrayList<Track>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRACK_PLAYBACK;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Track track = new Track();
                track.setTrackItemNumber(Integer.parseInt(cursor.getString(0)));
                track.setTrackName(cursor.getString(1));
                track.setTrackArtist(cursor.getString(2));
                track.setTrackUri(cursor.getString(3));
                // add contact to list
                trackList.add(track);
            }
            while (cursor.moveToNext());
        }

        // return contact list
        return trackList;
    }

    // code to update a single track
    public int updateTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRACK_NAME, track.getTrackName());
        values.put(KEY_TRACK_ARTIST, track.getTrackArtist());
        values.put(KEY_TRACK_URI, track.getTrackUri());

        // updating row
        return db.update(TABLE_TRACK_PLAYBACK, values, KEY_TRACK_ITEM_NUMBER + " = ?",
                new String[] { String.valueOf(track.getTrackItemNumber()) });
    }

    // delete single contact
    public void deleteTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRACK_PLAYBACK, KEY_TRACK_ITEM_NUMBER + " = ?",
                new String[] { String.valueOf(track.getTrackItemNumber()) });
        db.close();
    }

    // get tracks count
    public int getTracksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRACK_PLAYBACK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
