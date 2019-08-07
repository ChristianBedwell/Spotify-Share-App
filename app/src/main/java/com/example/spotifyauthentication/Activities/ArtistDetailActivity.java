package com.example.spotifyauthentication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.spotifyauthentication.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ArtistDetailActivity extends AppCompatActivity {

    private String shareLink;
    private TextView artistName, artistFollowers;
    private Button shareButton;
    private ImageView artistImage;
    private RatingBar artistPopularity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        // initialize the views
        artistName = (TextView) findViewById(R.id.artist_detail_name);
        artistFollowers = (TextView) findViewById(R.id.artist_detail_followers);
        shareLink = getIntent().getStringExtra("artist_share_link");
        artistImage = (ImageView) findViewById(R.id.artist_detail_image);
        shareButton = (Button) findViewById(R.id.share_button);
        artistPopularity = (RatingBar) findViewById(R.id.artist_detail_popularity);

        artistName.setText(getIntent().getStringExtra("artist_name"));
        artistFollowers.setText(getIntent().getStringExtra("artist_followers"));
        artistPopularity.setRating((getIntent().getFloatExtra("artist_popularity", 0.0f)));
        Picasso.get().load(getIntent().getStringExtra("artist_image_resource")).into(artistImage);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send track via SMS
                shareTrack();
            }
        });
    }

    public void shareTrack() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, artistName.toString() + " by " + artistName.toString());
        share.putExtra(Intent.EXTRA_TEXT, shareLink);

        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
