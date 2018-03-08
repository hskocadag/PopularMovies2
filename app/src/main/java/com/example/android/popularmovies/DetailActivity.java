package com.example.android.popularmoviesstage1;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage1.data.MovieContract;
import com.example.android.popularmoviesstage1.model.Movie;
import com.example.android.popularmoviesstage1.model.MovieImagesRequestResult;
import com.example.android.popularmoviesstage1.model.MovieReviewsRequestResult;
import com.example.android.popularmoviesstage1.model.MovieVideosRequestResult;
import com.example.android.popularmoviesstage1.utilities.ImageUtils;
import com.example.android.popularmoviesstage1.utilities.PropertyUtils;
import com.example.android.popularmoviesstage1.utilities.retrofitQueries.APIClient;
import com.example.android.popularmoviesstage1.utilities.retrofitQueries.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private ImageView mPosterImageView;
    private TextView mOriginalTitleView;
    private TextView mOverviewView;
    private TextView mRatingView;
    private TextView mReleaseDateView;
    private APIInterface apiInterface;
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;
    private FloatingActionButton mFABFavMovie;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterImageView = findViewById(R.id.detail_movie_poster);
        mOriginalTitleView = findViewById(R.id.detail_title_value);
        mOverviewView = findViewById(R.id.detail_overview_value);
        mRatingView = findViewById(R.id.detail_rating_value);
        mReleaseDateView = findViewById(R.id.detail_release_date_value);
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
            mMovie = starterIntent.getParcelableExtra(MovieContract.MovieEntry.TABLE_NAME);
        }
        if(mMovie != null && mMovie.getId() > 0)
        {
            mFABFavMovie = (FloatingActionButton) findViewById(R.id.fab_fav_movie);

            mFABFavMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addMovieToFavs();
                }
            });
            FloatingActionButton fabTrailer = (FloatingActionButton) findViewById(R.id.fab_watch_trailer);

            fabTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTrailers();
                }
            });
            apiInterface = APIClient.getClient().create(APIInterface.class);
            setDetailViews();
            handleComments();
            setStarColor();
        }
        else
        {
            Toast.makeText(this, "Oops, something went wrong!", Toast.LENGTH_LONG).show();
        }

    }

    private void addMovieToFavs()
    {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getPlotSynopsis());
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getFormattedReleaseDate());
        cv.put(MovieContract.MovieEntry.COLUMN_USER_RATING, mMovie.getUserRating());
        Uri uri = getContentResolver().insert(MovieContract.BASE_CONTENT_URI, cv);
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry._ID + "=?", new String[]{Long.toString(mMovie.getId())}, null);
        if(cursor != null && cursor.getCount() > 0)
        {

        }
    }

    private void setStarColor()
    {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry._ID + "=?", new String[]{Long.toString(mMovie.getId())}, null);
        boolean isFav;
        if(cursor == null)
            isFav = false;
        else
            isFav = cursor.getCount() > 0;
        //TODO delete 278 after fav feature is developed
        if(isFav || mMovie.getId() == 278)
            mFABFavMovie.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.theme_accent)));
        else
            mFABFavMovie.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.theme_primary)));
    }

    private void handleComments()
    {
        Call<MovieReviewsRequestResult> call = apiInterface.getMovieReviews(mMovie.getId(), getApiKey());

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

    private void getTrailers()
    {
        Call<MovieVideosRequestResult> call = apiInterface.getMovieVideos(mMovie.getId(), getApiKey());

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

    private void getBackdropImage()
    {
        Call<MovieImagesRequestResult> call = apiInterface.getMovieImages(mMovie.getId(), getApiKey());

        call.enqueue(new Callback<MovieImagesRequestResult>() {
            @Override
            public void onResponse(Call<MovieImagesRequestResult> call, Response<MovieImagesRequestResult> response) {
                int statusCode = response.code();
                MovieImagesRequestResult movieRequestResult = response.body();
                if (movieRequestResult == null)
                    return;
                if(movieRequestResult.getBackdrops() != null && movieRequestResult.getBackdrops().size() > 0)
                    //mImagePath = movieRequestResult.getBackdrops().get(0).getFilePath();
                    ImageUtils.insertImageIntoView(mPosterImageView, getApplicationContext(), movieRequestResult.getBackdrops().get(0).getFilePath(), ImageUtils.PosterSize.BACKDROP);
                else if (movieRequestResult.getPosters() != null && movieRequestResult.getPosters().size() > 0)
                    ImageUtils.insertImageIntoView(mPosterImageView, getApplicationContext(), movieRequestResult.getPosters().get(0).getFilePath(), ImageUtils.PosterSize.BIG);
                else
                    return;
            }

            @Override
            public void onFailure(Call<MovieImagesRequestResult> call, Throwable t) {
            }
        });
    }

    private void setDetailViews()
    {
        getBackdropImage();
        mOriginalTitleView.setText(mMovie.getOriginalTitle());
        mPosterImageView.setContentDescription(mMovie.getOriginalTitle());
        mOverviewView.setText(mMovie.getPlotSynopsis());
        mRatingView.setText(Double.toString(mMovie.getUserRating()));
        mReleaseDateView.setText(mMovie.getFormattedReleaseDate());
    }

}
