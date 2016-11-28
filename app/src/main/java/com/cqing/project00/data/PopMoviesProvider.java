package com.cqing.project00.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;


/**
 * Created by Cqing on 2016/10/21.
 */

public class PopMoviesProvider extends ContentProvider {


    private final static UriMatcher sUriMatcher = buildUriMatcher();
    static final int POPMOVIE = 100;
    static final int REVIEW = 101;
    static final int VIDEO = 102;
    public static final String MERGE = "merge";

    static final int MOVIE_WITH_MOVIE_ID = 103;
    static final int MOVIE_WITH_MOVIE_ID_WITH_REVIEWS = 104;
    static final int MOVIE_WITH_MOVIE_ID_WITH_VIDEOS = 105;
    static final int MOVIE_WITH_MOVIE_ID_WITH_MERGE = 106;

    private PopMoviesDbHelper mOpenHelper;
    public static final SQLiteQueryBuilder sqLiteQueryBuilder;
    public static final SQLiteQueryBuilder movieWithReviewSqLiteQueryBuilder;
    public static final SQLiteQueryBuilder movieWithVideoSqLiteQueryBuilder;

    static {
        sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(PopMoviesContract.PopMoviesEntry.TABLE_NAME);
        movieWithReviewSqLiteQueryBuilder = new SQLiteQueryBuilder();
        movieWithReviewSqLiteQueryBuilder.setTables(PopMoviesContract.ReviewEntry.TABLE_NAME);
        movieWithVideoSqLiteQueryBuilder = new SQLiteQueryBuilder();
        movieWithVideoSqLiteQueryBuilder.setTables(PopMoviesContract.VideoEntry.TABLE_NAME);
//        movieWithReviewSqLiteQueryBuilder = new SQLiteQueryBuilder();
//        //select * from movie LEFT JOIN reviews ON movie.movie_id = reviews.review_movie_id
//        movieWithReviewSqLiteQueryBuilder.setTables(
//                PopMoviesContract.PopMoviesEntry.TABLE_NAME +
//                        " LEFT JOIN " + PopMoviesContract.ReviewEntry.TABLE_NAME +
//                        " ON " + PopMoviesContract.PopMoviesEntry.TABLE_NAME +
//                        "." + PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID +
//                        " = " + PopMoviesContract.ReviewEntry.TABLE_NAME +
//                        "." + PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID
//        );
//        movieWithVideoSqLiteQueryBuilder = new SQLiteQueryBuilder();
//        //select * from movie LEFT JOIN videos ON movie.movie_id = videos.video_movie_id
//        movieWithVideoSqLiteQueryBuilder.setTables(
//                PopMoviesContract.PopMoviesEntry.TABLE_NAME +
//                        " LEFT JOIN " + PopMoviesContract.VideoEntry.TABLE_NAME +
//                        " ON " + PopMoviesContract.PopMoviesEntry.TABLE_NAME +
//                        "." + PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID +
//                        " = " + PopMoviesContract.VideoEntry.TABLE_NAME +
//                        "." + PopMoviesContract.VideoEntry.COLUMN_MOVIE_ID
//        );
    }
    //movie.movie_id = ?
    private static String sMovieIdSelection =
            PopMoviesContract.PopMoviesEntry.TABLE_NAME + "." + PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID + " = ?";
    //videos.video_movie_id = ? AND videos.video_id = IS NOT NULL
    private String sMovieIdInVideoSelection =
            PopMoviesContract.VideoEntry.TABLE_NAME + "." + PopMoviesContract.VideoEntry.COLUMN_MOVIE_ID + " = ? AND " +
                    PopMoviesContract.VideoEntry.TABLE_NAME + "." + PopMoviesContract.VideoEntry.COLUMN_VIDEO_ID + " IS NOT NULL";

    //reviews.review_movie_id = ? AND reviews.review_id = IS NOT NULL
    private String sMovieIdInReviewSelection =
            PopMoviesContract.ReviewEntry.TABLE_NAME + "." + PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? AND " +
            PopMoviesContract.ReviewEntry.TABLE_NAME + "." + PopMoviesContract.ReviewEntry.COLUMN_REVIEW_ID + " IS NOT NULL";

