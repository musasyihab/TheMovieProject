package com.musasyihab.themovieproject.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by musasyihab on 8/6/17.
 */

public class DBContract {

    public static final String CONTENT_AUTHORITY = "com.musasyihab.themovieproject";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // public static final String PATH_FAVORITE = "favorite";

    public static final String PATH_MOVIE = "movie";

    /*public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildFavoriteUri() {
            return CONTENT_URI.buildUpon()
                    .build();
        }
    }*/

    public static final class MovieEntry implements BaseColumns {

        public static final String[] PROJECTION = {
                DBContract.MovieEntry._ID,
                DBContract.MovieEntry.COLUMN_TITLE,
                DBContract.MovieEntry.COLUMN_OVERVIEW,
                DBContract.MovieEntry.COLUMN_POSTER_PATH,
                DBContract.MovieEntry.COLUMN_RELEASE_DATE,
                DBContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                DBContract.MovieEntry.COLUMN_IS_FAVORITE
        };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static Uri buildMovieUri() {
            return CONTENT_URI.buildUpon()
                    .build();
        }
    }
}
