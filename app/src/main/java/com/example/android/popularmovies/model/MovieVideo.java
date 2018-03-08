package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Semper_Sinem on 7.03.2018.
 */

public class MovieVideo {

    @SerializedName("id")
    private String id;

    @SerializedName("iso_639_1")
    private String isoLanguage639_1;

    @SerializedName("iso_3166_1")
    private String isoLanguage3166_1;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("size")
    private String size;

    @SerializedName("type")
    private String type;

    public MovieVideo(String id, String isoLanguage639_1, String isoLanguage3166_1, String key, String name, String site, String size, String type) {
        this.id = id;
        this.isoLanguage639_1 = isoLanguage639_1;
        this.isoLanguage3166_1 = isoLanguage3166_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsoLanguage639_1() {
        return isoLanguage639_1;
    }

    public void setIsoLanguage639_1(String isoLanguage639_1) {
        this.isoLanguage639_1 = isoLanguage639_1;
    }

    public String getIsoLanguage3166_1() {
        return isoLanguage3166_1;
    }

    public void setIsoLanguage3166_1(String isoLanguage3166_1) {
        this.isoLanguage3166_1 = isoLanguage3166_1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
