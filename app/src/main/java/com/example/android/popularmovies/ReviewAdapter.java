package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.model.MovieReview;

import java.util.List;

import us.feras.mdv.MarkdownView;

/**
 * Created by Semper_Sinem on 7.03.2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<MovieReview> mReviews;
    private final Context mContext;

    public ReviewAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_user_review, parent, false);
        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        if(position >= getItemCount())
            return;

        MovieReview review = mReviews.get(position);
        holder.mReviewContent.setText(review.getContent());
        holder.mAuthorName.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        if(mReviews == null)
            return 0;
        return mReviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView mAuthorName;
        public final TextView mReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorName = itemView.findViewById(R.id.tv_author);
            mReviewContent = itemView.findViewById(R.id.tv_review_content);
        }

    }

    public void updateReviewList(List<MovieReview> reviews)
    {
        mReviews = reviews;
        notifyDataSetChanged();
    }

}
