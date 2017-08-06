package com.musasyihab.themovieproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.musasyihab.themovieproject.R;
import com.musasyihab.themovieproject.model.ReviewModel;

import java.util.List;

/**
 * Created by musasyihab on 7/9/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private List<ReviewModel> reviewList;
    private Context context;

    public ReviewAdapter(Context context, List<ReviewModel> reviewList) {
        Log.d(TAG, reviewList.toString());
        this.context = context;
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_item_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: ");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(reviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView reviewAuthor;
        TextView reviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            reviewAuthor = (TextView) itemView.findViewById(R.id.review_item_author);
            reviewContent = (TextView) itemView.findViewById(R.id.review_item_content);
        }

        void bind(ReviewModel review) {
            if(review!=null) {
                reviewAuthor.setText(review.getAuthor());
                reviewContent.setText(review.getContent());
            }
        }
    }
}
