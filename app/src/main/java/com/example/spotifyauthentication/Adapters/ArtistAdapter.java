package com.example.spotifyauthentication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.spotifyauthentication.Models.Artists.Item;
import com.example.spotifyauthentication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private LayoutInflater inflater;
    private List<Item> artistItems;

    public ArtistAdapter(Context context, List<Item> items) {
        inflater = LayoutInflater.from(context);
        artistItems = items;
    }

    @Override
    public ArtistAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.artist_item, parent, false);
        ArtistViewHolder artistViewHolder = new ArtistViewHolder(view);
        return artistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAdapter.ArtistViewHolder artistViewHolder, int position) {
       Picasso.get().load(artistItems.get(position).getImages().get(0).getUrl()).into(artistViewHolder.artistImage);
    }

    @Override
    public int getItemCount() {
        return artistItems.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {

        ImageView artistImage;

        public ArtistViewHolder(View itemView) {
            super(itemView);
        }
    }
}