    @Nullable
    private Cursor getMovieByMovieIdAndReviewOrVideo (Uri uri, String[] projection, String sortOrder, int path) {
        Long  movieId = PopMoviesContract.PopMoviesEntry.getMovieId(uri);
        String selection;
        String []selectionArgs;
        selectionArgs = new String[] {String.valueOf(movieId)};
        switch (path) {
            case MOVIE_WITH_MOVIE_ID :
                selection = sMovieIdSelection;
                return sqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
            case MOVIE_WITH_MOVIE_ID_WITH_REVIEWS :
                selection = sMovieIdInReviewSelection;
                return movieWithReviewSqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
            case MOVIE_WITH_MOVIE_ID_WITH_VIDEOS :
                selection = sMovieIdInVideoSelection;
                return movieWithVideoSqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    static UriMatcher buildUriMatcher () {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopMoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE, POPMOVIE);
        matcher.addURI(authority, PopMoviesContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, PopMoviesContract.PATH_VIDEO, VIDEO);
        //"movie/789159"
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE + "/*" , MOVIE_WITH_MOVIE_ID);
        //"movie/789159/reviews"
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE + "/*/" + PopMoviesContract.ReviewEntry.TABLE_NAME, MOVIE_WITH_MOVIE_ID_WITH_REVIEWS);
        //"movie/789159/videos"
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE + "/*/" + PopMoviesContract.VideoEntry.TABLE_NAME, MOVIE_WITH_MOVIE_ID_WITH_VIDEOS);
        //"movie/789159/merge"
        matcher.addURI(authority, PopMoviesContract.PATH_POPMOVIE + "/*/" + MERGE, MOVIE_WITH_MOVIE_ID_WITH_MERGE);
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
            case MOVIE_WITH_MOVIE_ID_WITH_MERGE :
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
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            // "reviews"
            case REVIEW :
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopMoviesContract.ReviewEntry.TABLE_NAME,
                        projection,
                        sMovieIdInReviewSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
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
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            // "movie/*"
            case MOVIE_WITH_MOVIE_ID :
                retCursor = getMovieByMovieIdAndReviewOrVideo(uri, projection, sortOrder, MOVIE_WITH_MOVIE_ID);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            // "movie/*/reviews"
            case MOVIE_WITH_MOVIE_ID_WITH_REVIEWS :
                retCursor = getMovieByMovieIdAndReviewOrVideo(uri, projection, sortOrder, MOVIE_WITH_MOVIE_ID_WITH_REVIEWS);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            // "movie/*/videos"
            case MOVIE_WITH_MOVIE_ID_WITH_VIDEOS :
                retCursor = getMovieByMovieIdAndReviewOrVideo(uri, projection, sortOrder, MOVIE_WITH_MOVIE_ID_WITH_VIDEOS);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            // "movie/*/merge"
            case MOVIE_WITH_MOVIE_ID_WITH_MERGE :
                // "movie/*"
                Cursor detailCursor = getMovieByMovieIdAndReviewOrVideo(uri, projection, sortOrder, MOVIE_WITH_MOVIE_ID);
                // "movie/*/reviews"
                Cursor reviewCursor = getMovieByMovieIdAndReviewOrVideo(uri, projection, sortOrder, MOVIE_WITH_MOVIE_ID_WITH_REVIEWS);
                // "movie/*/videos"
                Cursor videoCursor = getMovieByMovieIdAndReviewOrVideo(uri, projection, sortOrder, MOVIE_WITH_MOVIE_ID_WITH_VIDEOS);

                Cursor [] cursors = new Cursor[]{detailCursor, reviewCursor,videoCursor };
                MergeCursor mergeCursor = new MergeCursor(cursors);
                mergeCursor.setNotificationUri(getContext().getContentResolver(), uri);
                //当uri做出改变，mergeCursor 会获得相应通知。
                mergeCursor.setNotificationUri(getContext().getContentResolver(), PopMoviesContract.PopMoviesEntry.CONTENT_URI);
                return mergeCursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

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
            case REVIEW: {
                long _id = db.insert(PopMoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PopMoviesContract.ReviewEntry.buildPopMoviesReviewUri(_id);
                } else {
                    throw new SQLException("Fail to insert row into " + uri);
                }
                break;
            }
            case VIDEO: {
                long _id = db.insert(PopMoviesContract.VideoEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PopMoviesContract.VideoEntry.buildPopMoviesVideoUri(_id);
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
            case REVIEW:
                rowsDeleted = db.delete(PopMoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEO:
                rowsDeleted = db.delete(PopMoviesContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
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
//                getContext().getContentResolver().notifyChange(mergeUri, null);
                break;
            case REVIEW:
                rowUpdate = db.update(PopMoviesContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case VIDEO:
                rowUpdate = db.update(PopMoviesContract.VideoEntry.TABLE_NAME, values, selection, selectionArgs);
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
