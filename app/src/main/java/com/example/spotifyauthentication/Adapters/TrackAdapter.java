package com.example.spotifyauthentication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.spotifyauthentication.Activities.TrackDetailActivity;
import com.example.spotifyauthentication.Models.Tracks.Item;
import com.example.spotifyauthentication.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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
        int trackItemNum = position + 1;
        DecimalFormat precision = new DecimalFormat("0.0");

        Picasso.get().load(trackItems.get(position).getAlbum().getImages().get(0).getUrl()).into(trackViewHolder.trackImage);
        trackViewHolder.trackArtist.setText(trackItems.get(position).getArtists().get(0).getName());
        trackViewHolder.trackName.setText(trackItems.get(position).getName());
        trackViewHolder.trackYear.setText(trackItems.get(position).getAlbum().getReleaseDate().substring(0,4));
        trackViewHolder.trackPopularityNumber.setText(String.valueOf(precision.format((float) trackItems.get(position).getPopularity() / 20)));
        trackViewHolder.trackItemNumber.setText(new StringBuilder().append(trackItemNum));
        trackViewHolder.trackPopularity.setRating((float) (trackItems.get(position).getPopularity()) / 20);
    }

    @Override
    public int getItemCount() {
        return trackItems.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        ImageView trackImage;
        TextView trackName, trackYear, trackArtist, trackPopularityNumber, trackItemNumber;
        RatingBar trackPopularity;

        public TrackViewHolder(View itemView) {
            super(itemView);

            // initialize the views
            trackImage = (ImageView) itemView.findViewById(R.id.track_picture);
            trackName = (TextView) itemView.findViewById(R.id.track_name);
            trackYear = (TextView) itemView.findViewById(R.id.track_year);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            trackPopularityNumber = (TextView) itemView.findViewById(R.id.track_popularity_number);
            trackItemNumber = (TextView) itemView.findViewById(R.id.track_item_number);
            trackPopularity = (RatingBar) itemView.findViewById(R.id.track_popularity);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int trackItemNum = getAdapterPosition() + 1;
            Item trackItem = trackItems.get(getAdapterPosition());
            Intent detailIntent = new Intent(mContext, TrackDetailActivity.class);
            DecimalFormat precision = new DecimalFormat("0.0");

            detailIntent.putExtra("track_name", trackItem.getName());
            detailIntent.putExtra("track_share_name", trackItem.getName());
            detailIntent.putExtra("track_year", trackItem.getAlbum().getReleaseDate().substring(0,4));
            detailIntent.putExtra("track_artist", trackItem.getArtists().get(0).getName());
            detailIntent.putExtra("track_popularity_number", String.valueOf(precision.format((float) trackItem.getPopularity() / 20)));
            detailIntent.putExtra("track_item_number", String.valueOf(trackItemNum));
            detailIntent.putExtra("track_uri", trackItem.getUri());
            detailIntent.putExtra("track_image_resource", trackItem.getAlbum().getImages().get(0).getUrl());
            detailIntent.putExtra("track_share_link", trackItem.getExternalUrls().getSpotify());
            detailIntent.putExtra("track_popularity", (float) (trackItem.getPopularity()) / 20);
            mContext.startActivity(detailIntent);
        }
    }
}
