package com.musasyihab.themovieproject.network;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.musasyihab.themovieproject.data.DBContract;
import com.musasyihab.themovieproject.model.MovieModel;

import java.io.IOException;

/**
 * Created by musasyihab on 8/2/17.
 */

public class TaskGetMovieDetail extends AsyncTaskLoader<MovieModel> {

    private int movieId;
    private MovieModel apiResponse;
    private Context context;

    public TaskGetMovieDetail(Context context, int movieId) {
        super(context);
        this.context = context;
        this.movieId = movieId;
    }

    @Override
    public MovieModel loadInBackground() {
        try {
            apiResponse = ApiUtils.setupRetrofit().getMovieDetail(movieId).execute().body();
            Uri uri = DBContract.FavoriteEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movieId + "").build();

            Cursor result = context.getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    null);

            int colIndex = result.getColumnIndex(DBContract.FavoriteEntry.COLUMN_MOVIE_ID);

            result.moveToFirst();

            if (result.getCount()>=colIndex && result.getInt(colIndex) == movieId){
                apiResponse.setFavorite(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiResponse;
    }

}
