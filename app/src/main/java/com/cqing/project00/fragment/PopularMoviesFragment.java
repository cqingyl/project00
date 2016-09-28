package com.cqing.project00.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.cqing.project00.BuildConfig;
import com.cqing.project00.R;
import com.cqing.project00.activity.MovieDetailActivity;
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
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Cqing on 2016/9/13.
 */
public class PopularMoviesFragment extends Fragment{

    private final static String TAG_LOG = PopularMoviesFragment.class.getSimpleName();

    public final static String PAR_KEY = "com.cqing.project00.fragment.par";

    private final static int NORMAL_STATE = 0;
    private final static int POPULAR_STATE = 1;
    private final static int VOTE_AVERAGE_STATE = 2;

    private PopularMoviesAdapter adapter;
    private ArrayList<PopularMovies> mData;
    private Bundle mBundle;
    public PopularMoviesFragment() {

    }


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    @Override
    public void onStart() {
        super.onStart();
            mData= new ArrayList<>();
            if( isNetworkConnected() ){
                new PopularMoviesTask(NORMAL_STATE).execute();
            } else {
                PopularMovies popularMovies = new PopularMovies();
                mData.add(popularMovies);
            }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.popfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
                Toast.makeText(getActivity(), "favorite", Toast.LENGTH_SHORT).show();
                    new PopularMoviesTask(POPULAR_STATE).execute();
                return true;
            case R.id.average:
                Toast.makeText(getActivity(), "average", Toast.LENGTH_SHORT).show();
                    new PopularMoviesTask(VOTE_AVERAGE_STATE).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gv_popular_movies);
        mData = new ArrayList<>();
        adapter = new PopularMoviesAdapter(getActivity(), mData);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                if(isNetworkConnected()){
                    mBundle.putInt("position", position);
                    intent.putExtras(mBundle);
                }
                    startActivity(intent);
            }
        });
        return rootView;
    }
    /**
     * Access to network resources
     * */
    public class PopularMoviesTask extends AsyncTask<String, String, ArrayList<PopularMovies>> {

        private final String TAG_LOG = PopularMoviesTask.class.getSimpleName();

        private int state;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        ArrayList<PopularMovies> mData = new ArrayList<>();

        public PopularMoviesTask(int state) {
            this.state = state;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<PopularMovies> doInBackground(String... params) {
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
                mData = getPopularMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mData;

        }

        /**
         * Get movie information
         * */
        private ArrayList<PopularMovies> getPopularMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            ArrayList<PopularMovies> list = null;
            JSONArray moviesArray = null;
                JSONObject jsonObject = new JSONObject(moviesJsonStr);
                final String RESULT = "results";

                list = new ArrayList<>();

                moviesArray = jsonObject.getJSONArray(RESULT);
            JSONObject moviesObject;
            mBundle = new Bundle();

            String moviesImgPath;
            String moviesOverview;
            String moviesTitle;
            String moviesData;
            Double moviesParpularity;
            Double moviesAverage;
            //get movie data
            for(int i = 0; i < moviesArray.length(); i++) {
                PopularMovies movies = null;
                    moviesObject = moviesArray.getJSONObject(i);
                    movies = new PopularMovies();

                    moviesImgPath = getImgPath(moviesObject);
                    movies.setImgUrl(moviesImgPath);

                    moviesTitle = getTitle(moviesObject);
                    movies.setTitle(moviesTitle);

                    moviesOverview = getOverview(moviesObject);
                    movies.setOverview(moviesOverview);

                    moviesData = getReleaseDate(moviesObject);
                    movies.setReleaseDate(moviesData);

                    moviesParpularity = getPopularity(moviesObject);
                    movies.setPopularity(moviesParpularity);

                    moviesAverage = getAverages(moviesObject);
                    movies.setVoteAverage(moviesAverage);

                    list.add(movies);
            }


            return list;
        }


        @Override
        protected void onPostExecute(ArrayList<PopularMovies> result) {
            if (result != null) {
                switch (state){
                    case POPULAR_STATE :
                        Collections.sort(result, new Comparator<PopularMovies>() {
                            @Override
                            public int compare(PopularMovies lhs, PopularMovies rhs) {
                                int i = 0;
                                if (lhs.getPopularity() > rhs.getPopularity()){
                                    i = -1;
                                } else if (lhs.getPopularity() == rhs.getPopularity()) {
                                    i = 0;
                                } else {
                                    i = 1;
                                }
                                return i;
                            }
                        });
                        mBundle.putParcelableArrayList(PAR_KEY, result);
                        mData = result;
                        adapter.setItemList(mData);
                        adapter.notifyDataSetChanged();
                        break;
                    case VOTE_AVERAGE_STATE :
                        Collections.sort(result, new Comparator<PopularMovies>() {
                            @Override
                            public int compare(PopularMovies lhs, PopularMovies rhs) {
                                int i = 0;
                                if (lhs.getVoteAverage() > rhs.getVoteAverage()){
                                    i = -1;
                                } else if (lhs.getVoteAverage() == rhs.getVoteAverage()) {
                                    i = 0;
                                } else {
                                    i = 1;
                                }
                                return i;
                            }
                        });
                        mBundle.putParcelableArrayList(PAR_KEY, result);
                        mData = result;
                        adapter.setItemList(mData);
                        adapter.notifyDataSetChanged();
                        break;
                    case NORMAL_STATE :
                        mBundle.putParcelableArrayList(PAR_KEY, result);
                        mData = result;
                        adapter.setItemList(mData);
                        adapter.notifyDataSetChanged();
                        break;
                }
                adapter.notifyDataSetChanged();
            }
        }
        /**
         * split joint image address string and added to the list of data, and returns a list of data
         * */
        private String getImgPath(JSONObject moviesObject) throws JSONException {
            final String POSTER_PATH = "poster_path";
            String moviesPath;
            moviesPath = moviesObject.getString(POSTER_PATH);

            String baseUrl = "http://image.tmdb.org/t/p/";
            String picSize = "w185";
            String imgPath = baseUrl.concat(picSize).concat(moviesPath);
           // Log.i(TAG_LOG, imgPath);
            return imgPath;
        }
        /**
         * get overview
         * */
        private String getOverview(JSONObject moviesObject) throws JSONException {

            final String OVER_VIEW= "overview";
            String moviesOverview;
            moviesOverview = moviesObject.getString(OVER_VIEW);
            return moviesOverview;
        }
        /**
         *  get title
         * */
        private String getTitle(JSONObject moviesObject) throws JSONException {
            final String TITLE = "title";
            String title;
            title = moviesObject.getString(TITLE);
            return title;
        }
        /**
         *  get date
         * */
        private String getReleaseDate(JSONObject moviesObject) throws JSONException {
            final String RELEASE_DATE = "release_date";
            String releaseDate;
            releaseDate = moviesObject.getString(RELEASE_DATE);
            return releaseDate;
        }
        /**
         *  get popularity
         * */
        private Double getPopularity(JSONObject moviesObject) throws JSONException {
            final String POPULARITY = "popularity";
            Double popularity;
            popularity = moviesObject.getDouble(POPULARITY);
            return  popularity;
        }
        /**
         *  get averages
         * */
        private Double getAverages(JSONObject moviesObject) throws JSONException {
            final String VOTE_AVERAGE = "vote_average";
            Double voteAverage;
            voteAverage = moviesObject.getDouble(VOTE_AVERAGE);
            return  voteAverage;
        }
    }
}
