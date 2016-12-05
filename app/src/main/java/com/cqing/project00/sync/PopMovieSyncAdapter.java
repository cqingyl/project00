package com.cqing.project00.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.cqing.project00.BuildConfig;
import com.cqing.project00.R;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.url.MovieUrl;
import com.cqing.project00.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Cqing on 2016/11/25.
 */

public class PopMovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private final static String LOG_TAG = PopMovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the movie, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private List<Integer> popMovieIdList;
    private static final int MOVIE_POPULAR_STATUS = 0;
    private static final int MOVIE_TOP_STATUS = 1;
    private ContentResolver contentResolver;

    public PopMovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        contentResolver = getContext().getContentResolver();
        popMovieIdList = new ArrayList<Integer>();
        final String MOVIE_BASE_URL = MovieUrl.HOST;
        final String POPULAR = "popular";
        final String TOP_RATED = "top_rated";
        final String REVIEW = "reviews";
        final String VIDEO = "videos";
        final String apiKey = "api_key";
        try {
            //http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
            Uri popMovieUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(POPULAR)
                    .appendQueryParameter(apiKey, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
            URL popMovieUrl = new URL(popMovieUri.toString());
            String popMoviesJsonStr = getJsonString(popMovieUrl);
            if (popMoviesJsonStr != null) {
                getMoviesDataFromJson(MOVIE_POPULAR_STATUS, popMoviesJsonStr);
            }
            //http://api.themoviedb.org/3/movie/top_rated?api_key=[YOUR_API_KEY]
            Uri topMovieUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(TOP_RATED)
                    .appendQueryParameter(apiKey, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
            URL topMovieUrl = new URL(topMovieUri.toString());
            String topMoviesJsonStr = getJsonString(topMovieUrl);
            if (topMoviesJsonStr != null) {
                getMoviesDataFromJson(MOVIE_TOP_STATUS, topMoviesJsonStr);
            }
            for (int i = 0; i < popMovieIdList.size(); i++) {
                //https://api.themoviedb.org/3/movie/157336/reviews?api_key=[YOUR_API_KEY]
                Uri reviewUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(String.valueOf(popMovieIdList.get(i)))
                        .appendPath(REVIEW)
                        .appendQueryParameter(apiKey, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
                URL reviewUrl = new URL(reviewUri.toString());
                String reviewsJsonStr = getJsonString(reviewUrl);
                if (reviewsJsonStr != null) {
                    getPopularMoviesReviewFromJson(reviewsJsonStr, String.valueOf(popMovieIdList.get(i)));
                }
                //https://api.themoviedb.org/3/movie/157336/videos?api_key=[YOUR_API_KEY]
                Uri videoUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(String.valueOf(popMovieIdList.get(i)))
                        .appendPath(VIDEO)
                        .appendQueryParameter(apiKey, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
                URL videoUrl = new URL(videoUri.toString());
                String videosJsonStr = getJsonString(videoUrl);
                if (videosJsonStr != null) {
                    getPopularMoviesVideoFromJson(videosJsonStr, String.valueOf(popMovieIdList.get(i)));
                }
            }
            Log.d(LOG_TAG, "sync Completed");
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }


    private String getJsonString(java.net.URL url) throws IOException {
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        if ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            return null;
        }
        return buffer.toString();
    }

    /**
     * Get Movie review
     */
    private void getPopularMoviesReviewFromJson(String reviewsJsonStr, String movieId) throws JSONException {
        final String RESULT = "results";
        JSONObject jsonObject = new JSONObject(reviewsJsonStr);
        JSONArray reviewArray = jsonObject.getJSONArray(RESULT);
        JSONObject reviewObject;
        Vector<ContentValues> cVector = new Vector<ContentValues>(reviewArray.length());
        for (int i = 0; i < reviewArray.length(); i++) {
            reviewObject = reviewArray.getJSONObject(i);
            final String REVIEW_ID = "id";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String REVIEW_URL = "url";
            String reviewId = reviewObject.getString(REVIEW_ID);
            String author = reviewObject.getString(AUTHOR);
            String content = reviewObject.getString(CONTENT);
            String url = reviewObject.getString(REVIEW_URL);
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_REVIEW_ID, reviewId);
            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_AUTHOR, author);
            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_CONTENT, content);
            reviewValues.put(PopMoviesContract.ReviewEntry.COLUMN_REVIEW_URL, url);
            cVector.add(reviewValues);
        }
        tableBulkInsert(cVector, PopMoviesContract.ReviewEntry.CONTENT_URI);
    }

    /**
     * Get Movie trailer
     */
    private void getPopularMoviesVideoFromJson(String videosJsonStr, String movieId) throws JSONException {
        final String RESULT = "results";
        JSONObject jsonObject = new JSONObject(videosJsonStr);
        JSONArray videoArray = jsonObject.getJSONArray(RESULT);
        JSONObject videoObject;
        Vector<ContentValues> cVector = new Vector<ContentValues>(videoArray.length());
        for (int i = 0; i < videoArray.length(); i++) {
            videoObject = videoArray.getJSONObject(i);
            final String VIDEO_ID = "id";
            final String KEY = "key";
            final String NAME = "name";
            final String SITE = "site";
            final String SIZE = "size";
            String videoId = videoObject.getString(VIDEO_ID);
            String key = videoObject.getString(KEY);
            String name = videoObject.getString(NAME);
            String site = videoObject.getString(SITE);
            String size = videoObject.getString(SIZE);
            ContentValues videoValues = new ContentValues();
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_VIDEO_ID, videoId);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_KEY, key);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_NAME, name);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_SITE, site);
            videoValues.put(PopMoviesContract.VideoEntry.COLUMN_SIZE, size);
            cVector.add(videoValues);
        }
        tableBulkInsert(cVector, PopMoviesContract.VideoEntry.CONTENT_URI);
    }

    /**
     * Get movie information
     *
     * @param status
     */
    private void getMoviesDataFromJson(int status, String moviesJsonStr) throws JSONException {

        final String RESULT = "results";
        JSONObject jsonObject = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = jsonObject.getJSONArray(RESULT);
        JSONObject moviesObject;

        Vector<ContentValues> cVector = new Vector<ContentValues>(moviesArray.length());

        String poster_path;
        int adult;
        String overview;
        String release_date;
        String original_title;
        String original_language;
        String title;
        String backdrop_path;
        double popularity;
        int vote_count;
        int video;
        int movieId;
        double vote_average;
        String genre_ids;
        int bothInPopAndTop;
        for (int i = 0; i < moviesArray.length(); i++) {
            moviesObject = moviesArray.getJSONObject(i);
            poster_path = getPoster_path(moviesObject);
            adult = getAdult(moviesObject);
            overview = getOverview(moviesObject);
            release_date = getReleaseDate(moviesObject);
            original_title = getOriginal_title(moviesObject);
            original_language = getOriginal_language(moviesObject);
            title = getTitle(moviesObject);
            movieId = getId(moviesObject);
            popMovieIdList.add(movieId);
            backdrop_path = getBackdrop_path(moviesObject);
            popularity = getPopularity(moviesObject);
            vote_count = getVote_count(moviesObject);
            video = getVideo(moviesObject);
            vote_average = getAverages(moviesObject);
            genre_ids = getGenre_ids(moviesObject);

            ContentValues movieValues = new ContentValues();
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_POSTER_PATH, poster_path);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_ADULT, adult);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_RELEASE_DATE, release_date);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_ORIGINAL_TITLE, original_title);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_ORIGINAL_LANGUAGE, original_language);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_TITLE, title);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_BACKDROP_PATH, backdrop_path);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_COUNT, vote_count);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_VIDEO, video);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_AVERAGE, vote_average);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_GENRE_IDS, genre_ids);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION, 1);
                //如果是popular就让表里的most_popular为0，如果是top就让表里的most_top为0，是top且如果 movieId在list里面，就让表popular和top都为0
                switch (status) {
                    case MOVIE_POPULAR_STATUS:
                        movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_MOST_POPULARITY, 0);
                        break;
                    case MOVIE_TOP_STATUS:
                        movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_MOST_TOP, 0);
                        break;
                }
            cVector.add(movieValues);
        }
        tableBulkInsert(cVector, PopMoviesContract.PopMoviesEntry.CONTENT_URI);
            bothInPopAndTop = isBothInPopAndTop(popMovieIdList);
        if (bothInPopAndTop != -1){
            Log.d(LOG_TAG, "bothInPopAndTop: " + bothInPopAndTop);
            ContentValues values = new ContentValues();
            values.put(PopMoviesContract.PopMoviesEntry.COLUMN_MOST_POPULARITY, 0);
            values.put(PopMoviesContract.PopMoviesEntry.COLUMN_MOST_TOP, 0);
            getContext().getContentResolver().update(PopMoviesContract.PopMoviesEntry.CONTENT_URI,
                    values,
                    PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{String.valueOf(bothInPopAndTop)}
                    );
        }
    }

    public int isBothInPopAndTop(List list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))){
                    return (int) list.get(i);
                }
            }
        }
        return -1;
    }
    private boolean isMovieIdExits(int movieId) {
        Cursor c = null;
        try {
            c = getContext().getContentResolver().query(PopMoviesContract.PopMoviesEntry.CONTENT_URI,
                    new String[]{PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID},
                    PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{String.valueOf(movieId)},
                    null
            );
            return c != null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    /**
     * 插入数据
     */
    private void tableBulkInsert(Vector<ContentValues> cVector, Uri uri) {
        int inserted = 0;
        if (cVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVector.size()];
            cVector.toArray(cvArray);
            inserted = contentResolver.bulkInsert(uri, cvArray);
        }
        Log.d(LOG_TAG, uri.getLastPathSegment() + " insert Completed. " + " Inserted: " + inserted);
    }

    /**
     * get poster path
     */
    private String getPoster_path(JSONObject moviesObject) throws JSONException {
        final String POSTER_PATH = "poster_path";
        String moviesPath;
        if (!moviesObject.isNull(POSTER_PATH)) {
            moviesPath = moviesObject.getString(POSTER_PATH);
            String baseUrl = MovieUrl.SERVICE_IMAGE_URL;
            String picSize = MovieUrl.SERVICE_IMAGE_URL_PIC_SIZE;
            return baseUrl.concat(picSize).concat(moviesPath);
        }
        return null;
    }

    /**
     * get overview
     */
    private String getOverview(JSONObject moviesObject) throws JSONException {

        final String OVER_VIEW = "overview";
        String moviesOverview;
        if (!moviesObject.isNull(OVER_VIEW)) {
            moviesOverview = moviesObject.getString(OVER_VIEW);
            return moviesOverview;
        }
        return null;
    }

    /**
     * get title
     */
    private String getTitle(JSONObject moviesObject) throws JSONException {
        final String TITLE = "title";
        if (!moviesObject.isNull(TITLE)) {
            String title;
            title = moviesObject.getString(TITLE);
            return title;
        }
        return null;
    }

    /**
     * get original title
     */
    private String getOriginal_title(JSONObject moviesObject) throws JSONException {
        final String ORIGINAL_TITLE = "original_title";
        if (!moviesObject.isNull(ORIGINAL_TITLE)) {
            String original_title;
            original_title = moviesObject.getString(ORIGINAL_TITLE);
            return original_title;
        }
        return null;
    }

    /**
     * get date
     */
    private String getReleaseDate(JSONObject moviesObject) throws JSONException {
        final String RELEASE_DATE = "release_date";
        if (!moviesObject.isNull(RELEASE_DATE)) {
            String releaseDate;
            releaseDate = moviesObject.getString(RELEASE_DATE);
            return releaseDate;
        }
        return null;
    }

    /**
     * get popularity
     */
    private double getPopularity(JSONObject moviesObject) throws JSONException {
        final String POPULARITY = "popularity";
        if (!moviesObject.isNull(POPULARITY)) {
            Double popularity;
            popularity = Util.formatDouble(moviesObject.getDouble(POPULARITY));
            return popularity;
        }
        return -1;
    }

    /**
     * get averages
     */
    private double getAverages(JSONObject moviesObject) throws JSONException {
        final String VOTE_AVERAGE = "vote_average";
        if (!moviesObject.isNull(VOTE_AVERAGE)) {
            Double voteAverage;
            voteAverage = moviesObject.getDouble(VOTE_AVERAGE);
            return voteAverage;
        }
        return -1;
    }

    /**
     * get Adult
     */
    private int getAdult(JSONObject moviesObject) throws JSONException {
        final String ADULT = "adult";
        if (!moviesObject.isNull(ADULT)) {
            boolean adult;
            adult = moviesObject.getBoolean(ADULT);
            if (adult) {
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

    /**
     * get id
     */
    private int getId(JSONObject moviesObject) throws JSONException {
        final String ID = "id";
        if (!moviesObject.isNull(ID)) {
            int id;
            id = moviesObject.getInt(ID);
            return id;
        }
        return -1;
    }

    /**
     * get original language
     */
    private String getOriginal_language(JSONObject moviesObject) throws JSONException {
        final String ORIGINAL_LANGUAGE = "original_language";
        if (!moviesObject.isNull(ORIGINAL_LANGUAGE)) {
            String original_language;
            original_language = moviesObject.getString(ORIGINAL_LANGUAGE);
            return original_language;
        }
        return null;
    }

    /**
     * get backdrop path
     */
    private String getBackdrop_path(JSONObject moviesObject) throws JSONException {
        final String BACKDROP_PATH = "backdrop_path";
        String moviesPath;
        if (!moviesObject.isNull(BACKDROP_PATH)) {
            moviesPath = moviesObject.getString(BACKDROP_PATH);
            String baseUrl = MovieUrl.SERVICE_IMAGE_URL;
            String picSize = MovieUrl.SERVICE_IMAGE_URL_PIC_SIZE;
            return baseUrl.concat(picSize).concat(moviesPath);
        }
        return null;
    }

    /**
     * get backdrop path
     */
    private int getVote_count(JSONObject moviesObject) throws JSONException {
        final String VOTE_COUNT = "vote_count";
        int vote_count;
        if (!moviesObject.isNull(VOTE_COUNT)) {
            vote_count = moviesObject.getInt(VOTE_COUNT);
            return vote_count;
        }
        return -1;
    }

    /**
     * get Video
     */
    private int getVideo(JSONObject moviesObject) throws JSONException {
        final String VIDEO = "video";
        if (!moviesObject.isNull(VIDEO)) {
            boolean video;
            video = moviesObject.getBoolean(VIDEO);
            if (video) {
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

    /**
     * get genre ids
     */
    private String getGenre_ids(JSONObject moviesObject) throws JSONException {
        final String GENRE_IDS = "genre_ids";
        if (!moviesObject.isNull(GENRE_IDS)) {
            JSONArray array = moviesObject.getJSONArray(GENRE_IDS);
            String s = "[";
            for (int i = 0; i < array.length(); i++) {
                int genre_id = array.optInt(i);
                s = s + genre_id + ",";
            }
            return s.substring(0, s.length() - 1) + "]";
        }
        return null;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopMovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
