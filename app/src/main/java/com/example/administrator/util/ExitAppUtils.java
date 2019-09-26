package com.example.administrator.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * @author hubujun
 * @date 2018/8/21
 */
public class ExitAppUtils {
    private static long exitTime;

    public static void exitApp(Context context) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(context,"再按一次退出",Toast.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }
}
