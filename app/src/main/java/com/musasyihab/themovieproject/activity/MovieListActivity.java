package com.musasyihab.themovieproject.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.musasyihab.themovieproject.R;
import com.musasyihab.themovieproject.adapter.MovieAdapter;
import com.musasyihab.themovieproject.model.MovieModel;
import com.musasyihab.themovieproject.model.response.GetMovieListResponse;
import com.musasyihab.themovieproject.network.TaskGetFavoriteMovies;
import com.musasyihab.themovieproject.network.TaskGetMovieList;
import com.musasyihab.themovieproject.util.Constants;
import java.util.Collections;
import java.util.List;

public class MovieListActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickListener,
        LoaderManager.LoaderCallbacks<GetMovieListResponse> {

    private ProgressBar mLoadingIndicator;
    private RecyclerView mMovieList;
    private TextView mMovieEmpty;
    private int sortMode = Constants.SORT_BY_POPULAR;;
    private MovieAdapter adapter;
    private Loader<GetMovieListResponse> mLoaderList;
    private Loader<List<MovieModel>> mLoaderFavs;
    private LoaderManager mLoaderManager;
    private ActionBar mActionBar;

    private final int loaderListId = Constants.GET_MOVIE_LIST_LOADER;
    private final int loaderFavId = Constants.GET_MOVIE_FAVS_LOADER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.movie_list_loading);
        mMovieList = (RecyclerView) findViewById(R.id.movie_list_view);
        mMovieEmpty = (TextView) findViewById(R.id.movie_list_empty);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int column = 2;

        if(isLandscape){
            column = 3;
        }

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, column);

        mMovieList.setLayoutManager(layoutManager);
        adapter = new MovieAdapter(this, Collections.EMPTY_LIST, this);

        mActionBar = getSupportActionBar();
        if(mActionBar!=null){
            mActionBar.setTitle(getString(R.string.movie_list) + " " + getString(R.string.popular));
        }

        getMovies();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLoaderManager != null){
            if(mLoaderManager.getLoader(loaderListId)!=null)
                mLoaderManager.destroyLoader(loaderListId);
            else if (mLoaderManager.getLoader(loaderFavId)!=null)
                mLoaderManager.destroyLoader(loaderFavId);
        }
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
                if(sortMode==Constants.SORT_BY_POPULAR) break;

                sortMode = Constants.SORT_BY_POPULAR;
                if(mActionBar!=null){
                    mActionBar.setTitle(getString(R.string.movie_list) + " " + getString(R.string.popular));
                }
                getMovies();
                break;
            case R.id.sort_top_rated:
                if(sortMode==Constants.SORT_BY_RATE) break;

                sortMode = Constants.SORT_BY_RATE;
                if(mActionBar!=null){
                    mActionBar.setTitle(getString(R.string.movie_list) + " " + getString(R.string.top_rated));
                }
                getMovies();
                break;
            case R.id.sort_favorite:
                if(sortMode==Constants.SORT_BY_FAV) break;

                sortMode = Constants.SORT_BY_FAV;
                if(mActionBar!=null){
                    mActionBar.setTitle(getString(R.string.movie_list) + " " + getString(R.string.favorite_movies));
                }
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

    // Call loader to fecth movie list from API
    private void getMovies(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mMovieList.setVisibility(View.GONE);
        mMovieEmpty.setVisibility(View.GONE);
        if(sortMode!=Constants.SORT_BY_FAV) {
            if (mLoaderList == null) {
                mLoaderManager = getSupportLoaderManager();
                mLoaderList = mLoaderManager.getLoader(loaderListId);
                mLoaderManager.initLoader(loaderListId, null, this).forceLoad();
            } else {
                mLoaderManager.restartLoader(loaderListId, null, this).forceLoad();
            }
        } else {
            if (mLoaderFavs == null) {
                mLoaderManager = getSupportLoaderManager();
                mLoaderFavs = mLoaderManager.getLoader(loaderFavId);
                mLoaderManager.initLoader(loaderFavId, null, this).forceLoad();
            } else {
                mLoaderManager.restartLoader(loaderFavId, null, this).forceLoad();
            }
        }
    }

    // load fetched data into the view
    private void loadMoviesToView(List<MovieModel> movieList){
        if(movieList==null || movieList.isEmpty()){
            mMovieEmpty.setVisibility(View.VISIBLE);
            mMovieList.setVisibility(View.GONE);
            return;
        }
        mMovieEmpty.setVisibility(View.GONE);
        mMovieList.setVisibility(View.VISIBLE);
        adapter = new MovieAdapter(this, movieList, this);
        mMovieList.setAdapter(adapter);

        if(mLoaderManager != null){
            if(mLoaderManager.getLoader(loaderListId)!=null)
                mLoaderManager.destroyLoader(loaderListId);
            else if (mLoaderManager.getLoader(loaderFavId)!=null)
                mLoaderManager.destroyLoader(loaderFavId);
        }
    }

    //---------<LOADER CALLBACKS>---------

    @Override
    public Loader<GetMovieListResponse> onCreateLoader(int id, Bundle args) {
        switch (id){
            case loaderListId:
                return new TaskGetMovieList(this, sortMode);
            case loaderFavId:
                return new TaskGetFavoriteMovies(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<GetMovieListResponse> loader, GetMovieListResponse data) {
        mLoadingIndicator.setVisibility(View.GONE);
        loadMoviesToView(data.getResults());
    }

    @Override
    public void onLoaderReset(Loader<GetMovieListResponse> loader) {

    }

    //--------</LOADER CALLBACKS>---------

}
