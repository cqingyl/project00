package com.cqing.project00.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.cqing.project00.adapter.PopMoviesAdapter;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.utils.ToastUtil;
import com.cqing.project00.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Cqing on 2016/9/13.
 */
public class PopularMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static String TAG_LOG = PopularMoviesFragment.class.getSimpleName();
    final Uri movieUri = PopMoviesContract.PopMoviesEntry.CONTENT_URI;
    private final static int POPULAR_MOVIES_LOADER = 0;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SAVE_POSITION = "save_position";
    @BindView(R.id.gv_popular_movies) GridView gridView;
    private Unbinder unbinder;
    public final static String PAR_KEY = "com.cqing.project00.fragment.par";
    //电影排序：受欢迎、评分高还是收藏
    public int movieState;
    public final static int POPULAR_STATE = 0;
    public final static int VOTE_AVERAGE_STATE = 1;
    public final static int COLLECTION_STATE = 2;

    public PopMoviesAdapter adapter;

    public static final String[] POPULAR_MOVIES_COLUMNS = {
            PopMoviesContract.PopMoviesEntry.TABLE_NAME  + "." + PopMoviesContract.PopMoviesEntry._ID,
            PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID,
            PopMoviesContract.PopMoviesEntry.COLUMN_POSTER_PATH,

    };
    //电影id
    public static final int COL_POPMOVIES_MOVIEID = 1;
    public static final int COL_POPMOVIES_COLUMN_POSTER_PATH = 2;

    public PopularMoviesFragment() {

    }
    public interface Callback {
        public void ItemSelected(Uri dataUri);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SAVE_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
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
                ToastUtil.show(getActivity(), getString(R.string.collection));
                movieState = COLLECTION_STATE;
                getLoaderManager().restartLoader(POPULAR_MOVIES_LOADER, null, this);
                return true;
            case R.id.popular:
                ToastUtil.show(getActivity(), getString(R.string.popularity));
                if (Util.isNetworkConnected(getActivity())) {
                    movieState = POPULAR_STATE;
                    getLoaderManager().restartLoader(POPULAR_MOVIES_LOADER, null, this);
                }
                return true;
            case R.id.average:
                ToastUtil.show(getActivity(), getString(R.string.average));
                if (Util.isNetworkConnected(getActivity())) {
                    movieState = VOTE_AVERAGE_STATE;
                    getLoaderManager().restartLoader(POPULAR_MOVIES_LOADER, null, this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Parcelable state;

    @Override
    public void onPause() {
        // Save gridView state @ onPause
        Log.d(TAG_LOG, "saving gridView state @ onPause");
        state = gridView.onSaveInstanceState();
        super.onPause();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        adapter = new PopMoviesAdapter(getActivity(), null, 0);
        gridView.setAdapter(adapter);
        View view = View.inflate(getActivity(), R.layout.empty, null);
        ((ViewGroup)gridView.getParent()).addView(view);
        gridView.setEmptyView(view);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Callback callback = (Callback) getActivity();
                    callback.ItemSelected(PopMoviesContract.PopMoviesEntry
                            .buildPopMoviesWithMovieId(cursor.getLong(COL_POPMOVIES_MOVIEID)));
                }
                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_POSITION)){
            mPosition = savedInstanceState.getInt(SAVE_POSITION);
        }
        if(state != null) {
            Log.d(TAG_LOG, "trying to restore gridView state..");
            gridView.onRestoreInstanceState(state);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(POPULAR_MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = null;
        switch (movieState) {
            case POPULAR_STATE:
                sortOrder = PopMoviesContract.PopMoviesEntry.COLUMN_POPULARITY + " DESC";
                    return new CursorLoader(getActivity(), movieUri, POPULAR_MOVIES_COLUMNS, PopMoviesContract.PopMoviesEntry.COLUMN_MOST_POPULARITY + "=?", new String[]{"0"}, sortOrder);
            case VOTE_AVERAGE_STATE:
                sortOrder = PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
                    return new CursorLoader(getActivity(), movieUri, POPULAR_MOVIES_COLUMNS, PopMoviesContract.PopMoviesEntry.COLUMN_MOST_TOP + "=?", new String[]{"0"}, sortOrder);
            case COLLECTION_STATE:
                return new CursorLoader(getActivity(), movieUri, POPULAR_MOVIES_COLUMNS, PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION + "=?", new String[]{"0"}, null);
            default:
                return new CursorLoader(getActivity(), movieUri, POPULAR_MOVIES_COLUMNS, PopMoviesContract.PopMoviesEntry.COLUMN_MOST_POPULARITY + "=?", new String[]{"0"}, null);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        adapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }
}
