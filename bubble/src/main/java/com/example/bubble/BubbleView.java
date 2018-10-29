package com.example.bubble;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

//随机产生气泡
//估值器、贝塞尔三阶曲线
public class BubbleView extends RelativeLayout {

    private int viewWidth = dp2pix(7), viewHeight = dp2pix(7);

    private int riseHeightBase = DisplayUtil.dp2px(getContext(),70);
    private int riseDuration = 3600;

    private int riseWithOffset = 100;
    private int riseHeightOffset = 100;
    private int controlOffset = 30;

    private float maxScale = 2.0f;
    private float minScale = 1.0f;

    private int delay = 500;
    private long period = 1200;
    private Disposable mDisposable;

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BubbleView setRiseDuration(int riseDuration) {
        this.riseDuration = riseDuration;
        return this;
    }

    public BubbleView setScaleAnimation(float maxScale, float minScale) {
        this.maxScale = maxScale;
        this.minScale = minScale;
        return this;
    }

    public BubbleView setRiseWithOffset(int px) {
        this.riseWithOffset = px;
        return this;
    }

    public BubbleView setPeriod(long period) {
        this.period = period;
        return this;
    }

    public BubbleView setItemViewWH(int viewWidth, int viewHeight) {
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        return this;
    }


    public void stop() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    public void startAnimation() {
        if (mDisposable != null){
            return;
        }
        Observable.interval(delay, period, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onNext(Long aLong) {
                        bubbleAnimation();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void bubbleAnimation() {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        int rankWidth = getWidth();
        int rankHeight = getHeight();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(viewWidth, viewHeight);
        ImageView tempImageView = new ImageView(getContext());
        tempImageView.setBackground(getResources().getDrawable(R.drawable.shape_bubble));
        addView(tempImageView, layoutParams);

        ObjectAnimator riseAlphaAnimator = ObjectAnimator.ofFloat(tempImageView, "alpha", 0.4f, 1.0f, 0.0f);
        riseAlphaAnimator.setDuration(riseDuration);
        riseAlphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator riseScaleXAnimator = ObjectAnimator.ofFloat(tempImageView, "scaleX", minScale, maxScale);
        riseScaleXAnimator.setDuration(riseDuration);
        riseScaleXAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator riseScaleYAnimator = ObjectAnimator.ofFloat(tempImageView, "scaleY", minScale, maxScale);
        riseScaleYAnimator.setDuration(riseDuration);
        riseScaleYAnimator.setInterpolator(new AccelerateInterpolator());

        ValueAnimator valueAnimator = getBesselAnimator(tempImageView, rankWidth, rankHeight);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(valueAnimator).with(riseAlphaAnimator).with(riseScaleXAnimator).with(riseScaleYAnimator);
        animatorSet.start();
    }

    private ValueAnimator getBesselAnimator(final ImageView imageView, int rankWidth, int rankHeight) {
        rankWidth = (int) (Math.random() * rankWidth);
        int tempHeight = (int) (Math.random() * rankHeight);
        if (tempHeight < riseHeightBase +  DisplayUtil.dp2px(getContext(),riseHeightOffset)) {
            rankHeight = riseHeightBase +  DisplayUtil.dp2px(getContext(),riseHeightOffset);
        } else {
            rankHeight = tempHeight;
        }

        //起点
        float point0[] = new float[2];
        point0[0] = rankWidth;
        point0[1] = rankHeight;

        //终点
        float point3[] = new float[2];
        switch ((int) (Math.random() * 2)) {
            case 0:
                point3[0] = (float) (rankWidth - Math.random() * DisplayUtil.dp2px(getContext(),riseWithOffset));
                break;
            case 1:
                point3[0] = (float) (rankWidth + Math.random() * DisplayUtil.dp2px(getContext(),riseWithOffset));
                break;
        }
        point3[1] = rankHeight - riseHeightBase - DisplayUtil.dp2px(getContext(),riseHeightOffset);

        //控制点
        float point1[] = new float[2];
        point1[0] = (float) (rankWidth + DisplayUtil.dp2px(getContext(),controlOffset) / 2 - Math.random() * DisplayUtil.dp2px(getContext(),controlOffset));
        point1[1] = (float) (rankHeight - Math.random() * DisplayUtil.dp2px(getContext(),riseWithOffset));

        float point2[] = new float[2];
        point2[0] = (float) (rankWidth + DisplayUtil.dp2px(getContext(),controlOffset) / 2 - Math.random() * DisplayUtil.dp2px(getContext(),controlOffset));
        point2[1] = (float) (rankHeight - Math.random() * DisplayUtil.dp2px(getContext(),riseWithOffset));

        BesselEvaluator besselEvaluator = new BesselEvaluator(point1, point2);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(besselEvaluator, point0, point3);
        valueAnimator.setDuration(riseDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float[] currentPosition = new float[2];
                currentPosition = (float[]) animation.getAnimatedValue();
                imageView.setTranslationX(currentPosition[0]);
                imageView.setTranslationY(currentPosition[1]);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(imageView);
                imageView.setBackground(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return valueAnimator;
    }

    private class BesselEvaluator implements TypeEvaluator<float[]> {
        private float point1[] = new float[2], point2[] = new float[2];

        public BesselEvaluator(float[] point1, float[] point2) {
            this.point1 = point1;
            this.point2 = point2;
        }

        @Override
        public float[] evaluate(float fraction, float[] point0, float[] point3) {
            float[] currentPosition = new float[2];
            currentPosition[0] = point0[0] * (1 - fraction) * (1 - fraction) * (1 - fraction)
                    + point1[0] * 3 * fraction * (1 - fraction) * (1 - fraction)
                    + point2[0] * 3 * (1 - fraction) * fraction * fraction
                    + point3[0] * fraction * fraction * fraction;
            currentPosition[1] = point0[1] * (1 - fraction) * (1 - fraction) * (1 - fraction)
                    + point1[1] * 3 * fraction * (1 - fraction) * (1 - fraction)
                    + point2[1] * 3 * (1 - fraction) * fraction * fraction
                    + point3[1] * fraction * fraction * fraction;
            return currentPosition;
        }
    }

    private int dp2pix(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pix = (int) (dp * scale + 0.5f);
        return pix;
    }
}
