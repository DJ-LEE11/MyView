package com.example.floatwindow.util;

import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * dp、sp 转换为 px 的工具类
 * 
 * @author fxsky 2012.11.12
 * 
 */
public class DisplayUtil {
	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * @return
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * @return
	 */
	public static int dp2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
	
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}

	/**
	 * 测量 View
	 *
	 * @param measureSpec
	 * @param defaultSize View 的默认大小
	 * @return
	 */
	public static int measure(int measureSpec, int defaultSize) {
		int result = defaultSize;
		int specMode = View.MeasureSpec.getMode(measureSpec);
		int specSize = View.MeasureSpec.getSize(measureSpec);

		if (specMode == View.MeasureSpec.EXACTLY) {
			result = specSize;
		} else if (specMode == View.MeasureSpec.AT_MOST) {
			result = Math.min(result, specSize);
		}
		return result;
	}


	/**
	 * 获取数值精度格式化字符串
	 *
	 * @param precision
	 * @return
	 */
	public static String getPrecisionFormat(int precision) {
		return "%." + precision + "f";
	}

	/**
	 * 反转数组
	 *
	 * @param arrays
	 * @param <T>
	 * @return
	 */
	public static <T> T[] reverse(T[] arrays) {
		if (arrays == null) {
			return null;
		}
		int length = arrays.length;
		for (int i = 0; i < length / 2; i++) {
			T t = arrays[i];
			arrays[i] = arrays[length - i - 1];
			arrays[length - i - 1] = t;
		}
		return arrays;
	}

	/**
	 * 测量文字高度
	 * @param paint
	 * @return
	 */
	public static float measureTextHeight(Paint paint) {
		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		return (Math.abs(fontMetrics.ascent) - fontMetrics.descent);
	}
}
