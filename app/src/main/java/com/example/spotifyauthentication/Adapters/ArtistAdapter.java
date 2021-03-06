package com.example.spotifyauthentication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.spotifyauthentication.Activities.ArtistDetailActivity;
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
        int artistItemNum = position + 1;
        Picasso.get().load(artistItems.get(position).getImages().get(0).getUrl()).into(artistViewHolder.artistImage);
        artistViewHolder.artistName.setText(artistItems.get(position).getName());

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);

        int number = artistItems.get(position).getFollowers().getTotal();
        String formatFollowerCount = numberFormat.format(number);

        DecimalFormat precision = new DecimalFormat("0.0");

        artistViewHolder.artistFollowers.setText(new StringBuilder().append(mContext.getString(R.string.follower_count)).append(": ").append(formatFollowerCount));
        artistViewHolder.artistPopularityNumber.setText(String.valueOf(precision.format((float) artistItems.get(position).getPopularity() / 20)));
        artistViewHolder.artistItemNumber.setText(new StringBuilder().append(artistItemNum));
        artistViewHolder.artistPopularity.setRating((float) (artistItems.get(position).getPopularity()) / 20);
    }

    @Override
    public int getItemCount() {
        return artistItems.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView artistImage;
        TextView artistName, artistFollowers, artistPopularityNumber, artistItemNumber;
        RatingBar artistPopularity;

        public ArtistViewHolder(View itemView) {
            super(itemView);

            // initialize the views
            artistImage = (ImageView) itemView.findViewById(R.id.artist_picture);
            artistName = (TextView) itemView.findViewById(R.id.artist_name);
            artistFollowers = (TextView) itemView.findViewById(R.id.artist_followers);
            artistPopularityNumber = (TextView) itemView.findViewById(R.id.artist_popularity_number);
            artistItemNumber = (TextView) itemView.findViewById(R.id.artist_item_number);
            artistPopularity = (RatingBar) itemView.findViewById(R.id.artist_popularity);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(true);

            int number = artistItems.get(getAdapterPosition()).getFollowers().getTotal();
            String formatFollowerCount = numberFormat.format(number);
            String formatFollowerString = (new StringBuilder().
                    append(mContext.getString(R.string.follower_count)).
                    append(": ").append(formatFollowerCount)).toString();

            int artistItemNum = getAdapterPosition() + 1;
            Item artistItem = artistItems.get(getAdapterPosition());
            Intent detailIntent = new Intent(mContext, ArtistDetailActivity.class);

            DecimalFormat precision = new DecimalFormat("0.0");

            detailIntent.putExtra("artist_name", artistItem.getName());
            detailIntent.putExtra("artist_share_name", artistItem.getName());
            detailIntent.putExtra("artist_followers", formatFollowerString);
            detailIntent.putExtra("artist_popularity_number", String.valueOf(precision.format((float) artistItem.getPopularity() / 20)));
            detailIntent.putExtra("artist_item_number", String.valueOf(artistItemNum));
            detailIntent.putExtra("artist_image_resource", artistItem.getImages().get(0).getUrl());
            detailIntent.putExtra("artist_share_link", artistItem.getExternalUrls().getSpotify());
            detailIntent.putExtra("artist_popularity", (float) (artistItem.getPopularity()) / 20);
            mContext.startActivity(detailIntent);
            ((Activity) mContext).overridePendingTransition(R.anim.most_popular_activity_in, R.anim.most_popular_activity_out);
        }
    }
}

