package com.example.tutorialview;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by mj.zeng on 2018/7/16.
 */

public class TutorialDelegate {

    public static final int NEW_GUIDE_TUTORIAL = 1;

    private Activity mContext;

    /// 界面
    private ViewGroup mParentView;
    private TutorialGuideView mTutorialGuideView;

    public TutorialDelegate(Activity context, ViewGroup parentView) {
        this.mContext = context;
        this.mParentView = parentView;
    }

    public void showTutorial(Rect rect, int tutorialType) {
        switch (tutorialType) {
            case NEW_GUIDE_TUTORIAL:
                showNewGuideTutorial(rect);
                break;
            default:
                break;
        }
    }

    private void showNewGuideTutorial(Rect rect) {
        if (mTutorialGuideView == null) {
            DisplayMetrics dm = new DisplayMetrics();
            mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            mTutorialGuideView = new TutorialGuideView(mContext, null);
            // 镂空区域
            mTutorialGuideView.setHoleRect(new RectF(width - dip2px(98), rect.top , width-dip2px(2) , rect.bottom));
            mTutorialGuideView.setClickable(true);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mParentView.addView(mTutorialGuideView, layoutParams);
            mTutorialGuideView.setCloseListener(new TutorialGuideView.ICloseListener() {
                @Override
                public void close() {
                    if (mParentView != null && mTutorialGuideView != null) {
                        mParentView.removeView(mTutorialGuideView);
                    }
                }
            });
        }
    }



    public boolean dismissTutorialView() {
        if (null != mTutorialGuideView && mTutorialGuideView.getVisibility() == View.VISIBLE) {
            mTutorialGuideView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
