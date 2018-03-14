package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieRequestResult;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.PropertyUtils;
import com.example.android.popularmovies.utilities.retrofitQueries.APIClient;
import com.example.android.popularmovies.utilities.retrofitQueries.APIInterface;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.annotation.RetentionPolicy.CLASS;

public class MainActivity extends AppCompatActivity implements MovieAdapter.GridItemClickListener{

    private static int GRID_LAYOUT_SPAN_COUNT = 3;
    private TextView mErrorMessage;
    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovies = new ArrayList<Movie>();
    private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    private APIInterface apiInterface;
    private static @MovieCategorie int movieCategory = 0;
    private GridLayoutManager mGridLayoutManager;
    private final String LIST_STATE_KEY = "LIST_STATE_KEY";
    private Parcelable mListState;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mErrorMessage = findViewById(R.id.tv_error_message);
        mMoviesRecyclerView = findViewById(R.id.rv_movies);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            GRID_LAYOUT_SPAN_COUNT = 5;

        mGridLayoutManager = new GridLayoutManager(this, GRID_LAYOUT_SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        mErrorMessage.setVisibility(View.GONE);
        mMoviesRecyclerView.setLayoutManager(mGridLayoutManager);
        mMovieAdapter = new MovieAdapter(this, this);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onLoadMore(int page) {
                loadMovies(movieCategory, page);
            }
        };
        mMoviesRecyclerView.addOnScrollListener(
                endlessRecyclerOnScrollListener
        );

        if(movieCategory == TOP_RATED)
            loadMovies(TOP_RATED, 1);
        else if(movieCategory == FAVOURITED)
            loadFavourites();
        else
            loadMovies(POPULAR, 1);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            GRID_LAYOUT_SPAN_COUNT = 5;
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            GRID_LAYOUT_SPAN_COUNT = 3;
        }
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        mListState = mGridLayoutManager.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, mListState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    private void loadMovies(@MovieCategorie int requestedMovieCategory, int page) {
        if(requestedMovieCategory == FAVOURITED)
            return;
        if(isOnline()) {
            Call<MovieRequestResult> call = apiInterface.getPopularMovies(getApiKey(), page);
            movieCategory = POPULAR;
            if (requestedMovieCategory == TOP_RATED)
            {
                call = apiInterface.getTopRatedMovies(getApiKey(), page);
                movieCategory = TOP_RATED;
            }

            call.enqueue(new Callback<MovieRequestResult>() {
                @Override
                public void onResponse(Call<MovieRequestResult> call, Response<MovieRequestResult> response) {
                    int statusCode = response.code();
                    MovieRequestResult movieRequestResult = response.body();
                    if (movieRequestResult == null)
                        return;
                    List<Movie> movies = movieRequestResult.getMovies();
                    if (movies == null) {
                        mErrorMessage.setText(R.string.error_internet_connection);
                        mErrorMessage.setVisibility(View.VISIBLE);
                        mMoviesRecyclerView.setVisibility(View.GONE);
                    } else {
                        mErrorMessage.setVisibility(View.GONE);
                        mMoviesRecyclerView.setVisibility(View.VISIBLE);
                        mMovies.addAll(movies);
                        mMovieAdapter.updateMoviesArray(mMovies);
                        if (mListState != null) {
                            mGridLayoutManager.onRestoreInstanceState(mListState);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MovieRequestResult> call, Throwable t) {
                    mErrorMessage.setText(R.string.error_internet_connection);
                    mErrorMessage.setVisibility(View.VISIBLE);
                    mMoviesRecyclerView.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            mErrorMessage.setText(R.string.error_internet_connection);
            mErrorMessage.setVisibility(View.VISIBLE);
            mMoviesRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGridItemClick(int clickedItemId) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        Movie clickedMovie = mMovies.get(clickedItemId);
        detailIntent.putExtra(MovieContract.MovieEntry.TABLE_NAME, clickedMovie);
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.order_movies_main_menu, menu);
        menuItems.add(menu.getItem(0));
        menuItems.add(menu.getItem(1));
        menuItems.add(menu.getItem(2));
        adjustMenuItems(menu.getItem(movieCategory));
        return true;
    }

    private String getApiKey()
    {
        return PropertyUtils.getProperty(this, getString(R.string.properties_filename), getString(R.string.properties_moviedb_api_key));
    }

    private void adjustMenuItems(MenuItem active)
    {
        if(menuItems == null || menuItems.size() == 0)
            return;
        for(MenuItem menuItem : menuItems)
        {
            if(menuItem == active)
            {
                menuItem.setEnabled(false);
            }
            else
            {
                menuItem.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mMovies.removeAll(mMovies);
        endlessRecyclerOnScrollListener.resetScroller();
        int id = item.getItemId();
        if(id == R.id.popularity_menu_item)
        {
            loadMovies(POPULAR, 1);
            setTitle(R.string.order_by_popularity_title);
        }
        else if(id == R.id.rating_menu_item)
        {
            loadMovies(TOP_RATED, 1);
            setTitle(R.string.order_by_rating_title);
        }
        else if(id == R.id.favourites_menu_item)
        {
            loadFavourites();
            setTitle(R.string.favourites_title);
        }
        adjustMenuItems(item);
        return super.onOptionsItemSelected(item);
    }

    private void loadFavourites() {
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if(cursor == null || cursor.getCount() == 0) {
            mErrorMessage.setText(R.string.no_favourites);
            mErrorMessage.setVisibility(View.VISIBLE);
            mMoviesRecyclerView.setVisibility(View.GONE);
        }
        else {
            List<Movie> movies = new ArrayList<Movie>();
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++, cursor.moveToNext())
            {
                movies.add(new Movie(
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
                        cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)),
                        cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIEDB_ID))
                ));
            }
            mErrorMessage.setVisibility(View.GONE);
            mMoviesRecyclerView.setVisibility(View.VISIBLE);
            mMovies = movies;
            mMovieAdapter.updateMoviesArray(movies);
        }
        movieCategory = FAVOURITED;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(cm == null)
            return false;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Retention(CLASS)
    @IntDef({POPULAR, TOP_RATED, FAVOURITED})
    public @interface MovieCategorie {}
    public static final int POPULAR = 0;
    public static final int TOP_RATED = 1;
    public static final int FAVOURITED = 2;
}
