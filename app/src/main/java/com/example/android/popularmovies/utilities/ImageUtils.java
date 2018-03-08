package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class ImageUtils {

    public static void insertImageIntoView(ImageView imageView, Context context, String imageUrl, PosterSize posterSize)
    {
        URL url = NetworkUtils.buildPosterUrl(imageUrl, posterSize.toString());
        if(url == null)
            return;
        Picasso.with(context).load(url.toString()).placeholder(R.drawable.poster_placeholder).into(imageView);
    }

    public enum PosterSize {
        SMALL ("w342"),
        BIG ("w500"),
        BACKDROP ("w1280");

        private final String fSizeString;

        PosterSize(String sizeString)
        {
            fSizeString = sizeString;
        }

        @Override
        public String toString() {
            return fSizeString;
        }
    }
}
