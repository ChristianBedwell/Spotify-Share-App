package com.example.spotifyauthentication.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotifyauthentication.R;
import com.squareup.picasso.Picasso;

public class TrackPlaybackFragment extends Fragment {

    private ImageView trackImage;
    private TextView trackItemNumber;
    private TextView trackName;
    private TextView trackArtist;

    private String strTrackImageUri;
    private String strTrackItemNumber;
    private String strTrackName;
    private String strTrackArtist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_track_playback, container, false);
        trackImage = (ImageView) rootView.findViewById(R.id.track_detail_image);
        trackItemNumber = (TextView) rootView.findViewById(R.id.track_detail_item_number);
        trackName = (TextView) rootView.findViewById(R.id.track_detail_name);
        trackArtist = (TextView) rootView.findViewById(R.id.track_detail_artist);

        if (getArguments() != null) {
            strTrackImageUri = getArguments().getString("track_image_uri");
            strTrackItemNumber = getArguments().getString("track_item_number");
            strTrackName = getArguments().getString("track_name");
            strTrackArtist = getArguments().getString("track_artist");
        }

        Picasso.get().load(strTrackImageUri).into(trackImage);
        trackName.setText(strTrackName);
        trackArtist.setText(strTrackArtist);
        trackItemNumber.setText(strTrackItemNumber);

        return rootView;
    }
}
