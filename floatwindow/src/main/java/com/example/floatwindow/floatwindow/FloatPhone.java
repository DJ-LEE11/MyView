package com.example.floatwindow.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.example.floatwindow.base.FloatView;
import com.example.floatwindow.util.Miui;


/**
 * Created by yhao on 17-11-14.
 * https://github.com/yhaolpz
 */

public class FloatPhone extends FloatView {

    private Context mContext;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mView;
    private int mX, mY;
    private boolean isRemove = false;

     FloatPhone(Context applicationContext) {
        mContext = applicationContext;
        mWindowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.windowAnimations = 0;
    }

    @Override
    public void setSize(int width, int height) {
        mLayoutParams.width = width;
        mLayoutParams.height = height;
    }

    @Override
    public void setView(View view) {
        mView = view;
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mLayoutParams.gravity = gravity;
        mLayoutParams.x = mX = xOffset;
        mLayoutParams.y = mY = yOffset;
    }


    @Override
    public void addView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (Miui.rom()) {//小米
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        mWindowManager.addView(mView, mLayoutParams);
        isRemove = false;
    }

    @Override
    public void dismiss() {
        isRemove = true;
        mWindowManager.removeViewImmediate(mView);
        mWindowManager = null;
    }

    @Override
    public void updateXY(int x, int y) {
        if (isRemove)
            return;
        mLayoutParams.x = mX = x;
        mLayoutParams.y = mY = y;
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    @Override
    public void updateX(int x) {
        if (isRemove)
            return;
        mLayoutParams.x = mX = x;
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    @Override
    public void updateY(int y) {
        if (isRemove)
            return;
        mLayoutParams.y = mY = y;
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    @Override
    public int getX() {
        return mX;
    }

    @Override
    public int getY() {
        return mY;
    }


}
