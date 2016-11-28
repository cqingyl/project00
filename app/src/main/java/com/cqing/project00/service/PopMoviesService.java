package com.cqing.project00.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.cqing.project00.BuildConfig;
import com.cqing.project00.Project00.URL;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.fragment.PopularMoviesFragment;

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

/**
 * Created by Cqing on 2016/11/16.
 */

public class PopMoviesService extends IntentService {

    private final static String LOG_TAG = PopMoviesService.class.getSimpleName();

    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    //存放电影id
    private List<Integer> movieIdList;

    private ContentResolver contentResolver;


    public PopMoviesService() {
        super("PopMoviesService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        contentResolver = this.getContentResolver();
        movieIdList = new ArrayList();
        String movieBaseUrl = URL.POPULAR;
        String reviewBaseUrl = URL.HOST;
        String videoBaseUrl = URL.HOST;
        String apiKey = URL.API_KEY;
        String api = BuildConfig.THE_MOVIE_DB_API_KEY;
        try {
            java.net.URL movieUrl = new java.net.URL(movieBaseUrl.concat(apiKey).concat(api));
            String moviesJsonStr = getJsonString(movieUrl);
//                   Log.i(TAG_LOG, moviesJsonStr);
            if (moviesJsonStr != null) {
                getPopularMoviesDataFromJson(moviesJsonStr);
            }

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

            if (isReviewExits(reviewId)) {
                contentResolver.update(PopMoviesContract.ReviewEntry.CONTENT_URI, reviewValues, sMovieIdInReviewSelection(reviewId), null);
            } else {
                contentResolver.insert(PopMoviesContract.ReviewEntry.CONTENT_URI, reviewValues);
            }
        }

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
            if (isVideoExits(videoId)) {
                contentResolver.update(PopMoviesContract.VideoEntry.CONTENT_URI, videoValues, sMovieIdInVideoSelection(videoId), null);
            } else {
                contentResolver.insert(PopMoviesContract.VideoEntry.CONTENT_URI, videoValues);
            }
        }
    }

    /**
     * Get movie information
     */
    private void getPopularMoviesDataFromJson(String moviesJsonStr) throws JSONException {

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
        int videoNum;
        int reviewNum;

        //get movie data
        for (int i = 0; i < moviesArray.length(); i++) {

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

            videoNum = i;
            reviewNum = i;

            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_REVIEW_KEY, ++videoNum);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_VIDEO_KEY, ++reviewNum);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_POSTER_PATH, poster_path);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_ADULT, adult);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_RELEASE_DATE, release_date);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID, movieIdList.get(i));
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
            if (isMovieIdExits(movieIdList.get(i))) {
                contentResolver.update(PopMoviesContract.PopMoviesEntry.CONTENT_URI, movieValues, sMovieIdInMovieSelection(movieIdList.get(i)), null);
            } else {
                contentResolver.insert(PopMoviesContract.PopMoviesEntry.CONTENT_URI, movieValues);
            }
        }
    }

    // 判断电影是否存在于"movie"里
    private boolean isMovieIdExits(int movieId) {
        Cursor c = contentResolver.query(PopMoviesContract.PopMoviesEntry.CONTENT_URI, PopularMoviesFragment.POPULAR_MOVIES_COLUMNS, sMovieIdInMovieSelection(movieId), null, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    private boolean isReviewExits(String reviewId) {

        Cursor c = contentResolver.query(PopMoviesContract.ReviewEntry.CONTENT_URI, null, sMovieIdInReviewSelection(reviewId), null, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }
    private boolean isVideoExits(String videoId) {
        Cursor c = contentResolver.query(PopMoviesContract.VideoEntry.CONTENT_URI, null, sMovieIdInVideoSelection(videoId), null, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    private String sMovieIdInMovieSelection(int movieId) {
        return PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID + "=" + movieId;
    }

    private String sMovieIdInVideoSelection(String videoId) {
        return PopMoviesContract.VideoEntry.COLUMN_VIDEO_ID + "= '" + videoId + "'";
    }

    private String sMovieIdInReviewSelection(String reviewId) {
        return PopMoviesContract.ReviewEntry.COLUMN_REVIEW_ID + "= '" + reviewId + "'";
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
            String picSize = URL.SERVICE_IMAGE_URL_PICSIZE;
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
            popularity = moviesObject.getDouble(POPULARITY);
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
        final String ORIGINAL_LANGUAGR = "original_language";
        if (!moviesObject.isNull(ORIGINAL_LANGUAGR)) {
            String original_language;
            original_language = moviesObject.getString(ORIGINAL_LANGUAGR);
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
            String picSize = URL.SERVICE_IMAGE_URL_PICSIZE;
            String imgPath = baseUrl.concat(picSize).concat(moviesPath);
            // Log.i(TAG_LOG, imgPath);
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

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent i = new Intent(context, PopMoviesService.class);
            context.startService(i);

        }
    }
}
