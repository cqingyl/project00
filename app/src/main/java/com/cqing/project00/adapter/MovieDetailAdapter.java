
package com.cqing.project00.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cqing.project00.R;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.other.RecyclerViewCursorAdapter;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cqing on 2016/11/22.
 */

public class MovieDetailAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> {
    private static final String TAG_LOG = MovieDetailAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_MOVIE_DETAIL = 0;
    private static final int VIEW_TYPE_REVIEW = 1;
    private static final int VIEW_TYPE_VIDEO = 2;

    private Context mContext;
    private int numberOfReviews;
    private ContentResolver contentResolver;
    //收藏的状态值
    private int collection;
    private String movieId;
    private DetailViewHolder detailViewHolder;

    private static final int COL_POPMOVIES_VOTE_AVERAGE = 13;
    private static final int COL_POPMOVIES_POPULARITY = 10;
    private static final int COL_POPMOVIES_MOVIEID = 5;
    private static final int COL_POPMOVIES_ORIGINAL_TITLE = 6;
    private static final int COL_POPMOVIES_OVERVIEW = 3;
    private static final int COL_POPMOVIES_COLUMN_POSTER_PATH = 1;
    private static final int COL_POPMOVIES_RELEASE_DATE = 4;
    private static final int COL_POPMOVIES_COLLECTION = 15;
    private static final int COL_REVIEW_AUTHOR = 2;
    private static final int COL_REVIEW_CONTENT = 3;
    private static final int COL_VIDEO_NAME = 3;

    private static OnItemClickListener clickListener;

    public MovieDetailAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.mContext = context;
        contentResolver = mContext.getContentResolver();
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static interface OnItemClickListener {
        void onClick(View view, int position);
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
                return new ReviewViewHolder(view);
            case VIEW_TYPE_VIDEO:
                view = View.inflate(mContext, R.layout.item_video, null);
                return new VideoViewHolder(view);
            default:
                throw new UnsupportedOperationException("Unknown viewType: " + viewType);
        }
    }

    private void changeButton(int collection, TextView tv_btn_favorite, TextView tv_btn_mark_as) {
        if (collection == 1) {
            tv_btn_mark_as.setText(R.string.mark_as);
            tv_btn_favorite.setText(R.string.favorite);
        } else {
            tv_btn_mark_as.setText(R.string.cancel);
            tv_btn_favorite.setText(R.string.cancel_collection);
        }
    }

    public static String sMovieIdSelection(String movieId) {
        return PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID + "=" + movieId;
    }

    @Override
    protected void onBindViewHolderCursor(RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof DetailViewHolder) {
            movieId = cursor.getString(COL_POPMOVIES_MOVIEID);
            detailViewHolder = (DetailViewHolder) holder;
            Picasso.with(mContext)
                    .load(cursor.getString(COL_POPMOVIES_COLUMN_POSTER_PATH))
                    .into(detailViewHolder.iv_movies);
            detailViewHolder.tv_title.setText(cursor.getString(COL_POPMOVIES_ORIGINAL_TITLE));
            detailViewHolder.tv_overview.setText(cursor.getString(COL_POPMOVIES_OVERVIEW));
            detailViewHolder.tv_data.setText(cursor.getString(COL_POPMOVIES_RELEASE_DATE));
            detailViewHolder.tv_averages.setText(cursor.getString(COL_POPMOVIES_VOTE_AVERAGE));
            detailViewHolder.tv_popularity.setText(cursor.getString(COL_POPMOVIES_POPULARITY));
            //初始化按钮状态
            collection = cursor.getInt(COL_POPMOVIES_COLLECTION);
            changeButton(collection,detailViewHolder.tv_btn_favorite, detailViewHolder.tv_btn_mark_as);
        } else if (holder instanceof ReviewViewHolder) {
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
            reviewViewHolder.tv_review_author.setText(cursor.getString(COL_REVIEW_AUTHOR));
            reviewViewHolder.tv_review_content.setText(cursor.getString(COL_REVIEW_CONTENT));
        } else if (holder instanceof VideoViewHolder) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.tv_trailer.setText(cursor.getString(COL_VIDEO_NAME));
        }
    }


    public class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_title) TextView tv_title;
        @BindView(R.id.iv_movies) ImageView iv_movies;
        @BindView(R.id.tv_overview) TextView tv_overview;
        @BindView(R.id.tv_data) TextView tv_data;
        @BindView(R.id.tv_averages) TextView tv_averages;
        @BindView(R.id.tv_btn_mark_as) TextView tv_btn_mark_as;
        @BindView(R.id.tv_btn_favorite) TextView tv_btn_favorite;
        @BindView(R.id.relativeLayout_btn) RelativeLayout relativeLayout_btn;
        @BindView(R.id.tv_popularity) TextView tv_popularity;

        public DetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            relativeLayout_btn.setOnClickListener(this);
        }
        //点击 DetailViewHolder的item 不会有效果，但是点击 item当中的一个按钮触发
        //本来这里也应该通过OnItemClickListener 回调，但是回调后，实现那边不知怎么改变 tv_btn_favorite，tv_btn_mark_as。
        @Override
        public void onClick(View view) {
            //更新按钮状态，点击时变成 “mark as favorite”或者“cancel collected”
            new ButtonAsyncTask().execute();
        }
    }

    public class ButtonAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues values = new ContentValues();
            values.put(PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION, 1-collection);
            contentResolver.update(PopMoviesContract.PopMoviesEntry.CONTENT_URI, values, sMovieIdSelection(movieId), null);
            Cursor c = contentResolver.query(PopMoviesContract.PopMoviesEntry.CONTENT_URI, null, sMovieIdSelection(movieId), null, null);
            c.moveToFirst();
            collection = c.getInt(c.getColumnIndex(PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION));
            c.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            changeButton(collection, detailViewHolder.tv_btn_favorite, detailViewHolder.tv_btn_mark_as);
        }
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_review_author) TextView tv_review_author;
        @BindView(R.id.tv_review_content) TextView tv_review_content;
        @BindView(R.id.ll_review) LinearLayout ll_review;
        public ReviewViewHolder( View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ll_review.setOnClickListener(this);

        }

        //当点击属于 ReviewViewHolder 的item 会回调
        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(itemView, getAdapterPosition());
            }

        }
    }
    public static class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_trailer) TextView tv_trailer;
        @BindView(R.id.ll_trailer) LinearLayout ll_trailer;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ll_trailer.setOnClickListener(this);
        }

        //当点击属于 VideoViewHolder 的item 会回调
        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(itemView, getAdapterPosition());
            }
        }
    }
}

