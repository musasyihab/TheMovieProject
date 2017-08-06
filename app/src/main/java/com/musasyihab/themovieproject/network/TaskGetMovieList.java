package com.musasyihab.themovieproject.network;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.musasyihab.themovieproject.model.response.GetMovieListResponse;
import com.musasyihab.themovieproject.util.Constants;

import java.io.IOException;

/**
 * Created by musasyihab on 8/2/17.
 */

public class TaskGetMovieList extends AsyncTaskLoader<GetMovieListResponse> {

    private int listType;
    private GetMovieListResponse apiResponse;

    public TaskGetMovieList(Context context, int listType) {
        super(context);
        this.listType = listType;
    }

    @Override
    public GetMovieListResponse loadInBackground() {
        if(listType == Constants.SORT_BY_POPULAR) {
            try {
                apiResponse = ApiUtils.setupRetrofit().getTopRatedMovieList().execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                apiResponse = ApiUtils.setupRetrofit().getPopularMovieList().execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return apiResponse;
    }

}
