package com.example.android.popularmoviesstage1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Semper_Sinem on 7.03.2018.
 */

public class MovieReviewsRequestResult {

    @SerializedName("id")
    private long id;

    @SerializedName("results")
    private List<MovieReview> results;

    public MovieReviewsRequestResult(long id, List<MovieReview> results) {
        this.id = id;
        this.results = results;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<MovieReview> getResults() {
        return results;
    }

    public void setResults(List<MovieReview> results) {
        this.results = results;
    }
}
