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
import android.widget.LinearLayout;
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

    private TextView mIftRecord;
    private RelativeLayout mRecordLayout;
    private RelativeLayout mScaleBg;
    private CountDownView mCountRecordView;
    private CountDownView mCountPlayView;
    private TextView mTvTime;

    private LinearLayout mLlNewRecord;
    private TextView mIftNewRecord;
    private TextView mTvNewRecordText;
    private LinearLayout mLlAchieve;
    private TextView mIftAchieve;
    private TextView mTvAchieveText;

    private Context mContext;
    private RecordStatus mCurrentStatus = RecordStatus.INITIAL;

    private final int mMinSecond = 5;
    private final int mMaxSecond = 10;
    private int mRecordTime;
    private boolean mIsRecordMax;

    private enum RecordStatus {
        INITIAL, PRE, RECORDING, COMPLETE_PRE, COMPLETE, PLAY
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
        mRecordLayout = inflate.findViewById(R.id.rlRecord);
        mIftRecord = inflate.findViewById(R.id.iftRecord);
        mScaleBg = inflate.findViewById(R.id.rlScaleBg);
        mTvTime = inflate.findViewById(R.id.tvTime);
        mLlNewRecord = inflate.findViewById(R.id.llNewRecord);
        mIftNewRecord = inflate.findViewById(R.id.iftNewRecord);
        mTvNewRecordText = inflate.findViewById(R.id.tvNewRecordText);

        mLlAchieve = inflate.findViewById(R.id.llAchieve);
        mIftAchieve = inflate.findViewById(R.id.iftAchieve);
        mTvAchieveText = inflate.findViewById(R.id.tvAchieveText);

        mCountRecordView = inflate.findViewById(R.id.countDownRecordView);
        mCountPlayView = inflate.findViewById(R.id.countDownPlayView);
        mCountRecordView.setLoadingTime(mMaxSecond);
        mCountPlayView.setLoadingTime(mMaxSecond);
        showInitial();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mIftNewRecord.setOnClickListener(this);
        mIftAchieve.setOnClickListener(this);
        mRecordLayout.setOnClickListener(this);
        mRecordLayout.setOnTouchListener(new OnTouchListener() {
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

        mCountRecordView.setOnLoadingFinishListener(new CountDownView.OnRecordFinishListener() {
            @Override
            public void finishTime(int time) {//正常时结束录音时间
                mRecordTime = time;
                mTvTime.setText(time + "S");
            }

            @Override
            public void finishMax() {//到达最大录音时间结束
                mIsRecordMax = true;
                showRecordComplete(true);
            }
        });

        mCountPlayView.setPlayListener(new CountDownView.OnPlayListener() {
            @Override
            public void startPlayVoice() {//开始录音

            }

            @Override
            public void playing(int countTime) {//播放录音中
                mTvTime.setText(countTime + "S");
            }

            @Override
            public void playVoiceFinish() {//播放录音结束
                showPlayResume();
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
        mRecordLayout.setVisibility(VISIBLE);
        mCountRecordView.setVisibility(VISIBLE);
        mCountPlayView.setVisibility(GONE);
        mScaleBg.setVisibility(GONE);
        playVoiceButtonShow();
        mRecordTime = 0;
        mIsRecordMax = false;
        mIftRecord.setText("录音");
    }

    //原始页面过渡到录音页面
    @Override
    public synchronized void showPre() {
        if (mCurrentStatus == RecordStatus.INITIAL) {
            mCurrentStatus = RecordStatus.PRE;
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
            mScaleBg.setVisibility(VISIBLE);
            mCountRecordView.start();
            mIftRecord.setText("录ing");
            if (mOnRecordListener != null) {
                mOnRecordListener.onStartRecord();
            }
        }
    }

    //从正在录音页面后恢复到原始页面或录音成功页面
    @Override
    public synchronized void showResume() {
        //触摸按钮但还没进入录音状态
        if (mCurrentStatus == RecordStatus.PRE) {
            mIftRecord.setText("录音");
            mCurrentStatus = RecordStatus.INITIAL;
            playRecordAnimator(false);
            return;
        }

        if (mCurrentStatus == RecordStatus.RECORDING) {
            if (mCountRecordView.getTime() < mMinSecond) {
                showRecordCancel();
            } else {
                showRecordComplete(false);
            }
        }
    }

    //从正在录音页面后恢复到原始页面
    @Override
    public synchronized void showRecordCancel() {
        if (mCurrentStatus == RecordStatus.RECORDING) {
            mCurrentStatus = RecordStatus.INITIAL;
            mCountRecordView.closeAnimator();
            playRecordAnimator(false);
            mIftRecord.setText("录音");
            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordingCancel();
            }
        }
    }

    //从正在录音页面回到录音成功页面
    @Override
    public synchronized void showRecordComplete(boolean mIsMaxSec) {
        if (mCurrentStatus == RecordStatus.RECORDING) {
            mCurrentStatus = RecordStatus.COMPLETE_PRE;
            playRecordAnimator(false);
            mCountRecordView.closeAnimator();
            mIftRecord.setText("完成");
            if (!mIsMaxSec) {
                if (mOnRecordListener != null) {
                    mOnRecordListener.onFinishRecord();
                }
            } else {
                if (mOnRecordListener != null) {
                    mOnRecordListener.onFinishMaxRecord();
                }
            }
        }
    }

    //从录音成功页面回到原始成功页面
    @Override
    public synchronized void showRecordCompleteToInitial() {
        if (mCurrentStatus == RecordStatus.COMPLETE) {
            mCurrentStatus = RecordStatus.INITIAL;
            showInitial();
        }
    }

    //从录音成功到播放页面
    @Override
    public synchronized void showPlay() {
        if (mCurrentStatus == RecordStatus.COMPLETE) {
            mCurrentStatus = RecordStatus.PLAY;
            playVoiceButtonShow();
            mIftRecord.setText("播放中");
            mCountPlayView.startPlayRecord(mRecordTime);
        }
    }

    //从播放页面回到录音成功
    @Override
    public synchronized void showPlayResume() {
        if (mCurrentStatus == RecordStatus.PLAY) {
            mCurrentStatus = RecordStatus.COMPLETE;
            playVoiceButtonShow();
            mIftRecord.setText("完成");
            mCountPlayView.closeAnimator();
            mTvTime.setText(mRecordTime + "S");
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
                animatorX = ObjectAnimator.ofFloat(mRecordLayout, "scaleX", value, 1f);
                animatorY = ObjectAnimator.ofFloat(mRecordLayout, "scaleY", value, 1f);

                animatorBgX = ObjectAnimator.ofFloat(mScaleBg, "scaleX", value * 2, 1f);
                animatorBgY = ObjectAnimator.ofFloat(mScaleBg, "scaleY", value * 2, 1f);
                mIsEnlargeSuccess = false;
                mAnimSetEnlarge.end();
            } else {
                animatorX = ObjectAnimator.ofFloat(mRecordLayout, "scaleX", 1.5f, 1f);
                animatorY = ObjectAnimator.ofFloat(mRecordLayout, "scaleY", 1.5f, 1f);
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
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(mRecordLayout, "scaleX", 1f, 1.5f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(mRecordLayout, "scaleY", 1f, 1.5f);

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
                    mScaleBg.setVisibility(GONE);
                    if (mCurrentStatus == RecordStatus.COMPLETE_PRE) {
                        mCountRecordView.setVisibility(GONE);
                        mCountPlayView.setVisibility(VISIBLE);
                        mCurrentStatus = RecordStatus.COMPLETE;
                        playVoiceButtonShow();
                    } else {
                        mCountRecordView.setVisibility(VISIBLE);
                        mCountPlayView.setVisibility(GONE);
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
    }

    private void playVoiceButtonShow() {
        mLlNewRecord.setVisibility(GONE);
        mLlAchieve.setVisibility(GONE);
        switch (mCurrentStatus) {
            case COMPLETE:
                mLlNewRecord.setVisibility(VISIBLE);
                mLlAchieve.setVisibility(VISIBLE);
                mIftNewRecord.setEnabled(true);
                mTvNewRecordText.setEnabled(true);
                mIftAchieve.setEnabled(true);
                mTvAchieveText.setEnabled(true);
                break;
            case PLAY:
                mLlNewRecord.setVisibility(VISIBLE);
                mLlAchieve.setVisibility(VISIBLE);
                mIftNewRecord.setEnabled(false);
                mTvNewRecordText.setEnabled(false);
                mIftAchieve.setEnabled(false);
                mTvAchieveText.setEnabled(false);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlRecord) {
            if (mIsRecordMax) {//录到最大值完成后，防止手指收起出发播放
                mIsRecordMax = false;
                return;
            }
            if (mCurrentStatus == RecordStatus.COMPLETE) {
                showPlay();
                return;
            }
            if (mCurrentStatus == RecordStatus.PLAY) {
                showPlayResume();
                return;
            }
        }
        if (id == R.id.iftNewRecord) {
            showRecordCompleteToInitial();
        }
        if (id == R.id.iftAchieve) {
            Toast.makeText(mContext, "完成", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVoiceAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mRecordLayout, "scaleX", 1f, 1.5f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mRecordLayout, "scaleY", 1f, 1.5f);
        animatorSet.playTogether(animatorX, animatorY);
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