package com.cqing.project00.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cqing.project00.R;
import com.cqing.project00.bean.PopularMovies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Cqing on 2016/9/19.
 */


public class PopularDetailFragment extends Fragment {

    private Bundle bundle;
    private int position;

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_detail, container, false);
        if (isNetworkConnected()){
            bundle = getActivity().getIntent().getExtras();
            position = bundle.getInt("position");

            ArrayList mList = bundle.getParcelableArrayList(PopularMoviesFragment.PAR_KEY);
            PopularMovies data = (PopularMovies) mList.get(position);
            loadOverview(rootView, data);
            loadImg(rootView, data);
            loadTitle(rootView, data);
            loadData(rootView, data);

            //mark as favorite
            rootView.findViewById(R.id.relativeLayout_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "you marked", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return rootView;
    }

    private void loadTitle(View rootView, PopularMovies data) {
        ((TextView)rootView.findViewById(R.id.tv)).setText(data.getTitle());
    }


    private void loadImg(View rootView, PopularMovies data) {
        ImageView iv = (ImageView) rootView.findViewById(R.id.iv_movies);
        Picasso.with(getActivity()).load(data.getImgUrl()).into(iv);
    }

    private void loadOverview (View rootView, PopularMovies data) {
        ((TextView)rootView.findViewById(R.id.tv_overview)).setText(data.getOverview());
    }

    private void loadData(View rootView, PopularMovies data) {
        ((TextView)rootView.findViewById(R.id.tv_data)).setText(data.getReleaseDate());
    }
}
