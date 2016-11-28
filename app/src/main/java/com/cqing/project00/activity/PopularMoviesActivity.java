package com.cqing.project00.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cqing.project00.R;
import com.cqing.project00.fragment.MovieDetailFragment;
import com.cqing.project00.fragment.PopularMoviesFragment;

public class PopularMoviesActivity extends AppCompatActivity implements PopularMoviesFragment.Callback{

    private static final String MOVIEDETAIL_TAG = "MDTAG";
    private static boolean mTwoPane;
    private static String mPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);

        if (findViewById(R.id.popular_movie_detail_container) != null) {
            mTwoPane = true;
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.popular_movie_detail_container, new MovieDetailFragment(), MOVIEDETAIL_TAG)
                    .commit();
        }

        } else {
            mTwoPane = false;
        }

    }

    @Override
    public void ItemSelected(Uri dataUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, dataUri);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.popular_movie_detail_container, fragment, MOVIEDETAIL_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .setData(dataUri);
            startActivity(intent);
        }
    }
}
