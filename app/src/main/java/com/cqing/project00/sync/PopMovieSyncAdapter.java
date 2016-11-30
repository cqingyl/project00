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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.cqing.project00.BuildConfig;
import com.cqing.project00.R;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.url.URL;
import com.cqing.project00.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static android.content.ContentValues.TAG;

/**
 * Created by Cqing on 2016/11/25.
 */

public class PopMovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private final static String LOG_TAG = PopMovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the movie, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private List<Integer> movieIdList;

    private ContentResolver contentResolver;

    public PopMovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        contentResolver = getContext().getContentResolver();
        movieIdList = new ArrayList<Integer>();
        String popMovieBaseUrl = URL.POPULAR;
        String topMovieBaseUrl = URL.TOP_RATED;
        String reviewBaseUrl = URL.HOST;
        String videoBaseUrl = URL.HOST;
        String apiKey = URL.API_KEY;
        String api = BuildConfig.THE_MOVIE_DB_API_KEY;
        try {
            //http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
            java.net.URL popMovieUrl = new java.net.URL(popMovieBaseUrl.concat(apiKey).concat(api));
            String popMoviesJsonStr = getJsonString(popMovieUrl);
            if (popMoviesJsonStr != null) {
                getMoviesDataFromJson(0, popMoviesJsonStr);
            }
            Log.i(TAG, "pop finish");
            //http://api.themoviedb.org/3/movie/top_rated?api_key=[YOUR_API_KEY]
            java.net.URL topMovieUrl = new java.net.URL(topMovieBaseUrl.concat(apiKey).concat(api));
            String topMoviesJsonStr = getJsonString(topMovieUrl);
            if (topMoviesJsonStr != null) {
                getMoviesDataFromJson(movieIdList.size(), topMoviesJsonStr);
            }
            //
            for (int i = 0; i < movieIdList.size(); i++) {
                java.net.URL reviewUrl = new java.net.URL(reviewBaseUrl.concat("/").concat(String.valueOf(movieIdList.get(i))).concat(URL.API_KEY_REVIEW).concat(api));
                String reviewsJsonStr = getJsonString(reviewUrl);
                if (reviewsJsonStr != null) {
                    getPopularMoviesReviewFromJson(reviewsJsonStr , String.valueOf(movieIdList.get(i)));
                }
                java.net.URL videoUrl = new java.net.URL(videoBaseUrl.concat("/").concat(String.valueOf(movieIdList.get(i))).concat(URL.API_KEY_VIDEO).concat(api));
                String videosJsonStr = getJsonString(videoUrl);
                if (videosJsonStr != null) {
                    getPopularMoviesVideoFromJson(videosJsonStr , String.valueOf(movieIdList.get(i)));
                }
            }

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
     * @param position move the start position of the movieIdList
     */
    private void getMoviesDataFromJson(int position, String moviesJsonStr) throws JSONException {

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
        double vote_average;
        String genre_ids;

        //get movie data
        for (int i = 0; i < moviesArray.length(); i++, position++) {
            moviesObject = moviesArray.getJSONObject(i);
            poster_path = getPoster_path(moviesObject);
            adult = getAdult(moviesObject);
            overview = getOverview(moviesObject);
            release_date = getReleaseDate(moviesObject);
            movieIdList.add(getId(moviesObject));
            original_title = getOriginal_title(moviesObject);
            original_language = getOriginal_language(moviesObject);
            title = getTitle(moviesObject);
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
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID, movieIdList.get(position));
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
            cVector.add(movieValues);
        }
        tableBulkInsert(cVector, PopMoviesContract.PopMoviesEntry.CONTENT_URI);
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
        Log.d(LOG_TAG, "PopularMoviesTask Completed. " + uri + ":" + inserted + " Inserted");
    }

    /**
     * get poster path
     */
    private String getPoster_path(JSONObject moviesObject) throws JSONException {
        final String POSTER_PATH = "poster_path";
        String moviesPath;
        if (!moviesObject.isNull(POSTER_PATH)) {

            moviesPath = moviesObject.getString(POSTER_PATH);

            String baseUrl = URL.SERVICE_IMAGE_URL;
            String picSize = URL.SERVICE_IMAGE_URL_PIC_SIZE;
            String imgPath = baseUrl.concat(picSize).concat(moviesPath);
//             Log.i(TAG_LOG, imgPath);
            return imgPath;
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

            String baseUrl = URL.SERVICE_IMAGE_URL;
            String picSize = URL.SERVICE_IMAGE_URL_PIC_SIZE;
            String imgPath = baseUrl.concat(picSize).concat(moviesPath);
            Log.d(LOG_TAG, imgPath);
            return imgPath;
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
        if ( null == accountManager.getPassword(newAccount) ) {

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
