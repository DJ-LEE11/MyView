package com.example.bubble.wave;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.example.bubble.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.parseFloat;

/**
 * 多重水波纹
 */
@SuppressWarnings("unused")
public class MultiWave extends ViewGroup {

    private Paint mPaint = new Paint();
    private Matrix mMatrix = new Matrix();
    private List<Wave> mltWave = new ArrayList<>();
    private int mWaveHeight;
    private int mStartColor;
    private int mCloseColor;
    private int mGradientAngle;
    private boolean mIsRunning;
    private float mVelocity;
    private float mColorAlpha;
    private float mProgress;
    private long mLastTime = 0;

    public MultiWave(Context context) {
        this(context, null, 0);
    }

    public MultiWave(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiWave(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultiWave);

        mWaveHeight = ta.getDimensionPixelOffset(R.styleable.MultiWave_mwhWaveHeight, dp2px(50));
        mStartColor = ta.getColor(R.styleable.MultiWave_mwhStartColor, 0xFF056CD0);
        mCloseColor = ta.getColor(R.styleable.MultiWave_mwhCloseColor, 0xFF31AFFE);
        mColorAlpha = ta.getFloat(R.styleable.MultiWave_mwhColorAlpha, 0.45f);
        mProgress = ta.getFloat(R.styleable.MultiWave_mwhProgress, 1f);
        mVelocity = ta.getFloat(R.styleable.MultiWave_mwhVelocity, 1f);
        mGradientAngle = ta.getInt(R.styleable.MultiWave_mwhGradientAngle, 45);
        mIsRunning = ta.getBoolean(R.styleable.MultiWave_mwhIsRunning, true);

        if (ta.hasValue(R.styleable.MultiWave_mwhWaves)) {
            setTag(ta.getString(R.styleable.MultiWave_mwhWaves));
        } else if (getTag() == null) {
            setTag("70,25,1.4,1.4,-26\n" +
                    "100,5,1.4,1.2,15\n" +
                    "420,0,1.15,1,-10\n" +
                    "520,10,1.7,1.5,20\n" +
                    "220,0,1,1,-15");
        }

        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateWavePath(w, h);
        updateLinearGradient(w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mltWave.size() > 0 && mPaint != null) {
            View thisView = this;
            int height = thisView.getHeight();
            long thisTime = System.currentTimeMillis();
            for (Wave wave : mltWave) {
                mMatrix.reset();
                canvas.save();
                if (mLastTime > 0 && wave.velocity != 0) {
                    float offsetX = (wave.offsetX - (wave.velocity * mVelocity * (thisTime - mLastTime) / 1000f));
                    if (-wave.velocity > 0) {
                        offsetX %= wave.width / 2;
                    } else {
                        while (offsetX < 0) {
                            offsetX += (wave.width / 2);
                        }
                    }
                    wave.offsetX = offsetX;
                    mMatrix.setTranslate(offsetX, (1 - mProgress) * height);
                    canvas.translate(-offsetX, -wave.offsetY - (1 - mProgress) * height);
                } else {
                    mMatrix.setTranslate(wave.offsetX, (1 - mProgress) * height);
                    canvas.translate(-wave.offsetX, -wave.offsetY - (1 - mProgress) * height);
                }
                mPaint.getShader().setLocalMatrix(mMatrix);
                canvas.drawPath(wave.path, mPaint);
                canvas.restore();
            }
            mLastTime = thisTime;
        }
        if (mIsRunning) {
            invalidate();
        }
    }

    private void updateLinearGradient(int width, int height) {
        int startColor = ColorUtils.setAlphaComponent(mStartColor, (int) (mColorAlpha * 255));
        int closeColor = ColorUtils.setAlphaComponent(mCloseColor, (int) (mColorAlpha * 255));
        double w = width;
        double h = height * mProgress;
        double r = Math.sqrt(w * w + h * h) / 2;
        double y = r * Math.sin(2 * Math.PI * mGradientAngle / 360);
        double x = r * Math.cos(2 * Math.PI * mGradientAngle / 360);
        mPaint.setShader(new LinearGradient((int) (w / 2 - x), (int) (h / 2 - y), (int) (w / 2 + x), (int) (h / 2 + y), startColor, closeColor, Shader.TileMode.CLAMP));
    }

    private void updateWavePath(int w, int h) {

        mltWave.clear();
        if (getTag() instanceof String) {
            String[] waves = getTag().toString().split("\\s+");
            if ("-1".equals(getTag())) {
                waves = "70,25,1.4,1.4,-26\n100,5,1.4,1.2,15\n420,0,1.15,1,-10\n520,10,1.7,1.5,20\n220,0,1,1,-15".split("\\s+");
            } else if ("-2".equals(getTag())) {
                waves = "0,0,1,0.5,90\n90,0,1,0.5,90".split("\\s+");
            }
            for (String wave : waves) {
                String[] args = wave.split("\\s*,\\s*");
                if (args.length == 5) {
                    mltWave.add(new Wave(/*getContext(),*/dp2px(parseFloat(args[0])), dp2px(parseFloat(args[1])), dp2px(parseFloat(args[4])), parseFloat(args[2]), parseFloat(args[3]), w, h, mWaveHeight / 2));
                }
            }
        } else {
            mltWave.add(new Wave(/*getContext(),*/dp2px(50), dp2px(0), dp2px(5), 1.7f, 2f, w, h, mWaveHeight / 2));
        }

    }

    public void setWaves(String waves) {
        setTag(waves);
        if (mLastTime > 0) {
            View thisView = this;
            updateWavePath(thisView.getWidth(), thisView.getHeight());
        }
    }

    public int getWaveHeight() {
        return mWaveHeight;
    }

    public void setWaveHeight(int waveHeight) {
        this.mWaveHeight = dp2px(waveHeight);
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateWavePath(thisView.getWidth(), thisView.getHeight());
        }
    }

    public float getVelocity() {
        return mVelocity;
    }

    public void setVelocity(float velocity) {
        this.mVelocity = velocity;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        if (mPaint != null) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), thisView.getHeight());
        }
    }

    public int getGradientAngle() {
        return mGradientAngle;
    }

    public void setGradientAngle(int angle) {
        this.mGradientAngle = angle;
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), thisView.getHeight());
        }
    }

    public int getStartColor() {
        return mStartColor;
    }

    public void setStartColor(int color) {
        this.mStartColor = color;
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), thisView.getHeight());
        }
    }

    public void setStartColorId(@ColorRes int colorId) {
        final View thisView = this;
        setStartColor(getColor(thisView.getContext(), colorId));
    }

    public int getCloseColor() {
        return mCloseColor;
    }

    public void setCloseColor(int color) {
        this.mCloseColor = color;
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), thisView.getHeight());
        }
    }

    public void setCloseColorId(@ColorRes int colorId) {
        final View thisView = this;
        setCloseColor(getColor(thisView.getContext(), colorId));
    }

    public float getColorAlpha() {
        return mColorAlpha;
    }

    public void setColorAlpha(float alpha) {
        this.mColorAlpha = alpha;
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), thisView.getHeight());
        }
    }

    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            mLastTime = System.currentTimeMillis();
            invalidate();
        }
    }

    public void stop() {
        mIsRunning = false;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    private static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, Resources.getSystem().getDisplayMetrics());
    }

    @ColorInt
    private static int getColor(@NonNull Context context, @ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(colorId);
        }
        //noinspection deprecation
        return context.getResources().getColor(colorId);
    }
}
