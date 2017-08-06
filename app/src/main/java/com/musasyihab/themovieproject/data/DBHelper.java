package com.musasyihab.themovieproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by musasyihab on 8/5/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorite.db";

    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE =

                "CREATE TABLE " + DBContract.FavoriteEntry.TABLE_NAME + " (" +
                        DBContract.FavoriteEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        DBContract.FavoriteEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
