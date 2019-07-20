package com.example.spotifyauthentication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotifyauthentication.Activities.TrackDetailActivity;
import com.example.spotifyauthentication.Models.Tracks.Item;
import com.example.spotifyauthentication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private List<Item> trackItems;
    private Context mContext;

    public TrackAdapter(Context context, List<Item> items) {
        this.trackItems = items;
        this.mContext = context;
    }

    @Override
    public TrackAdapter.TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.track_item, parent, false);
        TrackViewHolder trackViewHolder = new TrackViewHolder(view);
        return trackViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackAdapter.TrackViewHolder trackViewHolder, int position) {
        Picasso.get().load(trackItems.get(position).getAlbum().getImages().get(0).getUrl()).into(trackViewHolder.trackImage);
        trackViewHolder.trackArtist.setText(trackItems.get(position).getArtists().get(0).getName());
        trackViewHolder.trackName.setText(trackItems.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trackItems.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        ImageView trackImage;
        TextView trackName, trackArtist;
        Button playButton, shareButton;

        public TrackViewHolder(View itemView) {
            super(itemView);

            // initialize the views
            trackImage = (ImageView) itemView.findViewById(R.id.track_picture);
            trackName = (TextView) itemView.findViewById(R.id.track_name);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            playButton = (Button) itemView.findViewById(R.id.play_button);
            shareButton = (Button) itemView.findViewById(R.id.share_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Item trackItem = trackItems.get(getAdapterPosition());
            Intent detailIntent = new Intent(mContext, TrackDetailActivity.class);
            detailIntent.putExtra("track_name", trackItem.getName());
            detailIntent.putExtra("track_artist", trackItem.getArtists().get(0).getName());
            detailIntent.putExtra("track_uri", trackItem.getUri());
            detailIntent.putExtra("image_resource", trackItem.getAlbum().getImages().get(0).getUrl());
            mContext.startActivity(detailIntent);
        }
    }
}
