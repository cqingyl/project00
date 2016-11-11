package com.cqing.project00.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cqing.project00.R;
import com.cqing.project00.fragment.PopularDetailFragment;
import com.cqing.project00.utils.Util;
import com.squareup.picasso.Picasso;


/**
 * Created by Cqing on 2016/9/13.
 */

public class CollectionMovieAdapter extends CursorAdapter {
    public CollectionMovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.item_collection_movies, null);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String url = cursor.getString(PopularDetailFragment.COL_POPMOVIES_BACKDROP_PATH);
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.pop_movie_logo)
                .error(R.drawable.error)
                .resize(Util.dip2px(context, 64), Util.dip2px(context, 64))
                .centerCrop()
                .into(viewHolder.iv);
        String title = cursor.getString(PopularDetailFragment.COL_POPMOVIES_ORIGINAL_TITLE);
        viewHolder.textTitle.setText(title);
        String overview = cursor.getString(PopularDetailFragment.COL_POPMOVIES_OVERVIEW);
        viewHolder.textOverview.setText(overview);
    }

    public class ViewHolder {
        public ImageView iv;
        public TextView textTitle;
        public TextView textOverview;

        public ViewHolder(View view) {
            iv = (ImageView) view.findViewById(R.id.iv_poster);
            textTitle = (TextView) view.findViewById(R.id.tv_title);
            textOverview = (TextView) view.findViewById(R.id.tv_overview);
        }
    }
}
