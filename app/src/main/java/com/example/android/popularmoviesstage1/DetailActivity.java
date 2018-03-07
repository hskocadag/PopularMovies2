package com.example.android.popularmoviesstage1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage1.data.MovieContract;
import com.example.android.popularmoviesstage1.model.Movie;
import com.example.android.popularmoviesstage1.model.MovieImagesRequestResult;
import com.example.android.popularmoviesstage1.model.MovieRequestResult;
import com.example.android.popularmoviesstage1.model.MovieReview;
import com.example.android.popularmoviesstage1.model.MovieReviewsRequestResult;
import com.example.android.popularmoviesstage1.model.MovieVideosRequestResult;
import com.example.android.popularmoviesstage1.utilities.ImageUtils;
import com.example.android.popularmoviesstage1.utilities.NetworkUtils;
import com.example.android.popularmoviesstage1.utilities.PropertyUtils;
import com.example.android.popularmoviesstage1.utilities.retrofitQueries.APIClient;
import com.example.android.popularmoviesstage1.utilities.retrofitQueries.APIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private long mId;
    private ImageView mPosterImage;
    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mRating;
    private TextView mReleaseDate;
    private APIInterface apiInterface;
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;
    private List<MovieReview> mMovieReviews;

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

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView = findViewById(R.id.rv_reviews);
        mReviewRecyclerView.setLayoutManager(layoutManager);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        if(starterIntent != null)
        {
            apiInterface = APIClient.getClient().create(APIInterface.class);
            setDetailViews(starterIntent);
            handleComments(mId);
        }
        else
        {
            Toast.makeText(this, "Oops, something went wrong!", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fabFavMovie = (FloatingActionButton) findViewById(R.id.fab_fav_movie);

        fabFavMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO favorite the movie
            }
        });

        FloatingActionButton fabTrailer = (FloatingActionButton) findViewById(R.id.fab_watch_trailer);

        fabTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTrailers(mId);
            }
        });
    }

    private void handleComments(long movieId)
    {
        if(movieId < 0)
            return;
        Call<MovieReviewsRequestResult> call = apiInterface.getMovieReviews(movieId, getApiKey());

        call.enqueue(new Callback<MovieReviewsRequestResult>() {
            @Override
            public void onResponse(Call<MovieReviewsRequestResult> call, Response<MovieReviewsRequestResult> response) {
                int statusCode = response.code();
                MovieReviewsRequestResult movieReviewsRequestResult = response.body();
                if (movieReviewsRequestResult == null)
                    return;
                if(movieReviewsRequestResult.getResults() != null && movieReviewsRequestResult.getResults().size() > 0)
                    mReviewAdapter.updateReviewList(movieReviewsRequestResult.getResults());
            }

            @Override
            public void onFailure(Call<MovieReviewsRequestResult> call, Throwable t) {

            }
        });
    }

    private void getTrailers(long movieId)
    {
        if(movieId < 0)
            return;
        Call<MovieVideosRequestResult> call = apiInterface.getMovieVideos(movieId, getApiKey());

        call.enqueue(new Callback<MovieVideosRequestResult>() {
            @Override
            public void onResponse(Call<MovieVideosRequestResult> call, Response<MovieVideosRequestResult> response) {
                int statusCode = response.code();
                MovieVideosRequestResult movieRequestResult = response.body();
                if (movieRequestResult == null)
                    return;
                if(movieRequestResult.getResults() != null && movieRequestResult.getResults().size() > 0)
                    watchYoutubeVideo(movieRequestResult.getResults().get(0).getKey());
            }

            @Override
            public void onFailure(Call<MovieVideosRequestResult> call, Throwable t) {

            }
        });
    }

    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private String getApiKey()
    {
        return PropertyUtils.getProperty(this, getString(R.string.properties_filename), getString(R.string.properties_moviedb_api_key));
    }

    private void getBackdropImage(final long movieId)
    {
        Call<MovieImagesRequestResult> call = apiInterface.getMovieImages(movieId, getApiKey());

        call.enqueue(new Callback<MovieImagesRequestResult>() {
            @Override
            public void onResponse(Call<MovieImagesRequestResult> call, Response<MovieImagesRequestResult> response) {
                int statusCode = response.code();
                MovieImagesRequestResult movieRequestResult = response.body();
                if (movieRequestResult == null)
                    return;
                if(movieRequestResult.getBackdrops() != null && movieRequestResult.getBackdrops().size() > 0)
                    //mImagePath = movieRequestResult.getBackdrops().get(0).getFilePath();
                    ImageUtils.insertImageIntoView(mPosterImage, getApplicationContext(), movieRequestResult.getBackdrops().get(0).getFilePath(), ImageUtils.PosterSize.BACKDROP);
                else if (movieRequestResult.getPosters() != null && movieRequestResult.getPosters().size() > 0)
                    ImageUtils.insertImageIntoView(mPosterImage, getApplicationContext(), movieRequestResult.getPosters().get(0).getFilePath(), ImageUtils.PosterSize.BIG);
                else
                    return;
            }

            @Override
            public void onFailure(Call<MovieImagesRequestResult> call, Throwable t) {
            }
        });
    }

    private void setDetailViews(Intent starterIntent)
    {
        mId = starterIntent.getLongExtra(MovieContract.MovieEntry._ID, -1);
        getBackdropImage(mId);
        String title = starterIntent.getStringExtra(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        String overview = starterIntent.getStringExtra(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        double rating = starterIntent.getDoubleExtra(MovieContract.MovieEntry.COLUMN_USER_RATING, -1);
        String releaseDate = starterIntent.getStringExtra(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        mOriginalTitle.setText(title);
        mPosterImage.setContentDescription(title);
        mOverview.setText(overview);
        mRating.setText(Double.toString(rating));
        mReleaseDate.setText(releaseDate);
    }

}
