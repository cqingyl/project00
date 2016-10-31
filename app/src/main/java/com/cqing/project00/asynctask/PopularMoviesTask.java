package com.cqing.project00.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cqing.project00.BuildConfig;
import com.cqing.project00.URL;
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
import java.net.MalformedURLException;
import java.util.Vector;

/**
 * Access to network resources
 */
public class PopularMoviesTask extends AsyncTask<String, Void, Void> {

    private final static String TAG_LOG = PopularMoviesTask.class.getSimpleName();


    private Context mContext;
    private int state;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String moviesJsonStr = null;


    public PopularMoviesTask(int state, Context context) {
        this.state = state;
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        String  baseUrl = null;
        //判断是popular还是top rated
        switch (state) {
            case PopularMoviesFragment.NORMAL_STATE:
                baseUrl = URL.POPULAR;
                break;
            case PopularMoviesFragment.POPULAR_STATE:
                baseUrl = URL.POPULAR;
                break;
            case PopularMoviesFragment.VOTE_AVERAGE_STATE:
                baseUrl = URL.TOP_RATED;
                break;
        }
        Log.d(TAG_LOG, baseUrl);
        if (baseUrl == null){
            return null;
        }
        String apiKey = URL.API_KEY;
        String api = BuildConfig.THE_MOVIE_DB_API_KEY;
        try {
            java.net.URL url = new java.net.URL(baseUrl.concat(apiKey).concat(api));

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
            moviesJsonStr = buffer.toString();
//            Log.i(TAG_LOG, moviesJsonStr);
            getPopularMoviesDataFromJson(moviesJsonStr);
        } catch (MalformedURLException e) {
            Log.e(TAG_LOG, e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG_LOG, e.getMessage(), e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG_LOG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG_LOG, "Error closing stream", e);
                }
            }
        }
        return null;
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
        String adult;
        String overview;
        String release_date;
        int id;
        String original_title;
        String original_language;
        String title;
        String backdrop_path;
        double popularity;
        int vote_count;
        String video;
        double vote_average;
        String genre_ids;

        //get movie data
        for (int i = 0; i < moviesArray.length(); i++) {
            moviesObject = moviesArray.getJSONObject(i);

            poster_path = getPoster_path(moviesObject);

            adult = getAdult(moviesObject);

            overview = getOverview(moviesObject);

            release_date = getReleaseDate(moviesObject);

            id = getId(moviesObject);

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
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_ID, id);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_ORIGINAL_TITLE, original_title);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_ORIGINAL_LANGUAGE, original_language);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_TITLE, title);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_BACKDROP_PATH, backdrop_path);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_COUNT, vote_count);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_VIDEO, video);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_AVERAGE, vote_average);
            movieValues.put(PopMoviesContract.PopMoviesEntry.COLUMN_GENRE_IDS, genre_ids);

            cVector.add(movieValues);
        }
        int inserted = 0;
        if (cVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVector.size()];
            cVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(PopMoviesContract.PopMoviesEntry.CONTENT_URI, cvArray);
        }
        Log.d(TAG_LOG, "PopularMoviesTask Completed. " + inserted + " Inserted");

    }




    /**
     * get imgpath
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
    private String getAdult(JSONObject moviesObject) throws JSONException {
        final String ADULT = "adult";
        if (!moviesObject.isNull(ADULT)) {
            boolean adult;
            adult = moviesObject.getBoolean(ADULT);
            if (adult) {
                return "true";
            } else {
                return "false";
            }
        }
        return null;
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
    private String getVideo (JSONObject moviesObject) throws JSONException {
        final String VIDEO = "video";
        if (!moviesObject.isNull(VIDEO)) {
            boolean video;
            video = moviesObject.getBoolean(VIDEO);
            if (video) {
                return "true";
            } else {
                return "false";
            }
        }
        return null;
    }
    /**
     * get genre ids
     * */
    private String getGenre_ids (JSONObject moviesObject) throws JSONException {
        final String GENRE_IDS = "genre_ids";
        if (!moviesObject.isNull(GENRE_IDS)) {
            JSONArray array = moviesObject.getJSONArray(GENRE_IDS);
            String s = "[";
            for (int i = 0; i < array.length(); i++){
                int genre_id = array.optInt(i);
                s = s + genre_id + ",";
            }
            return s.substring(0, s.length()-1) + "]";
        }
        return null;
    }
}
