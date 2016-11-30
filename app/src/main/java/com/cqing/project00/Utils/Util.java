package com.cqing.project00.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Cqing on 2016/10/9.
 */

public class Util {
    private Util() {
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
    public  static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        PackageManager pm ;
        try {
            pm = context.getPackageManager();
            packageInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }
    public static boolean isIntentionToUse(Context context, Intent intent){
        if (intent.resolveActivity(context.getPackageManager()) != null)
            return true;
        else
            return false;
    }
    //保留两位小数
    public static double formatDouble(double d) {
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return bg.doubleValue();
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * */
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