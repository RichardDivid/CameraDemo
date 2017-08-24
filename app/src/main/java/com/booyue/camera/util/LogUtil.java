package com.booyue.camera.util;

import android.util.Log;

/**
 * Created by Administrator on 2017/8/22.
 */

public class LogUtil {
    private static final boolean IS_OPEN = true;

    private static final String TAG = LogUtil.class.getSimpleName();

    public static void d(String msg){
        if(IS_OPEN){
            Log.d(TAG, msg);
        }
    }

}
