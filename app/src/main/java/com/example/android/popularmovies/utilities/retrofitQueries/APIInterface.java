package com.example.android.popularmovies.utilities.retrofitQueries;

/**
 * Created by Semper_Sinem on 2.03.2018.
 */

import com.example.android.popularmovies.model.MovieImagesRequestResult;
import com.example.android.popularmovies.model.MovieRequestResult;
import com.example.android.popularmovies.model.MovieReviewsRequestResult;
import com.example.android.popularmovies.model.MovieVideosRequestResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("movie/popular?")
    Call<MovieRequestResult> getPopularMovies(@Query("page") int page, @Query("api_key") String apiKey);

    @GET("movie/top_rated?")
    Call<MovieRequestResult> getTopRatedMovies(@Query("page") int page, @Query("api_key") String apiKey);

    @GET("movie/{id}/images?")
    Call<MovieImagesRequestResult> getMovieImages(@Path("id") long id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos?")
    Call<MovieVideosRequestResult> getMovieVideos(@Path("id") long id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews?")
    Call<MovieReviewsRequestResult> getMovieReviews(@Path("id") long id, @Query("api_key") String apiKey);
}
