package com.cqing.project00.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cqing.project00.R;
import com.cqing.project00.bean.MoviesApi;
import com.cqing.project00.bean.PopularMovies;
import com.cqing.project00.utils.ToastUtil;
import com.cqing.project00.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Cqing on 2016/9/19.
 */


public class PopularDetailFragment extends Fragment {

    private Bundle bundle;
    private int position;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_detail, container, false);
        if (Util.isNetworkConnected(getActivity())) {
            bundle = getActivity().getIntent().getExtras();
            position = bundle.getInt(MoviesApi.KEY_MOVIE_POSITION);

            ArrayList mList = bundle.getParcelableArrayList(PopularMoviesFragment.PAR_KEY);
            PopularMovies data = (PopularMovies) mList.get(position);
            loadOverview(rootView, data);
            loadImg(rootView, data);
            loadTitle(rootView, data);
            loadData(rootView, data);
            loadAverage(rootView, data);
            //mark as favorite
            rootView.findViewById(R.id.relativeLayout_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.show(getActivity(),getString(R.string.marked));
                }
            });
        }
        return rootView;
    }

    private void loadTitle(View rootView, PopularMovies data) {
        ((TextView) rootView.findViewById(R.id.tv)).setText(data.getTitle());
    }


    private void loadImg(View rootView, PopularMovies data) {
        ImageView iv = (ImageView) rootView.findViewById(R.id.iv_movies);
        Picasso.with(getActivity()).load(data.getImgUrl()).into(iv);
    }

    private void loadOverview(View rootView, PopularMovies data) {
        ((TextView) rootView.findViewById(R.id.tv_overview)).setText(data.getOverview());
    }

    private void loadData(View rootView, PopularMovies data) {
        ((TextView) rootView.findViewById(R.id.tv_data)).setText(data.getReleaseDate());
    }

    private void loadAverage(View rootview, PopularMovies data) {
        ((TextView) rootview.findViewById(R.id.tv_averages)).setText(String.valueOf(data.getVoteAverage()));
    }
}
