package com.example.administrator.testView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myview.R;

/**
 * @author 李栋杰
 * @time 2018/11/2  17:07
 * @desc ${TODD}
 */
public class RecordLayout extends RelativeLayout implements onShowRecordStatus, View.OnClickListener {

    private TextView mIftRecordPre;
    private RelativeLayout mPreLayout;
    private RelativeLayout mScaleLayout;
    private RelativeLayout mScaleBg;
    private CountDownView mCountRecordView;
    private RelativeLayout mRlPlay;
    private CountDownView mCountPlayView;
    private Context mContext;
    private RecordStatus mCurrentStatus = RecordStatus.INITIAL;

    private final int minSec = 5;
    private final int maxSec = 60;

    private enum RecordStatus {
        INITIAL, PRE, RECORDING, COMPLETE, PLAY
    }

    public RecordStatus getRecordStatus() {
        return mCurrentStatus;
    }

    private OnRecordListener mOnRecordListener;

    public interface OnRecordListener {

        void onStartRecord();

        void onRecordingCancel();

        void onFinishRecord();

        void onFinishMaxRecord();
    }

    public void setOnRecordListener(OnRecordListener listener) {
        this.mOnRecordListener = listener;
    }

    public RecordLayout(Context context) {
        this(context, null);
    }

    public RecordLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initListener();
    }

    private void initView() {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.record_layout, this);
        mPreLayout = inflate.findViewById(R.id.rlRecordPre);
        mIftRecordPre = inflate.findViewById(R.id.iftRecordPre);
        mScaleLayout = inflate.findViewById(R.id.rlScaleLayout);
        mScaleBg = inflate.findViewById(R.id.rlScaleBg);
        mCountRecordView = inflate.findViewById(R.id.countDownRecordView);
        mRlPlay = inflate.findViewById(R.id.rlPlay);
        mCountPlayView = inflate.findViewById(R.id.countDownPlayView);
        showInitial();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mRlPlay.setOnClickListener(this);
        mPreLayout.setOnClickListener(this);
        mPreLayout.setOnTouchListener(new OnTouchListener() {
            float startX = 0;
            float startY = 0;
            float endX = 0;
            float endY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        showPre();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getX();
                        float moveY = event.getY();
                        int instance = (int) getDistance(startX, moveX, startY, moveY);
                        break;

                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();
                        showResume();
                        break;
                    case MotionEvent.ACTION_CANCEL:

                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mCountRecordView.setOnLoadingFinishListener(new CountDownView.OnLoadingFinishListener() {
            @Override
            public void finish() {
                //超过时
                showRecordComplete(true);
            }
        });
    }


    private double getDistance(float startX, float endX, float startY, float endY) {
        double _x = Math.abs(startX - endX);
        double _y = Math.abs(startY - endY);
        return Math.sqrt(_x * _x + _y * _y);
    }

    //原始页面
    @Override
    public void showInitial() {
        mCurrentStatus = RecordStatus.INITIAL;
        mPreLayout.setVisibility(VISIBLE);
        mScaleLayout.setVisibility(GONE);
        mCountRecordView.setVisibility(GONE);
        mScaleBg.setVisibility(GONE);
    }

    //原始页面过渡到录音页面
    @Override
    public synchronized void showPre() {
        if (mCurrentStatus == RecordStatus.INITIAL) {
            mCurrentStatus = RecordStatus.PRE;
            mPreLayout.setVisibility(INVISIBLE);
            mScaleLayout.setVisibility(VISIBLE);
            mScaleBg.setVisibility(VISIBLE);
            playRecordAnimator(true);
        }
    }

    //正在录音页面
    @Override
    public synchronized void showRecording() {
        if (mCurrentStatus == RecordStatus.PRE) {
            mCurrentStatus = RecordStatus.RECORDING;
            mCountRecordView.setVisibility(VISIBLE);
            mCountRecordView.start();
            Toast.makeText(mContext, "开始录音", Toast.LENGTH_SHORT).show();
            if (mOnRecordListener != null) {
                mOnRecordListener.onStartRecord();
            }
        }
    }

    //从正在录音页面后恢复到原始页面或录音成功页面
    @Override
    public synchronized void showResume() {
        if (mCurrentStatus == RecordStatus.PRE){
            mCurrentStatus = RecordStatus.INITIAL;
            playRecordAnimator(false);
            return;
        }

        if (mCurrentStatus == RecordStatus.RECORDING) {
            if (mCountRecordView.getTime() < minSec) {
                showRecordCancel();
            } else {
                showRecordComplete(false);
            }
        }
    }

    //从正在录音页面后恢复到原始页面
    @Override
    public synchronized void showRecordCancel() {
        if (mCurrentStatus == RecordStatus.RECORDING){
            mCurrentStatus = RecordStatus.INITIAL;
            mCountRecordView.setVisibility(GONE);
            mCountRecordView.closeAnimator();
            playRecordAnimator(false);
            Toast.makeText(mContext, "取消录音", Toast.LENGTH_SHORT).show();
            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordingCancel();
            }
        }
    }

    //从正在录音页面回到录音成功页面
    @Override
    public synchronized void showRecordComplete(boolean mIsMaxSec) {
        if (mCurrentStatus == RecordStatus.RECORDING) {
            mCurrentStatus = RecordStatus.COMPLETE;
            playRecordAnimator(false);
            mCountRecordView.setVisibility(GONE);
            mCountRecordView.closeAnimator();
            mIftRecordPre.setText("成");
            if (!mIsMaxSec){
                Toast.makeText(mContext, "成功", Toast.LENGTH_SHORT).show();
                if (mOnRecordListener != null) {
                    mOnRecordListener.onFinishRecord();
                }
            }else {
                Toast.makeText(mContext, "最大", Toast.LENGTH_SHORT).show();
                if (mOnRecordListener != null) {
                    mOnRecordListener.onFinishMaxRecord();
                }
            }
        }
    }

    //从录音成功页面回到原始成功页面
    @Override
    public synchronized void showRecordCompleteToInitial() {
        if (mCurrentStatus == RecordStatus.COMPLETE){
            mCurrentStatus = RecordStatus.INITIAL;
        }
    }

    //从录音成功到播放页面
    @Override
    public synchronized void showPlay() {
        if (mCurrentStatus == RecordStatus.COMPLETE){
            mCurrentStatus = RecordStatus.PLAY;
        }
    }

    //从播放页面回到录音成功
    @Override
    public synchronized void showPlayResume() {
        if (mCurrentStatus == RecordStatus.PLAY){
            mCurrentStatus = RecordStatus.COMPLETE;
        }
    }


    private AnimatorSet mAnimSetEnlarge;
    private AnimatorSet mAnimSetLessen;

    private void playRecordAnimator(boolean isPlay) {
        initRecordAnimator();
        if (isPlay) {
            if (mAnimSetEnlarge.isRunning()) {
                return;
            }
            mIsEnlargeSuccess = true;
            mAnimSetEnlarge.start();
        } else {
            if (mAnimSetLessen.isRunning()) {
                return;
            }
            mAnimSetLessen.getChildAnimations().clear();
            ObjectAnimator animatorX;
            ObjectAnimator animatorY;
            ObjectAnimator animatorBgX;
            ObjectAnimator animatorBgY;
            if (mAnimSetEnlarge.isRunning()) {
                ObjectAnimator animator = (ObjectAnimator) mAnimSetEnlarge.getChildAnimations().get(0);
                float value = (float) animator.getAnimatedValue();
                animatorX = ObjectAnimator.ofFloat(mScaleLayout, "scaleX", value, 1f);
                animatorY = ObjectAnimator.ofFloat(mScaleLayout, "scaleY", value, 1f);

                animatorBgX = ObjectAnimator.ofFloat(mScaleBg, "scaleX", value * 2, 1f);
                animatorBgY = ObjectAnimator.ofFloat(mScaleBg, "scaleY", value * 2, 1f);
                mIsEnlargeSuccess = false;
                mAnimSetEnlarge.end();
            } else {
                animatorX = ObjectAnimator.ofFloat(mScaleLayout, "scaleX", 1.5f, 1f);
                animatorY = ObjectAnimator.ofFloat(mScaleLayout, "scaleY", 1.5f, 1f);
                animatorBgX = ObjectAnimator.ofFloat(mScaleBg, "scaleX", 3f, 1f);
                animatorBgY = ObjectAnimator.ofFloat(mScaleBg, "scaleY", 3f, 1f);
            }

            mAnimSetLessen.playTogether(animatorX, animatorY, animatorBgX, animatorBgY);
            mAnimSetLessen.start();
        }
    }

    private boolean mIsEnlargeSuccess;

    private void initRecordAnimator() {
        if (mAnimSetEnlarge == null) {
            mAnimSetEnlarge = new AnimatorSet();
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(mScaleLayout, "scaleX", 1f, 1.5f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(mScaleLayout, "scaleY", 1f, 1.5f);

            ObjectAnimator animatorBgX = ObjectAnimator.ofFloat(mScaleBg, "scaleX", 1f, 3f);
            ObjectAnimator animatorBgY = ObjectAnimator.ofFloat(mScaleBg, "scaleY", 1f, 3f);

            mAnimSetEnlarge.playTogether(animatorX, animatorY, animatorBgX, animatorBgY);
            mAnimSetEnlarge.setDuration(300);
            mAnimSetEnlarge.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (mIsEnlargeSuccess) {
                        showRecording();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        if (mAnimSetLessen == null) {
            mAnimSetLessen = new AnimatorSet();
            mAnimSetLessen.setDuration(300);
            mAnimSetLessen.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mCountRecordView.closeAnimator();
                    mScaleLayout.setVisibility(GONE);
                    mScaleBg.setVisibility(GONE);
                    mPreLayout.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlRecordPre){
            if (mCurrentStatus == RecordStatus.COMPLETE){
                Toast.makeText(mContext, "播放", Toast.LENGTH_SHORT).show();
                showPlay();
            }
        }
        if (id == R.id.rlPlay){
            playVoiceAnim();
        }
    }

    private void playVoiceAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mRlPlay, "scaleX", 1f, 1.5f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mRlPlay, "scaleY", 1f, 1.5f);
        animatorSet.playTogether(animatorX,animatorY);
        animatorSet.setDuration(200);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCountPlayView.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}