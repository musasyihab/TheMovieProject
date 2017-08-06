package com.musasyihab.themovieproject.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.musasyihab.themovieproject.model.response.GetMovieReviewsResponse;

import java.io.IOException;

/**
 * Created by musasyihab on 8/2/17.
 */

public class TaskGetMovieReviews extends AsyncTaskLoader<GetMovieReviewsResponse> {

    private int movieId;
    private GetMovieReviewsResponse apiResponse;

    public TaskGetMovieReviews(Context context, int movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    public GetMovieReviewsResponse loadInBackground() {
        try {
            apiResponse = ApiUtils.setupRetrofit().getMovieReviews(movieId).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiResponse;
    }

}
