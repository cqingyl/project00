package com.cqing.project00.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.cqing.project00.R;
import com.cqing.project00.activity.CollectionActivity;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.utils.ToastUtil;
import com.cqing.project00.utils.Util;
import com.squareup.picasso.Picasso;

/**
 * Created by Cqing on 2016/9/19.
 */


public class PopularDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks{

    private final static String LOG_TAG = PopularDetailFragment.class.getSimpleName();
    private final static int DETAIL_LOADER = 0;

    private static final String DETAIL_URI = "URI";
    private Uri mUri = null;
    private TextView tv_title;
    private ImageView iv_movies;
    private TextView tv_overview;
    private TextView tv_data;
    private TextView tv_averages;

    public static final String[] DETAIL_COLUMNS = {
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
    public static final int COL_POPMOVIES_RELEASE_DATE = 14;
    public static final int COL_POPMOVIES_COLLECTION = 15;
    //Movie.id = ?
    private static String sMovieIdSelection =
            PopMoviesContract.PopMoviesEntry.TABLE_NAME + "." + PopMoviesContract.PopMoviesEntry.COLUMN_ID + " = ? ";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_detail, container, false);

        mUri = getActivity().getIntent().getData();
        Log.i(LOG_TAG, "uri: " + mUri.toString());
        iv_movies = (ImageView) rootView.findViewById(R.id.iv_movies);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_overview = (TextView) rootView.findViewById(R.id.tv_overview);
        tv_data = (TextView) rootView.findViewById(R.id.tv_data);
        tv_averages = (TextView) rootView.findViewById(R.id.tv_averages);
        //mark as popularity
        rootView.findViewById(R.id.relativeLayout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getActivity(), getString(R.string.marked));
                ContentValues values = new ContentValues();
                values.put(PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION, 0);
                //获取movieId
                Long movieId = PopMoviesContract.PopMoviesEntry.getMovieId(mUri);
                ContentResolver contentResolver = getActivity().getContentResolver();
                int position = contentResolver.update(PopMoviesContract.PopMoviesEntry.CONTENT_URI, values,"id=" + movieId , null);
                Log.i(LOG_TAG, String.valueOf(position));

            }
        });
        rootView.findViewById(R.id.iv_trailer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = Util.getYouTuBeIntent(mUri);
                startActivity(i);
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pop_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.collection :
                startActivity(new Intent(getActivity(), CollectionActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (((Cursor) data).moveToFirst() && data != null) {
            String ImgPath = ((Cursor) data).getString(COL_POPMOVIES_COLUMN_POSTER_PATH);
            Picasso.with(getActivity()).load(ImgPath).error(R.mipmap.ic_launcher).into(iv_movies);

            String title = ((Cursor) data).getString(COL_POPMOVIES_ORIGINAL_TITLE);
            tv_title.setText(title);

            String overview = ((Cursor) data).getString(COL_POPMOVIES_OVERVIEW);
            tv_overview.setText(overview);

            String release_data = ((Cursor) data).getString(COL_POPMOVIES_RELEASE_DATE);
            tv_data.setText(release_data);

            String averages = ((Cursor) data).getString(COL_POPMOVIES_VOTE_AVERAGE);
            tv_averages.setText(averages);

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
