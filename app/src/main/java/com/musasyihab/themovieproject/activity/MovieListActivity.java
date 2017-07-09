package com.musasyihab.themovieproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.musasyihab.themovieproject.R;
import com.musasyihab.themovieproject.adapter.MovieAdapter;
import com.musasyihab.themovieproject.model.MovieModel;
import com.musasyihab.themovieproject.model.response.GetMovieList;
import com.musasyihab.themovieproject.util.Constants;
import com.musasyihab.themovieproject.util.NetworkUtils;

import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MovieListActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickListener {

    private ProgressBar mLoadingIndicator;
    private RecyclerView mMovieList;
    private TextView mMovieEmpty;
    private int sortMode = Constants.SORT_BY_POPULAR;;
    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.movie_list_loading);
        mMovieList = (RecyclerView) findViewById(R.id.movie_list_view);
        mMovieEmpty = (TextView) findViewById(R.id.movie_list_empty);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        mMovieList.setLayoutManager(layoutManager);
        adapter = new MovieAdapter(this, Collections.EMPTY_LIST, this);

        getMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.sort_popular:
                sortMode = Constants.SORT_BY_POPULAR;
                getMovies();
                break;
            case R.id.sort_top_rated:
                sortMode = Constants.SORT_BY_RATE;
                getMovies();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(MovieModel selectedMovie) {
        // go to detail
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(MovieDetailActivity.MOVIE_ID, selectedMovie.getId());
        startActivity(intentToStartDetailActivity);
    }

    private void getMovies(){
        new TaskGetMovies().execute();
    }

    private void loadMoviesToView(List<MovieModel> movieList){
        adapter = new MovieAdapter(this, movieList, this);
        mMovieList.setAdapter(adapter);
    }

    public class TaskGetMovies extends AsyncTask<Void, Void, GetMovieList> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMovieList.setVisibility(View.GONE);
            mMovieEmpty.setVisibility(View.GONE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected GetMovieList doInBackground(Void... params) {

            URL requestUrl = NetworkUtils.callMovieListAPI(sortMode);

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(requestUrl);

                JsonParser parser = new JsonParser();
                JsonElement mJson =  parser.parse(jsonResponse);

                Gson gson = new Gson();
                GetMovieList movieList = gson.fromJson(mJson, GetMovieList.class);

                return movieList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(GetMovieList movieListResponse) {
            mLoadingIndicator.setVisibility(View.GONE);
            if(movieListResponse==null){
                mMovieEmpty.setVisibility(View.VISIBLE);
                return;
            }
            List movieList = movieListResponse.getResults();
            if(movieList.isEmpty()){
                mMovieEmpty.setVisibility(View.VISIBLE);
                mMovieList.setVisibility(View.GONE);
                return;
            }
            mMovieEmpty.setVisibility(View.GONE);
            mMovieList.setVisibility(View.VISIBLE);

            String jsonRes = new Gson().toJson(movieList);
            Log.d("jsonRes", jsonRes);
            loadMoviesToView(movieListResponse.getResults());
        }
    }
}
