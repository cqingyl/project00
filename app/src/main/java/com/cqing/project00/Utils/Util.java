package com.cqing.project00.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.cqing.project00.data.PopMoviesContract;

import static com.cqing.project00.Project00.URL.HOST;

/**
 * Created by Cqing on 2016/10/9.
 */

public class Util {
    private Util(){}

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
    public static Intent getYouTuBeIntent(Uri uri ){
        Intent i = null;
        String movieId = String.valueOf(PopMoviesContract.PopMoviesEntry.getMovieId(uri));

        final String movie = "movie";
        String videos = "videos";
        //  /movie/{id}/videos
        Uri ii = Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM");
        Uri buildUri = Uri.parse(HOST).buildUpon().appendPath(movie).appendPath(movieId).appendPath(videos).build();
        i = new Intent(Intent.ACTION_VIEW, ii);
        return i;

    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



}
