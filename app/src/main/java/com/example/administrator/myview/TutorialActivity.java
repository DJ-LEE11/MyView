package com.example.administrator.myview;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.tutorialview.TutorialDelegate;

import static com.example.tutorialview.TutorialDelegate.NEW_GUIDE_TUTORIAL;

public class TutorialActivity extends BaseActivity {

    private ViewGroup mRootView;
    private View tvGuide;
    private TutorialDelegate mTutorialDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        initView();
    }

    private void initView() {
        mRootView = findViewById(R.id.rootView);
        tvGuide = findViewById(R.id.tvGuide);
        mTutorialDelegate = new TutorialDelegate(this, mRootView);
        tvGuide.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        showTutorial();
                        tvGuide.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    private void showTutorial() {
        if (getViewRect(tvGuide) != null) {
            mTutorialDelegate.showTutorial(getViewRect(tvGuide), NEW_GUIDE_TUTORIAL);
        }
    }

    /**
     * 获取某个view的区域范围
     *
     * @return
     */
    public static Rect getViewRect(View view){
        if (view != null) {
            int[] l = new int[2];
            view.getLocationOnScreen(l);
            int x = l[0];
            int y = l[1];
            int w = view.getWidth();
            int h = view.getHeight();
            return new Rect(x, y, x + w, y + h);
        }
        return null;
    }
}
