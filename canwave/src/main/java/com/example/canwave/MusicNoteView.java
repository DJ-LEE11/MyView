package com.example.canwave;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class MusicNoteView extends RelativeLayout {
    private List<Drawable> drawableList = new ArrayList<>();

    private int viewWidth = dp2pix(32), viewHeight = dp2pix(32);

    private int riseDuration = 3000;

    private int originsOffset = 10;

    private float maxScale = 1.0f;
    private float minScale = 1.0f;

    private long period = 1000;
    private Disposable mDisposable;
    private LinkedList<AnimatorSet> mAnimatorSetList;

    public MusicNoteView(Context context) {
        this(context, null);
    }

    public MusicNoteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public MusicNoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDrawableList();
        mAnimatorSetList = new LinkedList<>();
    }

    public MusicNoteView setDrawableList() {
        drawableList.add(getResources().getDrawable(R.drawable.ic_musical_star));
        drawableList.add(getResources().getDrawable(R.drawable.ic_musical_love));
        drawableList.add(getResources().getDrawable(R.drawable.ic_musical_note));
        return this;
    }

    public MusicNoteView setRiseDuration(int riseDuration) {
        this.riseDuration = riseDuration;
        return this;
    }

    public MusicNoteView setOriginsOffset(int px) {
        this.originsOffset = px;
        return this;
    }

    public MusicNoteView setScaleAnimation(float maxScale, float minScale) {
        this.maxScale = maxScale;
        this.minScale = minScale;
        return this;
    }

    public MusicNoteView setPeriod(long period) {
        this.period = period;
        return this;
    }

    public MusicNoteView setItemViewWH(int viewWidth, int viewHeight) {
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        return this;
    }


    public void stop(boolean iSCleanAnim) {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        if (mAnimatorSetList.size() != 0 && iSCleanAnim) {
            for (AnimatorSet animatorSet : mAnimatorSetList) {
                if (animatorSet != null && animatorSet.isRunning()) {
                    animatorSet.end();
                }
            }
        }
        mAnimatorSetList.clear();
    }

    public void startAnimation(long delay) {
        if (mDisposable != null) {
            return;
        }
        Observable.interval(delay, period, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

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

    public void startAnimation() {
        startAnimation(0);
    }


    private void bubbleAnimation() {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        int rankWidth = getWidth();
        int rankHeight = getHeight();

        int heartDrawableIndex;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(viewWidth, viewHeight);
        ImageView tempImageView = new ImageView(getContext());
        heartDrawableIndex = (int) (drawableList.size() * Math.random());
        tempImageView.setImageDrawable(drawableList.get(heartDrawableIndex));
        addView(tempImageView, layoutParams);

        ObjectAnimator riseAlphaAnimator = ObjectAnimator.ofFloat(tempImageView, "alpha", 0.8f, 1.0f, 0.0f);
        riseAlphaAnimator.setDuration(riseDuration);

        ObjectAnimator riseScaleXAnimator = ObjectAnimator.ofFloat(tempImageView, "scaleX", minScale, maxScale);
        riseScaleXAnimator.setDuration(riseDuration);

        ObjectAnimator riseScaleYAnimator = ObjectAnimator.ofFloat(tempImageView, "scaleY", minScale, maxScale);
        riseScaleYAnimator.setDuration(riseDuration);

        ValueAnimator valueAnimator = getBesselAnimator(tempImageView, rankWidth, rankHeight);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(valueAnimator).with(riseAlphaAnimator).with(riseScaleXAnimator).with(riseScaleYAnimator);
        mAnimatorSetList.add(animatorSet);
        animatorSet.start();
    }

    private ValueAnimator getBesselAnimator(final ImageView imageView, int rankWidth, int rankHeight) {
        //起点
        float point0[] = new float[2];
        switch ((int) (Math.random() * 2)) {
            case 0:
                point0[0] = (float) (rankWidth / 2 - Math.random() * dp2pix(originsOffset) - dp2pix(5));
                break;
            case 1:
                point0[0] = (float) (rankWidth / 2 + Math.random() * dp2pix(originsOffset) -dp2pix(5));
                break;
        }
        point0[1] = rankHeight;


        //终点
        float point3[] = new float[2];
        switch ((int) (Math.random() * 2)) {
            case 0:
                point3[0] = (float) (rankWidth / 2 - Math.random() * dp2pix(originsOffset));
                break;
            case 1:
                point3[0] = (float) (rankWidth / 2 + Math.random() * dp2pix(originsOffset));
                break;
        }
        point3[1] = 0;

        //控制点
        rankWidth = rankWidth / 3 * 2;
        float point1[] = new float[2];
        point1[0] = (float) ((rankWidth) * (0.10)) + (float) (Math.random() * (rankWidth) * (0.8));
        point1[1] = (float) (rankHeight - Math.random() * rankHeight * (0.5));

        float point2[] = new float[2];
        point2[0] = (float) (Math.random() * rankWidth);
        point2[1] = (float) (Math.random() * (rankHeight - point1[1]));


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

            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(imageView);
                imageView.setImageDrawable(null);
                mAnimatorSetList.remove(animation);
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
