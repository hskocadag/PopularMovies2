package com.example.android.popularmoviesstage1.utilities;

import com.example.android.popularmoviesstage1.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JSONUtils {

    private static final String KEY_RESULTS = "results";
    private static final String KEY_ID = "id";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_ORIGINAL_TITLE = "original_title";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_RELEASE_DATE = "release_date";

    public static Movie[] getMoviesFromJSON(String json)
    {
        try {
            JSONObject rootObject = new JSONObject(json);
            JSONArray resultsArray = rootObject.optJSONArray(KEY_RESULTS);
            if(resultsArray != null && resultsArray.length() > 0)
            {
                Movie[] movies = new Movie[resultsArray.length()];
                for (int i = 0; i < resultsArray.length(); i++)
                {
                    JSONObject movieObject = resultsArray.optJSONObject(i);
                    String originalTitle = movieObject.optString(KEY_ORIGINAL_TITLE);
                    String posterPath = movieObject.optString(KEY_POSTER_PATH);
                    String overview = movieObject.optString(KEY_OVERVIEW);
                    long id = movieObject.optLong(KEY_ID);
                    double voteAverage = movieObject.optDouble(KEY_VOTE_AVERAGE);
                    String releaseDateStr = movieObject.optString(KEY_RELEASE_DATE);
                    Date releaseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(releaseDateStr);
                    movies[i] = new Movie(originalTitle, posterPath, overview, voteAverage, releaseDate, id);
                }
                return movies;
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
