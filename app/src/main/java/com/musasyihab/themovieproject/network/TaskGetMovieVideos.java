package com.musasyihab.themovieproject.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.musasyihab.themovieproject.model.response.GetMovieVideosResponse;

import java.io.IOException;

/**
 * Created by musasyihab on 8/2/17.
 */

public class TaskGetMovieVideos extends AsyncTaskLoader<GetMovieVideosResponse> {

    private int movieId;
    private GetMovieVideosResponse apiResponse;

    public TaskGetMovieVideos(Context context, int movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    public GetMovieVideosResponse loadInBackground() {
        try {
            apiResponse = ApiUtils.setupRetrofit().getMovieVideos(movieId).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiResponse;
    }

}
