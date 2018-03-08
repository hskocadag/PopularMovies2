package com.example.android.popularmovies.utilities;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    public enum OrderType {
        POPULARITY ("popular"),
        RATING ("top_rated");

        private final String fOrderStr;

        OrderType(String orderStr)
        {
            fOrderStr = orderStr;
        }

        @Override
        public String toString() {
            return fOrderStr;
        }
    }

    private static final String MOVIEDB_POSTER_API_BASE_URL = "http://image.tmdb.org/t/p";

    public static URL buildPosterUrl(String posterPath, String posterSize)
    {
        if(posterPath == null)
            return null;
        posterPath = posterPath.replace("/", "");
        Uri movieQueryUri = Uri.parse(MOVIEDB_POSTER_API_BASE_URL)
                .buildUpon()
                .appendPath(posterSize)
                .appendPath(posterPath)
                .build();
        try {
            return new URL(movieQueryUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
