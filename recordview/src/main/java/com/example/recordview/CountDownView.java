package com.example.recordview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class CountDownView extends View {
    private Context mContext;//上下文
    private Paint mPaintBackGround;//背景画笔
    private Paint mPaintArc;//圆弧画笔
    private Paint mPaintText;//文字画笔
    private int mRetreatType;//圆弧绘制方式（增加和减少）
    private int mTmeModel;//分秒、秒
    private float mPaintArcWidth;//最外层圆弧的宽度
    private int mCircleRadius;//圆圈的半径
    private int mPaintArcColor = Color.parseColor("#3C3F41");//初始值
    private int mPaintBackGroundColor = Color.parseColor("#55B2E5");//初始值
    private int mLoadingTime;//时间，单位秒
    private String mLoadingTimeUnit = "";//时间单位
    private int mTextColor = Color.BLACK;//字体颜色
    private int mTextSize;//字体大小
    private int location;//从哪个位置开始
    private float startAngle;//开始角度
    private float mmSweepAngleStart;//起点
    private float mmSweepAngleEnd;//终点
    private float mSweepAngle;//扫过的角度
    private int mTime = 0;//时间
    private int mWidth;
    private int mHeight;
    private AnimatorSet mAnimatorSet;


    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        mRetreatType = array.getInt(R.styleable.CountDownView_cd_retreat_type, 1);
        location = array.getInt(R.styleable.CountDownView_cd_location, 1);
        mTmeModel = array.getInt(R.styleable.CountDownView_cd_time_mode, 1);
        mCircleRadius = (int) array.getDimension(R.styleable.CountDownView_cd_circle_radius, dip2px(context, 25));//默认25dp
        mPaintArcWidth = array.getDimension(R.styleable.CountDownView_cd_arc_width, dip2px(context, 3));//默认3dp
        mPaintArcColor = array.getColor(R.styleable.CountDownView_cd_arc_color, mPaintArcColor);
        mTextSize = (int) array.getDimension(R.styleable.CountDownView_cd_text_size, dip2px(context, 14));//默认14sp
        mTextColor = array.getColor(R.styleable.CountDownView_cd_text_color, mTextColor);
        mPaintBackGroundColor = array.getColor(R.styleable.CountDownView_cd_bg_color, mPaintBackGroundColor);
        mLoadingTime = array.getInteger(R.styleable.CountDownView_cd_animator_time, 3);//默认3秒
        mLoadingTimeUnit = array.getString(R.styleable.CountDownView_cd_animator_time_unit);//时间单位
        if (TextUtils.isEmpty(mLoadingTimeUnit)) {
            mLoadingTimeUnit = "";
        }
        array.recycle();
        init();
    }

    private void init() {
        //背景设为透明，然后造成圆形View的视觉错觉
        this.setBackground(ContextCompat.getDrawable(mContext, android.R.color.transparent));
        mPaintBackGround = new Paint();
        mPaintBackGround.setStyle(Paint.Style.FILL);
        mPaintBackGround.setAntiAlias(true);
        mPaintBackGround.setColor(mPaintBackGroundColor);

        mPaintArc = new Paint();
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setAntiAlias(true);
        mPaintArc.setColor(mPaintArcColor);
        mPaintArc.setStrokeWidth(mPaintArcWidth);

        mPaintText = new Paint();
        mPaintText.setStyle(Paint.Style.STROKE);
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(mTextColor);
        mPaintText.setTextSize(mTextSize);
        if (mLoadingTime < 0) {
            mLoadingTime = 3;
        }
        if (location == 1) {//默认从左侧开始
            startAngle = -180;
        } else if (location == 2) {
            startAngle = -90;
        } else if (location == 3) {
            startAngle = 0;
        } else if (location == 4) {
            startAngle = 90;
        }

        if (mRetreatType == 1) {
            mmSweepAngleStart = 360f;
            mmSweepAngleEnd = 0f;
        } else {
            mmSweepAngleStart = 0f;
            mmSweepAngleEnd = 360f;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取view宽高
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //因为必须是圆形的view，所以在这里重新赋值
        setMeasuredDimension(mCircleRadius * 2, mCircleRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画背景园
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - mPaintArcWidth, mPaintBackGround);
        mPaintArc.setColor(mPaintArcColor);
        mPaintText.setColor(mTextColor);
        //画圆弧
        RectF rectF = new RectF(0 + mPaintArcWidth / 2, 0 + mPaintArcWidth / 2
                , mWidth - mPaintArcWidth / 2, mHeight - mPaintArcWidth / 2);
        canvas.drawArc(rectF, startAngle, mSweepAngle, false, mPaintArc);
//        drawTimeText(canvas);
    }

    public void startPlayRecord(int recordTime) {
        float sweepAngleStart = (float) recordTime / (float) mLoadingTime * mmSweepAngleStart;
        ValueAnimator animator = ValueAnimator.ofFloat(sweepAngleStart, mmSweepAngleEnd);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mSweepAngle = (float) valueAnimator.getAnimatedValue();
                //获取到需要绘制的角度，重新绘制
                invalidate();
            }
        });
        //这里是时间获取和赋值
        mTime = 0;
        ValueAnimator animator1 = ValueAnimator.ofInt(recordTime, 0);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTime = (int) valueAnimator.getAnimatedValue();
                if (mOnPlayListener !=null){
                    mOnPlayListener.playing(mTime);
                }
            }
        });
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animator, animator1);
        mAnimatorSet.setDuration(recordTime * 1000);
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.start();
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(mOnPlayListener != null){
                    mOnPlayListener.playVoiceFinish();
                }
                clearAnimation();

            }
        });
        if(mOnPlayListener != null){
            mOnPlayListener.startPlayVoice();
        }
    }

    public void start() {
        ValueAnimator animator = ValueAnimator.ofFloat(mmSweepAngleStart, mmSweepAngleEnd);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mSweepAngle = (float) valueAnimator.getAnimatedValue();
                //获取到需要绘制的角度，重新绘制
                invalidate();
            }
        });
        //这里是时间获取和赋值
        mTime = 0;
        ValueAnimator animator1 = ValueAnimator.ofInt(0, mLoadingTime);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTime = (int) valueAnimator.getAnimatedValue();
                if (mRecordFinishListener != null && mTime == mLoadingTime) {
                    mRecordFinishListener.finishMax();
                }
            }
        });
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animator, animator1);
        mAnimatorSet.setDuration(mLoadingTime * 1000);
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.start();
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mRecordFinishListener != null) {
                    mRecordFinishListener.finishTime(mTime);
                }
                mTime = 0;
                clearAnimation();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pauseAnimator() {
        if (mAnimatorSet != null && mAnimatorSet.isStarted() && mAnimatorSet.isRunning()) {
            mAnimatorSet.pause();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void resumeAnimator() {
        if (mAnimatorSet != null && mAnimatorSet.isPaused()) {
            mAnimatorSet.resume();
        }
    }


    public void closeAnimator() {
        if (mAnimatorSet != null) {
            mSweepAngle = 0;
            invalidate();
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    public int getTime() {
        return mTime;
    }

    public boolean isPlay(){
        boolean isPlay = false;
        if (mAnimatorSet!=null && mAnimatorSet.isRunning()){
            isPlay = true;
        }
        return isPlay;
    }

    private OnRecordFinishListener mRecordFinishListener;

    public void setOnLoadingFinishListener(OnRecordFinishListener listener) {
        this.mRecordFinishListener = listener;
    }

    public interface OnRecordFinishListener {
        //正常时结束录音时间
        void finishTime(int time);

        //到达最大录音时间结束
        void finishMax();
    }

    private OnPlayListener mOnPlayListener;

    public void setPlayListener(OnPlayListener listener) {
        this.mOnPlayListener = listener;
    }

    public interface OnPlayListener {
        void startPlayVoice();

        void playing(int countTime);

        void playVoiceFinish();
    }


    private void drawTimeText(Canvas canvas) {
        //画文字
        String text;
        if (mTmeModel == 1) {
            text = mTime + mLoadingTimeUnit;
        } else {
            int hour = (int) ((long) mTime / (60 * 60));
            int min = (int) (((long) mTime / (60)) - hour * 60);
            int sec = (int) ((long) mTime - hour * 60 * 60 - min * 60);
            text = String.format("%02d", hour * 60 + min) + ":" + String.format("%02d", sec);
        }
        float mTextWidth = mPaintText.measureText(text, 0, text.length());
        float dx = mWidth / 2 - mTextWidth / 2;
        Paint.FontMetricsInt fontMetricsInt = mPaintText.getFontMetricsInt();
        float dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        float baseLine = mHeight / 2 + dy;
        canvas.drawText(text, dx, baseLine, mPaintText);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setLoadingTime(int loadingTime) {
        mLoadingTime = loadingTime;
        invalidate();
    }
}
