package com.example.android.popularmoviesstage1.model;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Movie {

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("poster_path")
    private String imageUrl;

    @SerializedName("overview")
    private String plotSynopsis;

    @SerializedName("vote_average")
    private double userRating;

    @SerializedName("release_date")
    private Date releaseDate;

    @SerializedName("id")
    private long id;

    public Movie(String originalTitle, String imageUrl, String plotSynopsis, double userRating, Date releaseDate, long id) {
        this.originalTitle = originalTitle;
        this.imageUrl = imageUrl;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getFormattedReleaseDate()
    {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        return df.format(releaseDate);
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
