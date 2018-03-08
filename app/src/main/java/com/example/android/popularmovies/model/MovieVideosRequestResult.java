package com.example.android.popularmoviesstage1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Semper_Sinem on 7.03.2018.
 */

public class MovieVideosRequestResult {

    @SerializedName("id")
    private long id;

    @SerializedName("results")
    private List<MovieVideo> results;

    public MovieVideosRequestResult(long id, List<MovieVideo> results) {
        this.id = id;
        this.results = results;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<MovieVideo> getResults() {
        return results;
    }

    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }
}
