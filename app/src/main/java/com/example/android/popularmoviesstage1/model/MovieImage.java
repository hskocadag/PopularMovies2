package com.example.android.popularmoviesstage1.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Semper_Sinem on 7.03.2018.
 */

public class MovieImage {

    @SerializedName("aspect_ratio")
    private double aspectRatio;

    @SerializedName("file_path")
    private String filePath;

    @SerializedName("height")
    private String height;

    @SerializedName("width")
    private String width;

    public MovieImage(double aspectRatio, String filePath, String height, String width) {
        this.aspectRatio = aspectRatio;
        this.filePath = filePath;
        this.height = height;
        this.width = width;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
