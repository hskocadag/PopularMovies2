package com.example.android.popularmoviesstage1.utilities.retrofitQueries;

/**
 * Created by Semper_Sinem on 2.03.2018.
 */

import com.example.android.popularmoviesstage1.model.MovieRequestResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("movie/popular?")
    Call<MovieRequestResult> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated?")
    Call<MovieRequestResult> getTopRatedMovies(@Query("api_key") String apiKey);

}
