package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Movie implements Parcelable{

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("poster_path")
    private String imageUrl;

    @SerializedName("overview")
    private String plotSynopsis;

    @SerializedName("vote_average")
    private double userRating;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("id")
    private long id;

    public Movie(String originalTitle, String imageUrl, String plotSynopsis, double userRating, String releaseDate, long id) {
        this.originalTitle = originalTitle;
        this.imageUrl = imageUrl;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.originalTitle);
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.plotSynopsis);
        parcel.writeString(this.releaseDate);
        parcel.writeDouble(this.userRating);
        parcel.writeLong(this.id);
    }

    protected Movie(Parcel in) {
        originalTitle = in.readString();
        imageUrl = in.readString();
        plotSynopsis = in.readString();
        releaseDate = in.readString();
        userRating = in.readDouble();
        id = in.readLong();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getFormattedReleaseDate()
    {
        try {
            Date date = (new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)).parse(releaseDate);
            DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
