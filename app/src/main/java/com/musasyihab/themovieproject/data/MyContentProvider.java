package com.musasyihab.themovieproject.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;


/**
 * Created by musasyihab on 8/7/17.
 */

public class MyContentProvider extends ContentProvider {

    public static final int MOVIE = 102;
    public static final int MOVIE_WITH_ID = 103;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    private DBHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new DBHelper(getContext());
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE:
                long idMov = db.insert(DBContract.MovieEntry.TABLE_NAME, null, values);
                if ( idMov > 0 ) {
                    returnUri = ContentUris.withAppendedId(DBContract.MovieEntry.CONTENT_URI, idMov);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case MOVIE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(DBContract.MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case MOVIE:
                retCursor =  db.query(DBContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_ID:
                int movieId = Integer.parseInt(uri.getPathSegments().get(1));

                String whereClause = DBContract.MovieEntry._ID+"=?";
                String [] whereArgs = {movieId + ""};

                retCursor = db.query(
                        DBContract.MovieEntry.TABLE_NAME,
                        DBContract.MovieEntry.PROJECTION,
                        whereClause,
                        whereArgs,
                        null,
                        null,
                        null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int favDeleted;

        switch (match) {
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                favDeleted = db.delete(DBContract.MovieEntry.TABLE_NAME,
                        DBContract.MovieEntry._ID+"=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return favDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int retCursor;

        switch (match) {
            case MOVIE_WITH_ID:
                int movieId = Integer.parseInt(uri.getPathSegments().get(1));

                String whereClause = DBContract.MovieEntry._ID+"=?";
                String [] whereArgs = {movieId + ""};

                retCursor = db.updateWithOnConflict(
                        DBContract.MovieEntry.TABLE_NAME,
                        values,
                        whereClause,
                        whereArgs,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return retCursor;
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
