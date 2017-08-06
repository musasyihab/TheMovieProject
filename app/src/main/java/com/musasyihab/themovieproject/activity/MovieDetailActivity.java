package com.musasyihab.themovieproject.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.musasyihab.themovieproject.R;
import com.musasyihab.themovieproject.adapter.ReviewAdapter;
import com.musasyihab.themovieproject.adapter.VideoPagerAdapter;
import com.musasyihab.themovieproject.data.DBContract;
import com.musasyihab.themovieproject.model.MovieModel;
import com.musasyihab.themovieproject.model.ReviewModel;
import com.musasyihab.themovieproject.model.VideoModel;
import com.musasyihab.themovieproject.model.response.GetMovieReviewsResponse;
import com.musasyihab.themovieproject.model.response.GetMovieVideosResponse;
import com.musasyihab.themovieproject.network.TaskGetMovieDetail;
import com.musasyihab.themovieproject.network.TaskGetMovieReviews;
import com.musasyihab.themovieproject.network.TaskGetMovieVideos;
import com.musasyihab.themovieproject.util.Constants;
import com.musasyihab.themovieproject.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by musasyihab on 7/9/17.
 */

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    public static final String MOVIE_ID = "MOVIE_ID";

    private ProgressBar mLoadingIndicator;
    private ProgressBar mLoadingReview;
    private NestedScrollView mMovieLayout;
    private TextView mError;
    private TextView mMovieTitle;
    private TextView mMovieYear;
    private TextView mMovieRelease;
    private TextView mMovieVote;
    private TextView mMovieSynopsis;
    private TextView mReviewEmpty;
    private ImageView mMoviePoster;
    private ViewPager mVideoPager;
    private RecyclerView mReviewList;
    private int movieId;
    private MovieModel movie;
    private boolean isFavorite;
    private List<VideoModel> videoList = new ArrayList<>();
    private List<ReviewModel> reviewList = new ArrayList<>();
    private Loader<MovieModel> mLoaderDetail;
    private Loader<GetMovieVideosResponse> mLoaderVideos;
    private LoaderManager mLoaderManager;
    private ActionBar mActionBar;
    private MenuItem favoriteMenu;
    private ReviewAdapter reviewAdapter;

    private final int loaderGetDetailId = Constants.GET_MOVIE_DETAIL_LOADER;
    private final int loaderGetVideosId = Constants.GET_MOVIE_VIDEOS_LOADER;
    private final int loaderGetReviewsId = Constants.GET_MOVIE_REVIEWS_LOADER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.movie_detail_loading);
        mLoadingReview = (ProgressBar) findViewById(R.id.movie_detail_review_loading);
        mMovieLayout = (NestedScrollView) findViewById(R.id.movie_detail_layout);
        mError = (TextView) findViewById(R.id.movie_detail_error);
        mMovieTitle = (TextView) findViewById(R.id.movie_detail_title);
        mMovieYear = (TextView) findViewById(R.id.movie_detail_year);
        mMovieRelease = (TextView) findViewById(R.id.movie_detail_release);
        mMovieVote = (TextView) findViewById(R.id.movie_detail_vote);
        mMovieSynopsis = (TextView) findViewById(R.id.movie_detail_synopsis);
        mReviewEmpty = (TextView) findViewById(R.id.movie_detail_review_empty);
        mMoviePoster = (ImageView) findViewById(R.id.movie_detail_poster);
        mVideoPager = (ViewPager) findViewById(R.id.movie_detail_videos_pager);
        mReviewList = (RecyclerView) findViewById(R.id.movie_detail_review_list);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(MOVIE_ID)) {
                movieId = intentThatStartedThisActivity.getIntExtra(MOVIE_ID, 0);
                getMovieDetail();
                return;
            }
        }

        mActionBar = getSupportActionBar();

        getMovieDetail();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLoaderManager != null){
            if(mLoaderManager.getLoader(loaderGetDetailId)!=null)
                mLoaderManager.destroyLoader(loaderGetDetailId);
            else if (mLoaderManager.getLoader(loaderGetVideosId)!=null)
                mLoaderManager.destroyLoader(loaderGetVideosId);
            else if (mLoaderManager.getLoader(loaderGetReviewsId)!=null)
                mLoaderManager.destroyLoader(loaderGetVideosId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        MenuItem item = menu.findItem(R.id.favorite);
        favoriteMenu = item;
        setupFavorite();
        /* Initialize menu */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.favorite:
                isFavorite = !isFavorite;
                if(isFavorite){
                    addToFavorite();
                } else {
                    removeFromFavorite();
                }
                setupFavorite();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addToFavorite(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);
        Uri uri = getContentResolver().insert(DBContract.FavoriteEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            Toast.makeText(this, movie.getTitle()+" "+getString(R.string.added_favorite), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromFavorite(){
        Uri uri = DBContract.FavoriteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId + "").build();

        if(uri != null) {
            Toast.makeText(this, movie.getTitle()+" "+getString(R.string.removed_favorite), Toast.LENGTH_SHORT).show();
        }

        getContentResolver().delete(uri, null, null);
    }

    private void setupFavorite(){
        if(favoriteMenu!=null) {
            if (isFavorite) {
                favoriteMenu.setIcon(getResources().getDrawable(R.drawable.ic_star_on));
            } else {
                favoriteMenu.setIcon(getResources().getDrawable(R.drawable.ic_star_off));
            }
        }
    }

    private void getMovieDetail(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mMovieLayout.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mLoaderManager = getSupportLoaderManager();
        if (mLoaderDetail == null) {
            mLoaderDetail = mLoaderManager.getLoader(loaderGetDetailId);
            mLoaderManager.initLoader(loaderGetDetailId, null, this).forceLoad();
        } else {
            mLoaderManager.restartLoader(loaderGetDetailId, null, this).forceLoad();
        }
    }

    private void getMovieVideos(){
        mLoaderManager = getSupportLoaderManager();
        if (mLoaderVideos == null) {
            mLoaderVideos = mLoaderManager.getLoader(loaderGetVideosId);
            mLoaderManager.initLoader(loaderGetVideosId, null, this).forceLoad();
        } else {
            mLoaderManager.restartLoader(loaderGetVideosId, null, this).forceLoad();
        }
    }

    private void getMovieReviews(){
        mLoadingReview.setVisibility(View.VISIBLE);
        mReviewEmpty.setVisibility(View.GONE);
        mReviewList.setVisibility(View.GONE);
        mLoaderManager = getSupportLoaderManager();
        if (mLoaderVideos == null) {
            mLoaderVideos = mLoaderManager.getLoader(loaderGetReviewsId);
            mLoaderManager.initLoader(loaderGetReviewsId, null, this).forceLoad();
        } else {
            mLoaderManager.restartLoader(loaderGetReviewsId, null, this).forceLoad();
        }
    }

    private void loadMoviesToView(){
        if(movie==null){
            mMovieLayout.setVisibility(View.GONE);
            mError.setVisibility(View.VISIBLE);
            return;
        }
        mMovieLayout.setVisibility(View.VISIBLE);
        mError.setVisibility(View.GONE);

        mMovieTitle.setText(movie.getTitle());
        mMovieYear.setText(Helper.getYear(movie.getRelease_date()));
        mMovieRelease.setText(getString(R.string.releae_date)+" "+Helper.printDate(movie.getRelease_date()));
        mMovieVote.setText(getString(R.string.vote_average)+" "+movie.getVote_average());
        mMovieSynopsis.setText(movie.getOverview());

        String poster = Constants.POSTER_PATH + movie.getPoster_path();

        Glide.with(this).load(poster).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).into(mMoviePoster);

        if(mActionBar!=null){
            mActionBar.setTitle(movie.getTitle());
        }

        if(mLoaderManager != null) {
            mLoaderManager.destroyLoader(loaderGetDetailId);
        }
        isFavorite = movie.isFavorite();

        setupFavorite();

        getMovieVideos();
    }

    private void initVideosPager(){
        LayoutInflater mInflater = LayoutInflater.from(this);
        PagerAdapter viewPagerAdapter = new VideoPagerAdapter(MovieDetailActivity.this, videoList, mInflater);
        mVideoPager.setAdapter(viewPagerAdapter);
        mVideoPager.setPadding(getResources().getDimensionPixelOffset(R.dimen.video_pager_padding), 0, getResources().getDimensionPixelOffset(R.dimen.video_pager_padding), 0);
        mVideoPager.setClipToPadding(false);
        mVideoPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.video_pager_margin));

        getMovieReviews();
    }

    private void initReviews(){
        mLoadingReview.setVisibility(View.GONE);
        if(reviewList.size()>0){
            mReviewEmpty.setVisibility(View.GONE);
            mReviewList.setVisibility(View.VISIBLE);
            reviewAdapter = new ReviewAdapter(MovieDetailActivity.this, reviewList);

            mReviewList.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this));
            mReviewList.setAdapter(reviewAdapter);
            mReviewList.setNestedScrollingEnabled(false);
        } else {
            mReviewEmpty.setVisibility(View.VISIBLE);
            mReviewList.setVisibility(View.GONE);
        }
    }

    //---------<LOADER CALLBACKS>---------

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id){
            case loaderGetDetailId:
                return new TaskGetMovieDetail(this, movieId);
            case loaderGetVideosId:
                return new TaskGetMovieVideos(this, movieId);
            case loaderGetReviewsId:
                return new TaskGetMovieReviews(this, movieId);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()){
            case loaderGetDetailId:
                mLoadingIndicator.setVisibility(View.GONE);
                movie = (MovieModel) data;
                loadMoviesToView();
                break;
            case loaderGetVideosId:
                GetMovieVideosResponse response = (GetMovieVideosResponse) data;
                videoList = response.getResults();
                initVideosPager();
                break;
            case loaderGetReviewsId:
                GetMovieReviewsResponse response2 = (GetMovieReviewsResponse) data;
                reviewList = response2.getResults();
                initReviews();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    //--------</LOADER CALLBACKS>---------
}
