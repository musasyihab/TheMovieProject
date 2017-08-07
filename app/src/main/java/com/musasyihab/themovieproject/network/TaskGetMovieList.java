package com.musasyihab.themovieproject.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.musasyihab.themovieproject.data.DBContract;
import com.musasyihab.themovieproject.model.MovieModel;
import com.musasyihab.themovieproject.model.response.GetMovieListResponse;
import com.musasyihab.themovieproject.util.Constants;

import java.io.IOException;

/**
 * Created by musasyihab on 8/2/17.
 */

public class TaskGetMovieList extends AsyncTaskLoader<GetMovieListResponse> {

    private int listType;
    private GetMovieListResponse apiResponse;
    private Context context;

    public TaskGetMovieList(Context context, int listType) {
        super(context);
        this.context = context;
        this.listType = listType;
    }

    @Override
    public GetMovieListResponse loadInBackground() {
        // getting movie from API
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

        // inserting movies to content provider
        ContentValues[] values = new ContentValues[apiResponse.getResults().size()];
        for (int i=0; i<apiResponse.getResults().size(); i++){

            // get data from local storage first to check the isFavorite value
            MovieModel localData = new MovieModel();

            Uri uri = DBContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(apiResponse.getResults().get(i).getId()+"").build();
            Cursor result = context.getContentResolver().query(uri, null, null, null, null);

            int colTitle = result.getColumnIndex(DBContract.MovieEntry.COLUMN_TITLE);
            int colOverview = result.getColumnIndex(DBContract.MovieEntry.COLUMN_OVERVIEW);
            int colPoster = result.getColumnIndex(DBContract.MovieEntry.COLUMN_POSTER_PATH);
            int colRelease = result.getColumnIndex(DBContract.MovieEntry.COLUMN_RELEASE_DATE);
            int colVote = result.getColumnIndex(DBContract.MovieEntry.COLUMN_VOTE_AVERAGE);
            int colFavorite = result.getColumnIndex(DBContract.MovieEntry.COLUMN_IS_FAVORITE);

            if (result.getCount()>0){
                result.moveToFirst();

                localData.setId(apiResponse.getResults().get(i).getId());
                localData.setTitle(result.getString(colTitle));
                localData.setOverview(result.getString(colOverview));
                localData.setPoster_path(result.getString(colPoster));
                localData.setRelease_date(result.getString(colRelease));
                localData.setVote_average(result.getFloat(colVote));
                localData.setFavorite(result.getInt(colFavorite) > 0);
            }

            // then prepare content value for inserting the data to content provider
            MovieModel movie = apiResponse.getResults().get(i);
            ContentValues value = new ContentValues();
            value.put(DBContract.MovieEntry._ID, movie.getId());
            value.put(DBContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            value.put(DBContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            value.put(DBContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
            value.put(DBContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
            value.put(DBContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
            if(localData!=null && localData.isFavorite()) {
                value.put(DBContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
            } else {
                value.put(DBContract.MovieEntry.COLUMN_IS_FAVORITE, false);
            }
            values[i] = value;
        }
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.bulkInsert(DBContract.MovieEntry.CONTENT_URI, values);

        return apiResponse;
    }

}
