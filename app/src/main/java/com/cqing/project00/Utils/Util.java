package com.cqing.project00.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
    public Intent startYouTuBe(Cursor cursor ){
        Intent i = null;
//        int movieId = cursor.getInt(ID);
//
//        final String movie = "movie";
//        String videos = "videos";
//        //  /movie/{id}/videos
//        Uri buildUri = Uri.parse(Host).buildUpon().appendPath(movie).appendPath(movieId).appendPath(videos);
//        i = new Intent(Intent.ACTION_VIEW, buildUri);
        return i;

    }


}
