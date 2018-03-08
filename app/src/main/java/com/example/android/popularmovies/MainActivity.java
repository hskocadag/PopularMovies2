package com.example.android.popularmoviesstage1;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmoviesstage1.data.MovieContract;
import com.example.android.popularmoviesstage1.model.Movie;
import com.example.android.popularmoviesstage1.model.MovieRequestResult;
import com.example.android.popularmoviesstage1.utilities.NetworkUtils;
import com.example.android.popularmoviesstage1.utilities.PropertyUtils;
import com.example.android.popularmoviesstage1.utilities.retrofitQueries.APIClient;
import com.example.android.popularmoviesstage1.utilities.retrofitQueries.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.GridItemClickListener{

    private static final int GRID_LAYOUT_SPAN_COUNT = 3;
    private TextView mErrorMessage;
    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMovieAdapter;
    private Movie[] mMovies;
    private MenuItem mSortByPopularity;
    private MenuItem mSortByRating;
    private NetworkUtils.OrderType lastOrder;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mErrorMessage = findViewById(R.id.tv_error_message);
        mMoviesRecyclerView = findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, GRID_LAYOUT_SPAN_COUNT, GridLayoutManager.VERTICAL, false);

        mErrorMessage.setVisibility(View.GONE);
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMovieAdapter = new MovieAdapter(this, this);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        loadMovies(NetworkUtils.OrderType.POPULARITY);
        lastOrder = NetworkUtils.OrderType.POPULARITY;
    }

    private void loadMovies(NetworkUtils.OrderType orderType) {
        if(isOnline()) {
            lastOrder = orderType;
            Call<MovieRequestResult> call = apiInterface.getPopularMovies(getApiKey());
            if (orderType == NetworkUtils.OrderType.RATING)
                call = apiInterface.getTopRatedMovies(getApiKey());

            call.enqueue(new Callback<MovieRequestResult>() {
                @Override
                public void onResponse(Call<MovieRequestResult> call, Response<MovieRequestResult> response) {
                    int statusCode = response.code();
                    MovieRequestResult movieRequestResult = response.body();
                    if (movieRequestResult == null)
                        return;
                    Movie[] movies = new Movie[movieRequestResult.getMovies().size()];
                    movies = movieRequestResult.getMovies().toArray(movies);
                    if (movies == null) {
                        mErrorMessage.setVisibility(View.VISIBLE);
                        mMoviesRecyclerView.setVisibility(View.GONE);
                    } else {
                        mErrorMessage.setVisibility(View.GONE);
                        mMoviesRecyclerView.setVisibility(View.VISIBLE);
                        mMovies = movies;
                        mMovieAdapter.updateMoviesArray(movies);
                    }
                }

                @Override
                public void onFailure(Call<MovieRequestResult> call, Throwable t) {
                    mErrorMessage.setVisibility(View.VISIBLE);
                    mMoviesRecyclerView.setVisibility(View.GONE);
                }
            });
        }

        adjustTitle(lastOrder);
        adjustMenuItems(lastOrder);
    }

    @Override
    public void onGridItemClick(int clickedItemId) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        Movie clickedMovie = mMovies[clickedItemId];
        detailIntent.putExtra(MovieContract.MovieEntry.TABLE_NAME, clickedMovie);
//        detailIntent.putExtra(MovieContract.MovieEntry.COLUMN_POSTER, clickedMovie.getImageUrl());
//        detailIntent.putExtra(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, clickedMovie.getOriginalTitle());
//        detailIntent.putExtra(MovieContract.MovieEntry.COLUMN_OVERVIEW, clickedMovie.getPlotSynopsis());
//        detailIntent.putExtra(MovieContract.MovieEntry.COLUMN_USER_RATING, clickedMovie.getUserRating());
//        detailIntent.putExtra(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, clickedMovie.getFormattedReleaseDate());
//        detailIntent.putExtra(MovieContract.MovieEntry._ID, clickedMovie.getId());
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.order_movies_main_menu, menu);
        mSortByPopularity = menu.getItem(0);
        mSortByRating = menu.getItem(1);
        mSortByPopularity.setEnabled(false);
        return true;
    }

    private String getApiKey()
    {
        return PropertyUtils.getProperty(this, getString(R.string.properties_filename), getString(R.string.properties_moviedb_api_key));
    }

    private void adjustMenuItems(NetworkUtils.OrderType orderType)
    {
        if(mSortByPopularity == null || mSortByRating == null)
            return;
        if(mErrorMessage.getVisibility() == View.VISIBLE)
        {
            mSortByPopularity.setEnabled(true);
            mSortByPopularity.setEnabled(true);
        }else if(orderType == NetworkUtils.OrderType.POPULARITY)
        {
            mSortByPopularity.setEnabled(false);
            mSortByRating.setEnabled(true);
        }
        else if(orderType == NetworkUtils.OrderType.RATING)
        {
            mSortByPopularity.setEnabled(true);
            mSortByRating.setEnabled(false);
        }
    }

    private void adjustTitle(NetworkUtils.OrderType orderType)
    {
        if(mErrorMessage.getVisibility() == View.VISIBLE)
        {
            setTitle(R.string.app_name);
        }else if(orderType == NetworkUtils.OrderType.POPULARITY)
            setTitle(R.string.order_by_popularity_title);
        else if(orderType == NetworkUtils.OrderType.RATING)
            setTitle(R.string.order_by_rating_title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.popularity_menu_item)
        {
            loadMovies(NetworkUtils.OrderType.POPULARITY);
        }
        else if(id == R.id.rating_menu_item)
        {
            loadMovies(NetworkUtils.OrderType.RATING);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(cm == null)
            return false;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
