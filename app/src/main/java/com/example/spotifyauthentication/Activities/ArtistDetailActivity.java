package com.example.spotifyauthentication.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.spotifyauthentication.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ArtistDetailActivity extends AppCompatActivity {

    private String shareLink, artistShareName;
    private TextView artistName, artistFollowers, artistPopularityNumber;
    private Button shareButton;
    private ImageView artistImage;
    private RatingBar artistPopularity;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().
                getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        // initialize the views
        artistName = (TextView) findViewById(R.id.artist_detail_name);
        artistFollowers = (TextView) findViewById(R.id.artist_detail_followers);
        artistImage = (ImageView) findViewById(R.id.artist_detail_image);
        shareButton = (Button) findViewById(R.id.share_button);
        artistPopularity = (RatingBar) findViewById(R.id.artist_detail_popularity);
        artistPopularityNumber = (TextView) findViewById(R.id.artist_popularity_number);

        shareLink = getIntent().getStringExtra("artist_share_link");
        artistShareName = getIntent().getStringExtra("artist_share_name");
        artistName.setText(getIntent().getStringExtra("artist_name"));
        artistFollowers.setText(getIntent().getStringExtra("artist_followers"));
        artistPopularity.setRating((getIntent().getFloatExtra("artist_popularity", 0.0f)));
        artistPopularityNumber.setText(getIntent().getStringExtra("artist_popularity_number"));
        Picasso.get().load(getIntent().getStringExtra("artist_image_resource")).into(artistImage);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send track via SMS
                shareTrack();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void shareTrack() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, artistShareName);
        share.putExtra(Intent.EXTRA_TEXT, shareLink);

        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
