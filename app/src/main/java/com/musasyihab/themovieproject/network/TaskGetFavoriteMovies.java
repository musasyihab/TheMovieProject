package com.musasyihab.themovieproject.network;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.musasyihab.themovieproject.data.DBContract;
import com.musasyihab.themovieproject.model.MovieModel;
import com.musasyihab.themovieproject.model.response.GetMovieListResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by musasyihab on 8/7/17.
 */

public class TaskGetFavoriteMovies extends AsyncTaskLoader<GetMovieListResponse> {

    private Context context;

    public TaskGetFavoriteMovies(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public GetMovieListResponse loadInBackground() {

        // get data from local storage
        GetMovieListResponse response = new GetMovieListResponse();
        List<MovieModel> movies = new ArrayList<>();

        Uri uri = DBContract.MovieEntry.CONTENT_URI;

        String whereClause = DBContract.MovieEntry.COLUMN_IS_FAVORITE+"=?";
        String [] whereArgs = {"1"};

        Cursor result = context.getContentResolver().query(uri, DBContract.MovieEntry.PROJECTION, whereClause, whereArgs, null);

        int colId = result.getColumnIndex(DBContract.MovieEntry._ID);
        int colTitle = result.getColumnIndex(DBContract.MovieEntry.COLUMN_TITLE);
        int colOverview = result.getColumnIndex(DBContract.MovieEntry.COLUMN_OVERVIEW);
        int colPoster = result.getColumnIndex(DBContract.MovieEntry.COLUMN_POSTER_PATH);
        int colRelease = result.getColumnIndex(DBContract.MovieEntry.COLUMN_RELEASE_DATE);
        int colVote = result.getColumnIndex(DBContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int colFavorite = result.getColumnIndex(DBContract.MovieEntry.COLUMN_IS_FAVORITE);

        // convert data from cursor to MovieModel
        while (result.moveToNext()){
            MovieModel movie = new MovieModel();
            movie.setId(result.getInt(colId));
            movie.setTitle(result.getString(colTitle));
            movie.setOverview(result.getString(colOverview));
            movie.setPoster_path(result.getString(colPoster));
            movie.setRelease_date(result.getString(colRelease));
            movie.setVote_average(result.getFloat(colVote));
            movie.setFavorite(result.getInt(colFavorite) > 0);

            movies.add(movie);
        }

        response.setResults(movies);

        return response;

    }

}