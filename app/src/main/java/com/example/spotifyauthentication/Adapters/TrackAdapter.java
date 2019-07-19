package com.example.spotifyauthentication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotifyauthentication.Models.Tracks.Item;
import com.example.spotifyauthentication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private LayoutInflater inflater;
    private List<Item> trackItems;

    public TrackAdapter(Context context, List<Item> items) {
        inflater = LayoutInflater.from(context);
        trackItems = items;
    }

    @Override
    public TrackAdapter.TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.track_item, parent, false);
        TrackViewHolder trackViewHolder = new TrackViewHolder(view);
        return trackViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackAdapter.TrackViewHolder trackViewHolder, int position) {
        Picasso.get().load(trackItems.get(position).getAlbum().getImages().get(0).getUrl()).into(trackViewHolder.trackImage);
        trackViewHolder.trackArtist.setText(trackItems.get(position).getAlbum().getName());
        trackViewHolder.trackName.setText(trackItems.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trackItems.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {

        ImageView trackImage;
        TextView trackName, trackArtist;

        public TrackViewHolder(View itemView) {
            super(itemView);

            trackImage = (ImageView) itemView.findViewById(R.id.track_picture);
            trackName = (TextView) itemView.findViewById(R.id.track_name);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
        }
    }
}
