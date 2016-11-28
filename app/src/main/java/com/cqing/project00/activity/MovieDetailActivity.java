package com.cqing.project00.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cqing.project00.R;
import com.cqing.project00.fragment.MovieDetailFragment;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_detail);
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, getIntent().getData());

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.popular_movie_detail_container, fragment)
                    .commit();
        }
    }
}
