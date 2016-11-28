
package com.cqing.project00.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cqing.project00.R;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.other.RecyclerViewCursorAdapter;
import com.cqing.project00.viewholder.ReviewViewHolder;
import com.cqing.project00.viewholder.VideoViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by Cqing on 2016/11/22.
 */

public class MovieDetailAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> {
    private static final String TAG_LOG = MovieDetailAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_MOVIE_DETAIL = 0;
    private static final int VIEW_TYPE_REVIEW = 1;
    private static final int VIEW_TYPE_VIDEO = 2;

    private Context mContext;
    private Cursor mCursor;
    private int numberOfVideos;
    private int numberOfReviews;

    private static final int COL_POPMOVIES_VOTE_AVERAGE = 13;
    private static final int COL_POPMOVIES_MOVIEID = 5;
    private static final int COL_POPMOVIES_ORIGINAL_TITLE = 6;
    private static final int COL_POPMOVIES_OVERVIEW = 3;
    private static final int COL_POPMOVIES_COLUMN_POSTER_PATH = 1;
    private static final int COL_POPMOVIES_RELEASE_DATE = 4;
    private static final int COL_POPMOVIES_COLLECTION = 15;
    private static final int COL_REVIEW_AUTHOR = 2;
    private static final int COL_REVIEW_CONTENT = 3;
    private static final int COL_VIDEO_NAME = 3;

    private OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public MovieDetailAdapter(Context context, Cursor cursor, int numberOfReviews) {
        super(cursor);
        this.mContext = context;
        this.numberOfReviews = numberOfReviews;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_MOVIE_DETAIL;
        else if (position < (numberOfReviews + 1) && numberOfReviews != 0)
            return VIEW_TYPE_REVIEW;
        else
            return VIEW_TYPE_VIDEO;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case VIEW_TYPE_MOVIE_DETAIL:
                view = View.inflate(mContext, R.layout.item_detail, null);
                return new DetailViewHolder(view);
            case VIEW_TYPE_REVIEW:
                view = View.inflate(mContext, R.layout.item_review, null);
                return new ReviewViewHolder(view, clickListener);
            case VIEW_TYPE_VIDEO:
                view = View.inflate(mContext, R.layout.item_video, null);
                return new VideoViewHolder(view, clickListener);
            default:
                throw new UnsupportedOperationException("Unknown viewType: " + viewType);
        }
    }

    public int isCollected(int collection, TextView tv_btn_favorite, TextView tv_btn_mark_as) {
        if (collection == 1) {
            tv_btn_mark_as.setText(R.string.mark_as);
            tv_btn_favorite.setText(R.string.favorite);
            return 1;
        } else {
            tv_btn_mark_as.setText(R.string.cancel);
            tv_btn_favorite.setText(R.string.cancel_collection);
            return 0;
        }
    }

    int collection;
    String movieId;
    DetailViewHolder detailViewHolder;

    public static String sMovieIdSelection(String movieId) {
        return PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID + "=" + movieId;
    }

    @Override
    protected void onBindViewHolderCursor(RecyclerView.ViewHolder holder, Cursor cursor) {
        mCursor = cursor;
        if (holder instanceof DetailViewHolder) {
            movieId = mCursor.getString(COL_POPMOVIES_MOVIEID);
            detailViewHolder = (DetailViewHolder) holder;
            Picasso.with(mContext).load(cursor.getString(COL_POPMOVIES_COLUMN_POSTER_PATH)).into(detailViewHolder.iv_movies);
            detailViewHolder.tv_title.setText(cursor.getString(COL_POPMOVIES_ORIGINAL_TITLE));
            detailViewHolder.tv_overview.setText(cursor.getString(COL_POPMOVIES_OVERVIEW));
            detailViewHolder.tv_data.setText(cursor.getString(COL_POPMOVIES_RELEASE_DATE));
            detailViewHolder.tv_averages.setText(cursor.getString(COL_POPMOVIES_VOTE_AVERAGE));
            //初始化按钮状态
            collection = mCursor.getInt(COL_POPMOVIES_COLLECTION);
            if (collection == 1) {
                detailViewHolder.tv_btn_mark_as.setText(R.string.mark_as);
                detailViewHolder.tv_btn_favorite.setText(R.string.favorite);
            } else {
                detailViewHolder.tv_btn_mark_as.setText(R.string.cancel);
                detailViewHolder.tv_btn_favorite.setText(R.string.cancel_collection);
            }
            Log.i(TAG_LOG, "collection :" + collection);
        } else if (holder instanceof ReviewViewHolder) {
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
            reviewViewHolder.tv_review_author.setText(cursor.getString(COL_REVIEW_AUTHOR));
            reviewViewHolder.tv_review_content.setText(cursor.getString(COL_REVIEW_CONTENT));
        } else if (holder instanceof VideoViewHolder) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.tv_trailer.setText(cursor.getString(COL_VIDEO_NAME));
        }
    }


    class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_title;
        private ImageView iv_movies;
        private TextView tv_overview;
        private TextView tv_data;
        private TextView tv_averages;
        private TextView tv_btn_mark_as;
        private TextView tv_btn_favorite;
        private RelativeLayout relativeLayout_btn;

        public DetailViewHolder(View itemView) {
            super(itemView);
            iv_movies = (ImageView) itemView.findViewById(R.id.iv_movies);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_overview = (TextView) itemView.findViewById(R.id.tv_overview);
            tv_data = (TextView) itemView.findViewById(R.id.tv_data);
            tv_averages = (TextView) itemView.findViewById(R.id.tv_averages);
            tv_btn_mark_as = (TextView) itemView.findViewById(R.id.tv_btn_mark_as);
            tv_btn_favorite = (TextView) itemView.findViewById(R.id.tv_btn_favorite);
            relativeLayout_btn = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_btn);
            relativeLayout_btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            ContentResolver contentResolver = mContext.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION, isCollected(1-collection, detailViewHolder.tv_btn_favorite, detailViewHolder.tv_btn_mark_as));
            contentResolver.update(PopMoviesContract.PopMoviesEntry.CONTENT_URI, values, sMovieIdSelection(movieId), null);
            //更新按钮状态，点击时变成 “mark as favorite”或者“cancel collected”
            Cursor c = contentResolver.query(PopMoviesContract.PopMoviesEntry.CONTENT_URI, null, sMovieIdSelection(movieId), null, null);
            c.moveToFirst();
            collection = c.getInt(c.getColumnIndex(PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION));
            c.close();




        }
    }

}

