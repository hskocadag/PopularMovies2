package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Semper_Sinem on 7.03.2018.
 */

public class MovieImagesRequestResult {

    @SerializedName("id")
    private long id;

    @SerializedName("backdrops")
    private List<MovieImage> backdrops;

    @SerializedName("posters")
    private List<MovieImage> posters;

    public MovieImagesRequestResult(long id, List<MovieImage> backdrops, List<MovieImage> posters) {
        this.id = id;
        this.backdrops = backdrops;
        this.posters = posters;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<MovieImage> getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(List<MovieImage> backdrops) {
        this.backdrops = backdrops;
    }

    public List<MovieImage> getPosters() {
        return posters;
    }

    public void setPosters(List<MovieImage> posters) {
        this.posters = posters;
    }
}
