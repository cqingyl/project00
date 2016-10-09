package com.cqing.project00.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Cqing on 2016/10/9.
 */

public class ToastUtil {
    private static Toast mToast=null;

    public static void show(Context context, String msg){
        if(mToast==null){
            mToast=Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }
}
