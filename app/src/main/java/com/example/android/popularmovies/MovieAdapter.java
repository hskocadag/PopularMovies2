package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final GridItemClickListener mGridItemClickListener;
    private List<Movie> mMovies;
    private final Context mContext;

    public MovieAdapter(Context context, GridItemClickListener gridItemClickListener)
    {
        mGridItemClickListener = gridItemClickListener;
        mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if(position >= getItemCount())
            return;

        String imageUrl = mMovies.get(position).getImageUrl();
        ImageUtils.insertImageIntoView(holder.mMoviePoster, mContext, imageUrl, ImageUtils.PosterSize.SMALL);
        holder.mMoviePoster.setContentDescription(mMovies.get(position).getOriginalTitle());
    }

    @Override
    public int getItemCount() {
        if(mMovies == null)
            return 0;
        return mMovies.size();
    }

    public interface GridItemClickListener {
        void onGridItemClick(int clickedItemId);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final ImageView mMoviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mMoviePoster = itemView.findViewById(R.id.image_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mGridItemClickListener.onGridItemClick(adapterPosition);
        }

    }

    public void updateMoviesArray(List<Movie> movies)
    {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public void insertToMoviesArray(List<Movie> movies, int startIndex, int itemCount)
    {
        mMovies = movies;
        notifyItemRangeInserted(startIndex, itemCount);
    }

}
