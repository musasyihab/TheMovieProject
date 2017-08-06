package com.musasyihab.themovieproject.util;

/**
 * Created by musasyihab on 7/9/17.
 */

public class Constants {
    public static final String APP_NAME = "TheMovieProject";
    public static final String API_PATH = "https://api.themoviedb.org/3/";
    public static final String API_KEY = Keys.API_KEY;
    public static final String API_KEY_PARAM = "api_key";
    public static final String SORT_POPULAR_ENDPOINT = "movie/popular";
    public static final String SORT_TOP_RATED_ENDPOINT = "movie/top_rated";
    public static final String MOVIE_DETAIL_ENDPOINT = "movie/";

    public static final String POSTER_PATH = "http://image.tmdb.org/t/p/w185";

    public static final int SORT_BY_POPULAR = 0;
    public static final int SORT_BY_RATE = 1;

    public static final int TIMEOUT = 10;


    public static final int GET_MOVIE_LIST_LOADER = 91;
    public static final int GET_MOVIE_DETAIL_LOADER = 92;
    public static final int GET_MOVIE_VIDEOS_LOADER = 93;
    public static final int GET_MOVIE_REVIEWS_LOADER = 94;
}
