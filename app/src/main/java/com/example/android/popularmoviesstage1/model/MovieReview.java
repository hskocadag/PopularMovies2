package com.example.android.popularmoviesstage1.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Semper_Sinem on 7.03.2018.
 */

public class MovieReview {

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    public MovieReview(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
