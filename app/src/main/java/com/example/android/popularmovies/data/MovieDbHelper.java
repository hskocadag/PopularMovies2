package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Semper_Sinem on 2.03.2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 3;

    public MovieDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_QUERY = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ( " +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIEDB_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_USER_RATING + " REAL, " +
                MovieContract.MovieEntry.COLUMN_POSTER + " BLOB" +
                MovieContract.MovieEntry.COLUMN_BACKDROP + " BLOB );" ;
        sqLiteDatabase.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String DROP_QUERY = "DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(DROP_QUERY);
        onCreate(sqLiteDatabase);
    }
}
