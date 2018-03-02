package com.example.android.popularmoviesstage1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Semper_Sinem on 2.03.2018.
 */

public class MovieContract {

    public static final String PROVIDER_NAME = "com.example.android.popularmoviesstage1.data.MovieContentProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_USER_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
    }
}
