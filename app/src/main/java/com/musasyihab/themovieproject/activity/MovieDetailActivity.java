package com.musasyihab.themovieproject.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.musasyihab.themovieproject.R;
import com.musasyihab.themovieproject.model.MovieModel;
import com.musasyihab.themovieproject.util.Constants;
import com.musasyihab.themovieproject.util.Helper;
import com.musasyihab.themovieproject.util.NetworkUtils;

import java.net.URL;

/**
 * Created by musasyihab on 7/9/17.
 */

public class MovieDetailActivity extends AppCompatActivity {
    public static final String MOVIE_ID = "MOVIE_ID";

    private ProgressBar mLoadingIndicator;
    private ScrollView mMovieLayout;
    private TextView mError;
    private TextView mMovieTitle;
    private TextView mMovieYear;
    private TextView mMovieRelease;
    private TextView mMovieVote;
    private TextView mMovieSynopsis;
    private ImageView mMoviePoster;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.movie_detail_loading);
        mMovieLayout = (ScrollView) findViewById(R.id.movie_detail_layout);
        mError = (TextView) findViewById(R.id.movie_detail_error);
        mMovieTitle = (TextView) findViewById(R.id.movie_detail_title);
        mMovieYear = (TextView) findViewById(R.id.movie_detail_year);
        mMovieRelease = (TextView) findViewById(R.id.movie_detail_release);
        mMovieVote = (TextView) findViewById(R.id.movie_detail_vote);
        mMovieSynopsis = (TextView) findViewById(R.id.movie_detail_synopsis);
        mMoviePoster = (ImageView) findViewById(R.id.movie_detail_poster);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(MOVIE_ID)) {
                movieId = intentThatStartedThisActivity.getIntExtra(MOVIE_ID, 0);
                getMovieDetail();
                return;
            }
        }

        mMovieLayout.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.GONE);
        mError.setVisibility(View.VISIBLE);

    }

    private void getMovieDetail(){
        new TaskGetMovieDetail().execute();
    }

    private void loadMoviesToView(MovieModel selectedMovie){
        mMovieTitle.setText(selectedMovie.getTitle());
        mMovieYear.setText(Helper.getYear(selectedMovie.getRelease_date()));
        mMovieRelease.setText(getString(R.string.releae_date)+" "+Helper.printDate(selectedMovie.getRelease_date()));
        mMovieVote.setText(getString(R.string.vote_average)+" "+selectedMovie.getVote_average());
        mMovieSynopsis.setText(selectedMovie.getOverview());

        String poster = Constants.POSTER_PATH + selectedMovie.getPoster_path();

        Glide.with(this).load(poster).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).into(mMoviePoster);
    }

    public class TaskGetMovieDetail extends AsyncTask<Void, Void, MovieModel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mMovieLayout.setVisibility(View.GONE);
        }

        @Override
        protected MovieModel doInBackground(Void... params) {

            URL requestUrl = NetworkUtils.callMovieDetailAPI(movieId);

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(requestUrl);

                JsonParser parser = new JsonParser();
                JsonElement mJson =  parser.parse(jsonResponse);

                Gson gson = new Gson();
                MovieModel movieModel = gson.fromJson(mJson, MovieModel.class);

                return movieModel;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieModel movieResponse) {
            mLoadingIndicator.setVisibility(View.GONE);
            if(movieResponse==null){
                mMovieLayout.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
                return;
            }
            mMovieLayout.setVisibility(View.VISIBLE);
            mError.setVisibility(View.GONE);
            String jsonRes = new Gson().toJson(movieResponse);
            Log.d("jsonRes", jsonRes);
            loadMoviesToView(movieResponse);
        }
    }
}
