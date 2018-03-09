package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.example.android.popularmovies.data.MovieContentProvider;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieImagesRequestResult;
import com.example.android.popularmovies.model.MovieReviewsRequestResult;
import com.example.android.popularmovies.model.MovieVideosRequestResult;
import com.example.android.popularmovies.utilities.ImageUtils;
import com.example.android.popularmovies.utilities.PropertyUtils;
import com.example.android.popularmovies.utilities.retrofitQueries.APIClient;
import com.example.android.popularmovies.utilities.retrofitQueries.APIInterface;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

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
    private boolean mIsFavMovie;
    private String mYoutubeKey;

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
                    updateFavStatus();
                }
            });

            FloatingActionButton fabTrailer = (FloatingActionButton) findViewById(R.id.fab_watch_trailer);
            fabTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTrailers();
                }
            });

            FloatingActionButton fabShareTrailer = (FloatingActionButton) findViewById(R.id.fab_share_trailer);
            fabShareTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareTrailer();
                }
            });

            mIsFavMovie = isFavMovie();
            apiInterface = APIClient.getClient().create(APIInterface.class);
            setDetailViews();
            handleComments();
            setStarColor(mIsFavMovie);
        }
        else
        {
            Toast.makeText(this, "Oops, something went wrong!", Toast.LENGTH_LONG).show();
        }

    }

    //TODO send original trailer
    private void ShareTrailer(){
        if(mYoutubeKey == null) {
            Call<MovieVideosRequestResult> call = apiInterface.getMovieVideos(mMovie.getId(), getApiKey());

            call.enqueue(new Callback<MovieVideosRequestResult>() {
                @Override
                public void onResponse(Call<MovieVideosRequestResult> call, Response<MovieVideosRequestResult> response) {
                    int statusCode = response.code();
                    MovieVideosRequestResult movieRequestResult = response.body();
                    if (movieRequestResult == null)
                        return;
                    if (movieRequestResult.getResults() != null && movieRequestResult.getResults().size() > 0) {
                        String key = movieRequestResult.getResults().get(0).getKey();
                        startShareIntent(key);
                        mYoutubeKey = key;
                    }
                }
                @Override
                public void onFailure(Call<MovieVideosRequestResult> call, Throwable t) {
                }
            });
        }
        else
        {
            startShareIntent(mYoutubeKey);
        }
    }

    private void startShareIntent(String key)
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        i.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + key);
        startActivity(Intent.createChooser(i, "Share URL"));
    }

    private boolean isFavMovie()
    {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIEDB_ID + "=?",
                new String[]{Long.toString(mMovie.getId())},
                null);
        if(cursor == null)
            return false;
        else
            return cursor.getCount() > 0;
    }

    private void updateFavStatus()
    {
        if(!mIsFavMovie) {
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIEDB_ID, mMovie.getId());
            cv.put(MovieContract.MovieEntry.COLUMN_POSTER, mMovie.getImageUrl());
            cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getPlotSynopsis());
            cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getFormattedReleaseDate());
            cv.put(MovieContract.MovieEntry.COLUMN_USER_RATING, mMovie.getUserRating());
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
        }
        else
        {
            String stringId = Long.toString(mMovie.getId());
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();
            getContentResolver().delete(
                    uri,
                    MovieContract.MovieEntry.COLUMN_MOVIEDB_ID + "=?",
                    null);
        }
        mIsFavMovie = isFavMovie();
        setStarColor(mIsFavMovie);
    }

    private void setStarColor(boolean isFav)
    {
        if(isFav)
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
                if (movieReviewsRequestResult == null
                        || movieReviewsRequestResult.getResults() == null
                        || movieReviewsRequestResult.getResults().size() <= 0) {
                    findViewById(R.id.tv_review_title).setVisibility(View.GONE);
                    mReviewRecyclerView.setVisibility(View.GONE);
                    return;
                }
                else
                    findViewById(R.id.tv_review_title).setVisibility(View.VISIBLE);
                    mReviewAdapter.updateReviewList(movieReviewsRequestResult.getResults());
            }

            @Override
            public void onFailure(Call<MovieReviewsRequestResult> call, Throwable t) {

            }
        });
    }

    private void getTrailers()
    {
        if(mYoutubeKey == null) {
            Call<MovieVideosRequestResult> call = apiInterface.getMovieVideos(mMovie.getId(), getApiKey());

            call.enqueue(new Callback<MovieVideosRequestResult>() {
                @Override
                public void onResponse(Call<MovieVideosRequestResult> call, Response<MovieVideosRequestResult> response) {
                    int statusCode = response.code();
                    MovieVideosRequestResult movieRequestResult = response.body();
                    if (movieRequestResult == null)
                        return;
                    if (movieRequestResult.getResults() != null && movieRequestResult.getResults().size() > 0) {
                        String key = movieRequestResult.getResults().get(0).getKey();
                        watchYoutubeVideo(key);
                        mYoutubeKey = key;
                    }

                }

                @Override
                public void onFailure(Call<MovieVideosRequestResult> call, Throwable t) {

                }
            });
        }
        else
        {
            watchYoutubeVideo(mYoutubeKey);
        }
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
