package com.example.spotifyauthentication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.spotifyauthentication.Models.Artists.Item;
import com.example.spotifyauthentication.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<Item> artistItems;
    private Context mContext;

    public ArtistAdapter(Context context, List<Item> items) {
        this.artistItems = items;
        this.mContext = context;
    }

    @Override
    public ArtistAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.artist_item, parent, false);
        ArtistViewHolder artistViewHolder = new ArtistViewHolder(view);
        return artistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAdapter.ArtistViewHolder artistViewHolder, int position) {
       Picasso.get().load(artistItems.get(position).getImages().get(0).getUrl()).into(artistViewHolder.artistImage);
       artistViewHolder.artistName.setText(artistItems.get(position).getName());

       NumberFormat myFormat = NumberFormat.getInstance();
       myFormat.setGroupingUsed(true);

       int number = artistItems.get(position).getFollowers().getTotal();
       String formatFollowerCount = myFormat.format(number);

       artistViewHolder.artistFollowers.setText(new StringBuilder().append(mContext.getString(R.string.follower_count)).append(": ").append(formatFollowerCount));
       artistViewHolder.artistPopularity.setRating((float) (artistItems.get(position).getPopularity()) / 20);
    }

    @Override
    public int getItemCount() {
        return artistItems.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {

        ImageView artistImage;
        TextView artistName, artistFollowers;
        RatingBar artistPopularity;

        public ArtistViewHolder(View itemView) {
            super(itemView);

            // initialize the views
            artistImage = (ImageView) itemView.findViewById(R.id.artist_picture);
            artistName = (TextView) itemView.findViewById(R.id.artist_name);
            artistFollowers = (TextView) itemView.findViewById(R.id.artist_followers);
            artistPopularity = (RatingBar) itemView.findViewById(R.id.artist_popularity);
        }
    }
}

