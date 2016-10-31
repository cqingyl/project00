package com.cqing.project00.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


/**
 * Created by Cqing on 2016/10/21.
 */

public class PopMoviesProvider extends ContentProvider {


    private final static UriMatcher sUriMatcher = buildUriMatcher();
    static final int POPMOVIE = 100;
    static final int POPMOVIE_ID = 101;

    private PopMoviesDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher () {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopMoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE, POPMOVIE);
        //matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE + "/#" ,POPMOVIE_ID);
        return matcher;

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PopMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POPMOVIE :
                return PopMoviesContract.PopMoviesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie"
            case POPMOVIE :
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopMoviesContract.PopMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)){
            case POPMOVIE: {
                long _id = db.insert(PopMoviesContract.PopMoviesEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PopMoviesContract.PopMoviesEntry.buildPopMoviesUri(_id);
                } else {
                    throw new SQLException("Fail to insert row into " + uri);
                }
                break;
            }
            default :
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int rowsDeleted;
        if (null == selection){
            selection = "1";
        }
        switch (sUriMatcher.match(uri)){
            case POPMOVIE:
                rowsDeleted = db.delete(PopMoviesContract.PopMoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdate;
        switch (sUriMatcher.match(uri)){
            case POPMOVIE:
                rowUpdate = db.update(PopMoviesContract.PopMoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowUpdate != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdate;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
