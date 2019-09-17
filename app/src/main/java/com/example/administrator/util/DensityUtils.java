package com.example.administrator.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * 今日头条适配工具，放在BaseActivity中使用
 */

public class DensityUtils {
    private static float sNonCompatDensity;
    private static float sNonCompatScaleDensity;
    private static final int WIDTH_DP = 360;

    public static void setCustomDensity(Activity activity, final Application application) {
        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (sNonCompatDensity == 0) {
            sNonCompatDensity = appDisplayMetrics.density;
            sNonCompatScaleDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNonCompatScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        //假设设计图的宽度是360dp,则适配后的 density = 设备真实宽(单位px) / 360
        final float targetDensity = appDisplayMetrics.widthPixels / WIDTH_DP;
        final float targetScaleDensity = targetDensity * (sNonCompatScaleDensity / sNonCompatDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);

        appDisplayMetrics.density = appDisplayMetrics.scaledDensity = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaleDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDispalyMetrics = activity.getResources().getDisplayMetrics();
        activityDispalyMetrics.densityDpi = targetDensityDpi;
        activityDispalyMetrics.scaledDensity = targetScaleDensity;
        activityDispalyMetrics.density = activityDispalyMetrics.scaledDensity = targetDensity;
    }
}
