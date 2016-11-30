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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqing.project00.R;
import com.cqing.project00.adapter.MovieDetailAdapter;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Cqing on 2016/9/19.
 */


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private final static int DETAIL_LOADER = 1;
    public final static String MOVIE_DETAIL_URI = "URI";
    @BindView (R.id.movie_recycler_view) RecyclerView mMovieRecyclerView;
    private Unbinder unbinder;
    private MovieDetailAdapter movieDetailAdapter;
    private Cursor cursor;
    private Uri mUri = null;
    int numberOfMovie = 0;
    int numberOfReview= 0;
    int numberOfVideo = 0;

    private static final int COL_REVIEW_URL = 5;
    private static final int COL_VIDEO_KEY = 2;
    public String movieId;
    private static final String YouTuBePkgName = "com.google.android.youtube";
    private static final String YouTuBeProtocol = "vnd.youtube:";
    private static final String YouTuBeUrl = "http://www.youtube.com/watch?v=";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null){
            mUri = args.getParcelable(MovieDetailFragment.MOVIE_DETAIL_URI);
            Log.d(LOG_TAG, "uri: " + mUri);
        }
        View rootView = inflater.inflate(R.layout.fragment_popular_detail, container, false);
        unbinder = ButterKnife.bind(this,rootView);
        movieDetailAdapter = new MovieDetailAdapter(getActivity(), cursor);
        int numColumn = 1;
        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numColumn));
        mMovieRecyclerView.setAdapter(movieDetailAdapter);
        if (mUri != null) {
        movieId = mUri.getLastPathSegment();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), PopMoviesContract.PopMoviesEntry.buildPopMoviesWithRT(Long.parseLong(movieId)), null, null, null, null);
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader loader, Cursor cur) {
        this.cursor = cur;
        switch(loader.getId()) {
            case DETAIL_LOADER:
                //在movie，review，video 三张表都存储了movie id
                //遍历cursor，获取到不同表的movie id
                for(cursor.moveToFirst(), numberOfMovie = 0, numberOfReview = 0, numberOfVideo = 0; !cursor.isAfterLast(); cursor.moveToNext())
                {
                    for (int i = 0; i < cursor.getColumnCount(); i ++){
                        if (cursor.getColumnName(i).equals(PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID))
                            numberOfMovie++;
                        if (cursor.getColumnName(i).equals(PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID))
                            numberOfReview++;
                        if (cursor.getColumnName(i).equals(PopMoviesContract.VideoEntry.COLUMN_MOVIE_ID))
                            numberOfVideo++;
                    }
                }
                movieDetailAdapter.setNumberOfReviews(numberOfReview);
                movieDetailAdapter.swapCursor(cursor);

                //这是自定义的lister
                movieDetailAdapter.setClickListener(new MovieDetailAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        cursor.moveToPosition(position);
                        if (position == 0) {

                        } else if (position < numberOfReview + 1){
                            Uri reviewUri = Uri.parse(cursor.getString(COL_REVIEW_URL));
                            Intent intent  = new Intent(Intent.ACTION_VIEW, reviewUri);
                            if(Util.isIntentionToUse(getActivity(), intent))
                                getActivity().startActivity(intent);
                        } else if (position < numberOfVideo + numberOfReview + 1){
                            String youtubeKey = cursor.getString(COL_VIDEO_KEY);
                            Uri trailerUri = null;
                            if (Util.isPkgInstalled(getActivity(),YouTuBePkgName)){
                                 trailerUri = Uri.parse(YouTuBeProtocol + youtubeKey);
                            } else {
                                 trailerUri = Uri.parse(YouTuBeUrl + youtubeKey);
                            }
                            Intent intent   = new Intent(Intent.ACTION_VIEW, trailerUri);
                            if(Util.isIntentionToUse(getActivity(), intent))
                                getActivity().startActivity(intent);
                        }
                    }
                });
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }
    @Override
    public void onLoaderReset(Loader loader) {
        switch(loader.getId()) {
            case DETAIL_LOADER:
                movieDetailAdapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }
}
