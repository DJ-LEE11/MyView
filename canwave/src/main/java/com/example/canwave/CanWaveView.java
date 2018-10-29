package com.example.canwave;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class CanWaveView extends View {

    //底图
    private Bitmap mBitmap;
    //所有波浪
    private Canvas mWaveCanvas;
    private float mWaveClipRadius;

    //浪TOP
    private Paint mWaveTopPaint;
    private Path mPathTop;
    private int mTopStartColor = Color.parseColor("#6adafd");
    private int mTopEndColor = Color.parseColor("#6adafd");
    private double mTopHeightRatio = 0.42;//波浪占整个空间高度的比例
    private int mTopHeight;//波浪高度
    private int mTopSwingHeight = 43;//波浪振幅高
    private float mTopBit = 1 / 4f;//波浪比例

    //浪Middle
    private Paint mWaveMiddlePaint;
    private Path mPathMiddle;
    private int mMiddleStartColor = Color.parseColor("#66fffe");
    private int mMiddleCentreColor = Color.parseColor("#70aefd");
    private int mMiddleEndColor = Color.parseColor("#ac73fc");
    private double mMiddleHeightRatio = 0.34;
    private int mMiddleHeight;//波浪高度
    private int mMiddleSwingHeight = 20;
    private float mMiddleBit = 1 / 3f;

    //浪bottom
    private Paint mWaveBottomPaint;
    private Path mPathBottom;
    private int mBottomStartColor = Color.parseColor("#fd78fc");
    private int mBottomEndColor = Color.parseColor("#e7de74");
    private double mBottomHeightRatio = 0.20;
    private int mBottomHeight;//波浪高度
    private int mBottomSwingHeight = 15;
    private float mBottomBit = 1 / 4f;


    //其他
    private int mWidth;
    private int mHeight;

    private ValueAnimator mAnimator;
    private long mDuring = 1500;

    private int mWaveLength = 1000;//浪宽
    private int mOffset;
    private int mWaveCount;

    public CanWaveView(Context context) {
        this(context, null);
    }

    public CanWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initAnim();
    }

    public void startAnim() {
        if (mAnimator != null && !mAnimator.isStarted()) {
            mAnimator.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pauseAnim() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.pause();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void resume() {
        if (mAnimator != null && mAnimator.isPaused()) {
            mAnimator.resume();
        }
    }

    public void destory() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        end();
    }

    private void end() {
        if (mAnimator != null) {
            mAnimator.end();
            mAnimator = null;
        }
    }

    private void initAnim() {
        mAnimator = ValueAnimator.ofInt(0, mWaveLength);
        mAnimator.setDuration(mDuring);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }

    private void initPaint() {
        mWaveTopPaint = new Paint();
        mWaveTopPaint.setAntiAlias(true);
        mPathTop = new Path();

        mWaveMiddlePaint = new Paint();
        mWaveMiddlePaint.setAntiAlias(true);
        mPathMiddle = new Path();

        mWaveBottomPaint = new Paint();
        mWaveBottomPaint.setAntiAlias(true);
        mPathBottom = new Path();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mWaveCanvas = new Canvas(mBitmap);

        mTopHeight = (int) (mHeight * (1 - mTopHeightRatio));
        mMiddleHeight = (int) (mHeight * (1 - mMiddleHeightRatio));
        mBottomHeight = (int) (mHeight * (1 - mBottomHeightRatio));

        mWaveCount = (int) Math.round(mHeight / mWaveLength + 1.5);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        clipWaveCanvas();
        super.onDraw(canvas);
        mBitmap.eraseColor(Color.parseColor("#00000000"));
        paintTopWave(mWaveLength, mOffset);
        paintMiddleWave((int) (mWaveLength * 0.7), (int) (mOffset * 0.7));
        paintBottomWave((int) (mWaveLength * 0.85), (int) (mOffset * 0.85));
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    private void paintTopWave(int waveLength, int offset) {
        mPathTop.reset();
        mPathTop.moveTo(-waveLength + offset, mTopHeight);
        for (int i = 0; i < mWaveCount; i++) {//50是波纹的大小

            mPathTop.quadTo((-waveLength * (1 - mTopBit)) + (i * waveLength) + offset, mTopHeight + mTopSwingHeight,
                    (-waveLength / 2) + (i * waveLength) + offset, mTopHeight);

            mPathTop.quadTo((-waveLength * mTopBit) + (i * waveLength) + offset, mTopHeight - mTopSwingHeight,
                    i * waveLength + offset, mTopHeight);
        }

        mPathTop.lineTo(mWidth, mHeight);
        mPathTop.lineTo(0, mHeight);
        mPathTop.close();
        LinearGradient linearGradient = new LinearGradient(0, mTopHeight, 0, mHeight, mTopStartColor, mTopEndColor, Shader.TileMode.CLAMP);
        mWaveTopPaint.setShader(linearGradient);
        mWaveCanvas.drawPath(mPathTop, mWaveTopPaint);
    }

    private void paintMiddleWave(int waveLength, int offset) {
        mPathMiddle.reset();
        mPathMiddle.moveTo(-waveLength + offset, mMiddleHeight);
        for (int i = 0; i < mWaveCount; i++) {

            mPathMiddle.quadTo((-waveLength * (1 - mMiddleBit)) + (i * waveLength) + offset, mMiddleHeight + mMiddleSwingHeight,
                    (-waveLength / 2) + (i * waveLength) + offset, mMiddleHeight);

            mPathMiddle.quadTo((-waveLength * mMiddleBit) + (i * waveLength) + offset, mMiddleHeight - mMiddleSwingHeight,
                    i * waveLength + offset, mMiddleHeight);
        }

        mPathMiddle.lineTo(mWidth, mHeight);
        mPathMiddle.lineTo(0, mHeight);
        mPathMiddle.close();

        LinearGradient linearGradient = new LinearGradient(0, mMiddleHeight, 0, mHeight, new int[]{mMiddleStartColor,mMiddleCentreColor,mMiddleEndColor},null, Shader.TileMode.CLAMP);
        mWaveMiddlePaint.setShader(linearGradient);
        mWaveCanvas.drawPath(mPathMiddle, mWaveMiddlePaint);
    }

    private void paintBottomWave(int waveLength, int offset) {
        mPathBottom.reset();
        mPathBottom.moveTo(-waveLength + offset, mBottomHeight);
        for (int i = 0; i < mWaveCount; i++) {

            mPathBottom.quadTo((-waveLength * (1 - mBottomBit)) + (i * waveLength) + offset, mBottomHeight + mBottomSwingHeight,
                    (-waveLength / 2) + (i * waveLength) + offset, mBottomHeight);

            mPathBottom.quadTo((-waveLength * mBottomBit) + (i * waveLength) + offset, mBottomHeight - mBottomSwingHeight,
                    i * waveLength + offset, mBottomHeight);
        }

        mPathBottom.lineTo(mWidth, mHeight);
        mPathBottom.lineTo(0, mHeight);
        mPathBottom.close();
        LinearGradient linearGradient = new LinearGradient(0, mBottomHeight, 0, mHeight, mBottomStartColor, mBottomEndColor, Shader.TileMode.CLAMP);
        mWaveBottomPaint.setShader(linearGradient);
        mWaveCanvas.drawPath(mPathBottom, mWaveBottomPaint);
    }

    //裁剪波浪圆角
    private void clipWaveCanvas() {
        Path path = new Path();
        mWaveClipRadius = getWidth() / 7;
        float[] radiusArray = {mWaveClipRadius, mWaveClipRadius, mWaveClipRadius, mWaveClipRadius, mWaveClipRadius, mWaveClipRadius, mWaveClipRadius, mWaveClipRadius};
        path.addRoundRect(new RectF(dp2px(5), 0, getWidth() - dp2px(5), getHeight() - dp2px(5)), radiusArray, Path.Direction.CW);
        mWaveCanvas.clipPath(path);

    }

    public int dp2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
