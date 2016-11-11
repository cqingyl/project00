package com.cqing.project00.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.cqing.project00.R;
import com.cqing.project00.activity.CollectionActivity;
import com.cqing.project00.activity.MovieDetailActivity;
import com.cqing.project00.adapter.PopMoviesAdapter;
import com.cqing.project00.asynctask.PopularMoviesTask;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.utils.ToastUtil;
import com.cqing.project00.utils.Util;

/**
 * Created by Cqing on 2016/9/13.
 */
public class PopularMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks{

    private final static String TAG_LOG = PopularMoviesFragment.class.getSimpleName();

    private final static int POPULAR_MOVIES_LOADER = 0;
    private  int mPosition = ListView.INVALID_POSITION;
    private GridView gridView;

    public final static String PAR_KEY = "com.cqing.project00.fragment.par";
    //电影状态 受欢迎还是评分高
    public int movieState;
    public final static int POPULAR_STATE = 0;
    public final static int VOTE_AVERAGE_STATE = 1;

    public PopMoviesAdapter adapter;

    private static final String[] POPULAR_MOVIES_COLUMNS = {
            PopMoviesContract.PopMoviesEntry.TABLE_NAME  + "." + PopMoviesContract.PopMoviesEntry._ID,
            PopMoviesContract.PopMoviesEntry.COLUMN_POPULARITY,
            PopMoviesContract.PopMoviesEntry.COLUMN_ADULT,
            PopMoviesContract.PopMoviesEntry.COLUMN_BACKDROP_PATH,
            PopMoviesContract.PopMoviesEntry.COLUMN_ID,
            PopMoviesContract.PopMoviesEntry.COLUMN_ORIGINAL_LANGUAGE,
            PopMoviesContract.PopMoviesEntry.COLUMN_ORIGINAL_TITLE,
            PopMoviesContract.PopMoviesEntry.COLUMN_OVERVIEW,
            PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_AVERAGE,
            PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_COUNT,
            PopMoviesContract.PopMoviesEntry.COLUMN_POSTER_PATH,
            PopMoviesContract.PopMoviesEntry.COLUMN_TITLE,
            PopMoviesContract.PopMoviesEntry.COLUMN_VIDEO,
            PopMoviesContract.PopMoviesEntry.COLUMN_GENRE_IDS,
            PopMoviesContract.PopMoviesEntry.COLUMN_RELEASE_DATE,
            PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION,

    };
    //列表id
    public static final int COL_POPMOVIES_ID = 0;
    public static final int COL_POPMOVIES_POPULARITY = 1;
    public static final int COL_POPMOVIES_ADULT = 2;
    public static final int COL_POPMOVIES_BACKDROP_PATH = 3;
    //电影id
    public static final int COL_POPMOVIES_MOVIEID = 4;
    public static final int COL_POPMOVIES_ORIGINAL_LANGUAGE = 5;
    public static final int COL_POPMOVIES_ORIGINAL_TITLE = 6;
    public static final int COL_POPMOVIES_OVERVIEW = 7;
    public static final int COL_POPMOVIES_VOTE_AVERAGE = 8;
    public static final int COL_POPMOVIES_VOTE_COUNT = 9;
    public static final int COL_POPMOVIES_COLUMN_POSTER_PATH = 10;
    public static final int COL_POPMOVIES_TITLE = 11;
    public static final int COL_POPMOVIES_VIDEO = 12;
    public static final int COL_POPMOVIES_GENRE_IDS = 13;
    public static final int COLUMN_RELEASE_DATE = 14;
    public static final int COLUMN_COLLECTION = 15;

    public PopularMoviesFragment() {

    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (Util.isNetworkConnected(getActivity())) {
//            new PopularMoviesTask(NORMAL_STATE, getActivity()).execute();
//
//        }
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pop_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.collection:
                getActivity().startActivity(new Intent(getActivity(), CollectionActivity.class));
                return true;
            case R.id.popular:
                ToastUtil.show(getActivity(), getString(R.string.popularity));
                if (Util.isNetworkConnected(getActivity())) {
                    new PopularMoviesTask(getActivity(), PopularMoviesTask.MOVIE_STATE).execute();
                    movieState = POPULAR_STATE;
                    getLoaderManager().restartLoader(POPULAR_MOVIES_LOADER, null, this);
                }
                return true;
            case R.id.average:
                ToastUtil.show(getActivity(), getString(R.string.average));
                if (Util.isNetworkConnected(getActivity())) {
                    new PopularMoviesTask(getActivity(), PopularMoviesTask.MOVIE_STATE).execute();
                    movieState = VOTE_AVERAGE_STATE;
                    getLoaderManager().restartLoader(POPULAR_MOVIES_LOADER, null, this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gv_popular_movies);
        adapter = new PopMoviesAdapter(getActivity(), null, 0);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                            .setData(PopMoviesContract.PopMoviesEntry
                                    .buildPopMoviesWithMovieId(cursor.getLong(COL_POPMOVIES_MOVIEID)));

                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(POPULAR_MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String sortOrder = null;
        switch (movieState) {
            case POPULAR_STATE:
                sortOrder = PopMoviesContract.PopMoviesEntry.COLUMN_POPULARITY + " ASC";
                break;
            case VOTE_AVERAGE_STATE:
                sortOrder = PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_AVERAGE + " ASC";
                break;
        }
        Uri movieUri = PopMoviesContract.PopMoviesEntry.CONTENT_URI;
        Log.i(TAG_LOG, "onCreateLoader sortOrder: " + sortOrder);
        if (sortOrder != null)
            return new CursorLoader(getActivity(), movieUri, POPULAR_MOVIES_COLUMNS, null, null, sortOrder);
        return new CursorLoader(getActivity(), movieUri, POPULAR_MOVIES_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        adapter.swapCursor((Cursor) data);
        if (mPosition != ListView.INVALID_POSITION) {
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }
}
