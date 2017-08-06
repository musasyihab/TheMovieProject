package com.musasyihab.themovieproject.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by musasyihab on 8/6/17.
 */

public class DBContract {

    public static final String CONTENT_AUTHORITY = "com.musasyihab.themovieproject";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE = "favorite";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildFavoriteUri() {
            return CONTENT_URI.buildUpon()
                    .build();
        }
    }
}
