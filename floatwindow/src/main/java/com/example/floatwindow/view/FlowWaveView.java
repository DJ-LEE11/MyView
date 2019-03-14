package com.example.floatwindow.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.floatwindow.R;
import com.example.floatwindow.util.DisplayUtil;


/**
 * Created by allen on 2017/6/20.
 * /**
 * y=A*sin(ωx+φ)+k
 * <p>
 * A—振幅越大，波形在y轴上最大与最小值的差值越大
 * ω—角速度， 控制正弦周期(单位角度内震动的次数)
 * φ—初相，反映在坐标系上则为图像的左右移动。这里通过不断改变φ,达到波浪移动效果
 * k—偏距，反映在坐标系上则为图像的上移或下移。
 */

public class FlowWaveView extends View {

    private Context mContext;
    /**
     * 振幅
     */
    private int A;
    /**
     * 偏距
     */
    private int K;

    /**
     * 波形的颜色
     */

    private int startColor;
    private int endColor;

    /**
     * 初相
     */
    private float φ;

    /**
     * 波形移动的速度
     */
    private float waveSpeed = 3f;

    /**
     * 角速度
     */
    private double ω;

    /**
     * 开始位置相差多少个周期
     */
    private double startPeriod;

    /**
     * 是否直接开启波形
     */
    private boolean waveStart;

    private Path path;
    private Paint paint;

    private ValueAnimator valueAnimator;

    public FlowWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getAttr(attrs);
        K = A;
        initPaint();

        initAnimation();
    }

    private void getAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.FloatWaveView);
        A = typedArray.getDimensionPixelOffset(R.styleable.FloatWaveView_waveAmplitude, dp2px(10));
        startColor = typedArray.getColor(R.styleable.FloatWaveView_startColor, 0xFF056CD0);
        endColor = typedArray.getColor(R.styleable.FloatWaveView_endColor, 0xFF31AFFE);
        waveSpeed = typedArray.getFloat(R.styleable.FloatWaveView_waveSpeed, waveSpeed);
        startPeriod = typedArray.getFloat(R.styleable.FloatWaveView_waveStartPeriod, 0);
        waveStart = typedArray.getBoolean(R.styleable.FloatWaveView_waveStart, false);

        typedArray.recycle();
    }

    private void initPaint() {
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ω = 2 * Math.PI / getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        φ -= waveSpeed / 100;
        float y;

        path.reset();
        path.moveTo(0, 0);

        for (float x = 0; x <= getWidth(); x += 10) {
            y = (float) (A * Math.sin(ω * x + φ + Math.PI * startPeriod) + K);
            path.lineTo(x, y);
        }

        //填充半圆
        path.lineTo(getWidth(), getHeight()- DisplayUtil.dp2px(mContext,60));
        RectF rectF = new RectF(0,getHeight()- DisplayUtil.dp2px(mContext,60),getWidth(),getHeight());
        path.arcTo(rectF,0,180);
        path.close();
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        canvas.drawPath(path, paint);
    }

    private void initAnimation() {
        valueAnimator = ValueAnimator.ofInt(0, getWidth());
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /**
                 * 刷新页面调取onDraw方法，通过变更φ 达到移动效果
                 */
                invalidate();
            }
        });
        if (waveStart) {
            valueAnimator.start();
        }
    }

    public void startAnimation() {
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    public void stopAnimation() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pauseAnimation() {
        if (valueAnimator != null) {
            valueAnimator.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resumeAnimation() {
        if (valueAnimator != null) {
            valueAnimator.resume();
        }
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}
