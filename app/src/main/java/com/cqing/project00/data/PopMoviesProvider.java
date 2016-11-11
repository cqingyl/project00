package com.cqing.project00.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


/**
 * Created by Cqing on 2016/10/21.
 */

public class PopMoviesProvider extends ContentProvider {


    private final static UriMatcher sUriMatcher = buildUriMatcher();
    static final int POPMOVIE = 100;
    static final int REVIEW = 101;
    static final int VIDEO = 102;

    static final int MOVIE_WITH_MOVIE_ID = 103;
    static final int MOVIE_WITH_MOVIE_ID_WITH_REVIEWS = 104;
    static final int MOVIE_WITH_MOVIE_ID_WITH_VIDEOS = 105;

    private PopMoviesDbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sqLiteQueryBuilder;

    static {
        sqLiteQueryBuilder = new SQLiteQueryBuilder();
        //(movie INNER JOIN review ON movie.review_id = review._id) INNER JOIN video ON movie.video_id = video._id
        sqLiteQueryBuilder.setTables(
                "(" +
                PopMoviesContract.PopMoviesEntry.TABLE_NAME + " INNER JOIN " +
                        PopMoviesContract.ReviewEntry.TABLE_NAME +
                        " ON " + PopMoviesContract.PopMoviesEntry.TABLE_NAME +
                        "." + PopMoviesContract.PopMoviesEntry.COLUMN_REVIEW_KEY +
                        " = " + PopMoviesContract.ReviewEntry.TABLE_NAME +
                        "." +PopMoviesContract.ReviewEntry._ID +
                        ") INNER JOIN " + PopMoviesContract.VideoEntry.TABLE_NAME +
                        " ON " +
                        PopMoviesContract.PopMoviesEntry.TABLE_NAME + "." +
                        PopMoviesContract.PopMoviesEntry.COLUMN_VIDEO_KEY +
                        " = " + PopMoviesContract.VideoEntry.TABLE_NAME +
                        "." + PopMoviesContract.VideoEntry._ID
        );
    }
    //movie.id = ?
    private static String sMovieIdSelection =
            PopMoviesContract.PopMoviesEntry.TABLE_NAME + "." + PopMoviesContract.PopMoviesEntry.COLUMN_ID + " = ? ";
    //movie.id = ? And reviews.id = ?
    private static String sMovieIdAndReviewIdSelection =
            PopMoviesContract.PopMoviesEntry.TABLE_NAME + "." + PopMoviesContract.PopMoviesEntry.COLUMN_ID + " = ? And " +
                    PopMoviesContract.ReviewEntry.TABLE_NAME + "." + PopMoviesContract.ReviewEntry.COLUMN_REVIEW_ID + " = ?";
    //movie.id = ? And videos.id = ?
    private static String sMovieIdAndVideoIdSelection =
            PopMoviesContract.PopMoviesEntry.TABLE_NAME + "." + PopMoviesContract.PopMoviesEntry.COLUMN_ID + " = ? And " +
                    PopMoviesContract.VideoEntry.TABLE_NAME + "." + PopMoviesContract.VideoEntry.CONTENT_VIDEO_ID + " = ?";
    //当uri为"movie/*"时
    private Cursor getMovieByMovieId (Uri uri, String[] projection, String sortOrder) {
        Long  movieId = PopMoviesContract.PopMoviesEntry.getMovieId(uri);
        String selection;
        String []selectionArgs;
        selection = sMovieIdSelection;
        selectionArgs = new String[] {String.valueOf(movieId)};
        return sqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }
    //当uri为"movie/*/reviews"时
    private Cursor getMovieByMovieIdAndReview (Uri uri, String[] projection, String sortOrder) {
        Long  movieId = PopMoviesContract.PopMoviesEntry.getMovieId(uri);
        String selection;
        String []selectionArgs;
        selection = sMovieIdAndReviewIdSelection;
        selectionArgs = new String[] {String.valueOf(movieId)};
        return sqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }
    //当uri为"movie/*/videos"时
    private Cursor getMovieByMovieIdAndVideo (Uri uri, String[] projection, String sortOrder) {
        Long  movieId = PopMoviesContract.PopMoviesEntry.getMovieId(uri);
        String selection;
        String []selectionArgs;
        selection = sMovieIdAndVideoIdSelection;
        selectionArgs = new String[] {String.valueOf(movieId)};
        return sqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }
    static UriMatcher buildUriMatcher () {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopMoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE, POPMOVIE);
        matcher.addURI(authority, PopMoviesContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, PopMoviesContract.PATH_VIDEO, VIDEO);
        //"movie/789159"
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE + "/*" ,MOVIE_WITH_MOVIE_ID);
        //"movie/789159/reviews"
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE + "/*" + PopMoviesContract.ReviewEntry.TABLE_NAME,MOVIE_WITH_MOVIE_ID_WITH_REVIEWS);
        //"movie/789159/videos"
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE + "/*" + PopMoviesContract.VideoEntry.TABLE_NAME,MOVIE_WITH_MOVIE_ID_WITH_VIDEOS);
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
            case REVIEW :
                return PopMoviesContract.ReviewEntry.CONTENT_TYPE;
            case VIDEO :
                return PopMoviesContract.VideoEntry.CONTENT_TYPE;
            case MOVIE_WITH_MOVIE_ID :
                return PopMoviesContract.PopMoviesEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_MOVIE_ID_WITH_REVIEWS :
                return PopMoviesContract.PopMoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_MOVIE_ID_WITH_VIDEOS :
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
            // "reviews"
            case REVIEW :
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopMoviesContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // "videos"
            case VIDEO :
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopMoviesContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // "movie/*"
            case MOVIE_WITH_MOVIE_ID :
                retCursor = getMovieByMovieId(uri, projection, sortOrder);
                break;
            // "movie/*/review"
            case MOVIE_WITH_MOVIE_ID_WITH_REVIEWS :
                retCursor = getMovieByMovieIdAndReview(uri, projection, sortOrder);
                break;
            // "movie/*/video"
            case MOVIE_WITH_MOVIE_ID_WITH_VIDEOS :
                retCursor = getMovieByMovieIdAndVideo(uri, projection, sortOrder);
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
