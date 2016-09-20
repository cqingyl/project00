package com.cqing.project00.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cqing.project00.BuildConfig;
import com.cqing.project00.R;
import com.cqing.project00.activity.PopularMoviesInfoActivity;
import com.cqing.project00.adapter.PopularMoviesAdapter;
import com.cqing.project00.bean.PopularMovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Cqing on 2016/9/13.
 */
public class PopularMoviesFragment extends Fragment{

    private final static String TAG_LOG = PopularMoviesFragment.class.getSimpleName();

    private PopularMoviesAdapter adapter;
    private ArrayList<PopularMovies> listData;
    private String imgPath;
    private ArrayList<String> urls  = new ArrayList<>();
    public PopularMoviesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gv_popular_movies);

        /**
         * Access to network resources
         * */
        try {
            urls = new PopularMoviesTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        listData= new ArrayList<>();

        for (String url: urls) {
            PopularMovies movies = new PopularMovies(url);
            listData.add(movies);
        }
        adapter = new PopularMoviesAdapter(getActivity(), listData);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), PopularMoviesInfoActivity.class));
            }
        });
        return rootView;
    }

    public class PopularMoviesTask extends AsyncTask<String, String, ArrayList<String>> {

        private final String TAG_LOG = PopularMoviesTask.class.getSimpleName();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        ArrayList<String> imgPaths = new ArrayList<>();

        public PopularMoviesTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String baseUrl = "http://api.themoviedb.org/3/movie/popular?";
            String apiKey = "api_key=";
            String api = BuildConfig.THE_MOVIE_DB_API_KEY;
            try {
                URL url = new URL(baseUrl.concat(apiKey).concat(api));

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
                Log.i(TAG_LOG, moviesJsonStr);
            } catch (MalformedURLException e) {
                Log.e(TAG_LOG, e.getMessage(), e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG_LOG, e.getMessage(), e);
                e.printStackTrace();
            }
            try {
                return getPopularMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(TAG_LOG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
        }
        /**
         * split joint image address string
         * */
        private ArrayList<String> getPopularMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            final String RESULT = "results";
            final String POSTER_PATH = "poster_path";

            JSONObject jsonObject = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = jsonObject.getJSONArray(RESULT);
            JSONObject moviesObject;
            String moviesPath;
            for(int i = 0; i < moviesArray.length(); i++) {
                moviesObject = moviesArray.getJSONObject(i);
                moviesPath = moviesObject.getString(POSTER_PATH);
               // Log.i(TAG_LOG, moviesPath);

                String baseUrl = "http://image.tmdb.org/t/p/";
                String picSize = "w185";
                imgPath = baseUrl.concat(picSize).concat(moviesPath);
                imgPaths.add(imgPath);
            }
            return imgPaths;
        }
    }
}
