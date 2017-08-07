package com.musasyihab.themovieproject.network;

import android.content.ContentValues;
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

        // get data from local storage first
        MovieModel localData = new MovieModel();

        Uri uri = DBContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId+"").build();
        Cursor result = context.getContentResolver().query(uri, DBContract.MovieEntry.PROJECTION, null, null, null);

        int colTitle = result.getColumnIndex(DBContract.MovieEntry.COLUMN_TITLE);
        int colOverview = result.getColumnIndex(DBContract.MovieEntry.COLUMN_OVERVIEW);
        int colPoster = result.getColumnIndex(DBContract.MovieEntry.COLUMN_POSTER_PATH);
        int colRelease = result.getColumnIndex(DBContract.MovieEntry.COLUMN_RELEASE_DATE);
        int colVote = result.getColumnIndex(DBContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int colFavorite = result.getColumnIndex(DBContract.MovieEntry.COLUMN_IS_FAVORITE);

        result.moveToFirst();

        if (result.getColumnCount()>=DBContract.MovieEntry.PROJECTION.length){
            localData.setId(movieId);
            localData.setTitle(result.getString(colTitle));
            localData.setOverview(result.getString(colOverview));
            localData.setPoster_path(result.getString(colPoster));
            localData.setRelease_date(result.getString(colRelease));
            localData.setVote_average(result.getFloat(colVote));
            localData.setFavorite(result.getInt(colFavorite) > 0);
        }


        // then try to get latest data from API
        try {
            apiResponse = ApiUtils.setupRetrofit().getMovieDetail(movieId).execute().body();
//            Uri uri = DBContract.FavoriteEntry.CONTENT_URI;
//            uri = uri.buildUpon().appendPath(movieId + "").build();
//
//            Cursor result = context.getContentResolver().query(uri,
//                    null,
//                    null,
//                    null,
//                    null);
//
//            int colIndex = result.getColumnIndex(DBContract.FavoriteEntry.COLUMN_MOVIE_ID);
//
//            result.moveToFirst();
//
//            if (result.getColumnCount()>=colIndex && result.getInt(colIndex) == movieId){
//                apiResponse.setFavorite(true);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if get data from API is success, update local storage
        if(apiResponse != null) {
            Uri uriUpdate = DBContract.MovieEntry.CONTENT_URI;
            uriUpdate = uriUpdate.buildUpon().appendPath(movieId + "").build();

            ContentValues value = new ContentValues();
            value.put(DBContract.MovieEntry._ID, movieId);
            value.put(DBContract.MovieEntry.COLUMN_TITLE, apiResponse.getTitle());
            value.put(DBContract.MovieEntry.COLUMN_POSTER_PATH, apiResponse.getPoster_path());
            value.put(DBContract.MovieEntry.COLUMN_RELEASE_DATE, apiResponse.getRelease_date());
            value.put(DBContract.MovieEntry.COLUMN_OVERVIEW, apiResponse.getOverview());
            value.put(DBContract.MovieEntry.COLUMN_VOTE_AVERAGE, apiResponse.getVote_average());
            if(localData!=null && localData.isFavorite()) {
                value.put(DBContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
            } else {
                value.put(DBContract.MovieEntry.COLUMN_IS_FAVORITE, false);
            }
            apiResponse.setFavorite(localData!=null && localData.isFavorite());
            context.getContentResolver().update(uriUpdate, value, null, null);

            return apiResponse;

        } else { // if fail, return the local storage
            return localData;
        }

    }

}
