package com.musasyihab.themovieproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.musasyihab.themovieproject.R;
import com.musasyihab.themovieproject.model.MovieModel;
import com.musasyihab.themovieproject.util.Constants;
import com.musasyihab.themovieproject.util.Helper;

import java.util.List;

/**
 * Created by musasyihab on 7/9/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private List<MovieModel> movieList;
    private final MovieAdapterClickListener mOnClickListener;
    private Context context;

    public interface MovieAdapterClickListener {
        void onMovieClick(MovieModel movieClicked);
    }

    public MovieAdapter (Context context, List<MovieModel> movieList, MovieAdapterClickListener listener) {
        Log.d(TAG, movieList.toString());
        this.context = context;
        this.movieList = movieList;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_item_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: ");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(movieList.get(position));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView moviePoster;
        TextView movieTitle;
        TextView movieYear;

        public MovieViewHolder(View itemView) {
            super(itemView);

            moviePoster = (ImageView) itemView.findViewById(R.id.movie_item_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_item_title);
            movieYear = (TextView) itemView.findViewById(R.id.movie_item_year);
            itemView.setOnClickListener(this);
        }

        void bind(MovieModel movie) {
            if(movie!=null) {
                String poster = Constants.POSTER_PATH + movie.getPoster_path();
                Glide.with(context).load(poster).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).into(moviePoster);
                movieTitle.setText(movie.getTitle());
                movieYear.setText(Helper.getYear(movie.getRelease_date()));
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onMovieClick(movieList.get(clickedPosition));
        }
    }
}
