package com.example.android.popularmoviesstage1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage1.utilities.ImageUtils;
import com.example.android.popularmoviesstage1.utilities.JSONUtils;

public class DetailActivity extends AppCompatActivity {

    private ImageView mPosterImage;
    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mRating;
    private TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterImage = findViewById(R.id.detail_movie_poster);
        mOriginalTitle = findViewById(R.id.detail_title_value);
        mOverview = findViewById(R.id.detail_overview_value);
        mRating = findViewById(R.id.detail_rating_value);
        mReleaseDate = findViewById(R.id.detail_release_date_value);
        Intent starterIntent = getIntent();
        setTitle(R.string.movie_detail_title);
        if(starterIntent != null)
        {
            setDetailViews(starterIntent);
        }
        else
        {
            Toast.makeText(this, "Oops, something went wrong!", Toast.LENGTH_LONG).show();
        }
    }

    private void setDetailViews(Intent starterIntent)
    {
        String title = starterIntent.getStringExtra(JSONUtils.KEY_ORIGINAL_TITLE);
        String posterPath = starterIntent.getStringExtra(JSONUtils.KEY_POSTER_PATH);
        String overview = starterIntent.getStringExtra(JSONUtils.KEY_OVERVIEW);
        double rating = starterIntent.getDoubleExtra(JSONUtils.KEY_VOTE_AVERAGE, -1);
        String releaseDate = starterIntent.getStringExtra(JSONUtils.KEY_RELEASE_DATE);
        mOriginalTitle.setText(title);
        ImageUtils.insertImageIntoView(mPosterImage, this, posterPath, ImageUtils.PosterSize.BIG);
        mPosterImage.setContentDescription(title);
        mOverview.setText(overview);
        mRating.setText(Double.toString(rating));
        mReleaseDate.setText(releaseDate);
    }

}
