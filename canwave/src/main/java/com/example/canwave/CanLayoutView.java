package com.example.canwave;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author 李栋杰
 * @time 2018/10/26  15:04
 * @desc ${TODD}
 */
public class CanLayoutView extends RelativeLayout {

    private Context mContext;
    private CanWaveView mCanView;
    private ImageView mIvCap;
    private MusicNoteView mMusicNoteView;

    public CanLayoutView(Context context) {
        this(context,null);

    }

    public CanLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CanLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(context);
    }


    private void initView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_can_layout, this);
        mCanView = inflate.findViewById(R.id.canView);
        mIvCap = inflate.findViewById(R.id.capView);
        mMusicNoteView = inflate.findViewById(R.id.musicNoteView);
        mIvCap.setAlpha(0f);
        mCanView.startAnim();
    }

    public void reset(){
        mIvCap.setAlpha(0f);
    }

    public void startAnim(){
        startAnim(0);
    }

    public void startAnim(long musicNoteDelay){
        mMusicNoteView.startAnimation(musicNoteDelay);
        mCanView.resume();
    }

    public void startWaveAnim(){
        mCanView.resume();
    }

    public void stopAnim(){
        stopAnim(false);
    }

    public void stopAnim(boolean isCleanMusicNote){
        mMusicNoteView.stop(isCleanMusicNote);
        mCanView.pauseAnim();
    }

    public void stopWaveAnim(){
        mCanView.pauseAnim();
    }

    public void dropCap(){
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mIvCap, "alpha", 0f, 1f).setDuration(200);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mIvCap, "translationY", -mContext.getResources().getDimension(R.dimen.pair_cap_margin_top), 0.f).setDuration(300);
        translationY.setInterpolator(new OvershootInterpolator());
        set.play(alpha).with(translationY);
        set.start();
    }

    public void riseCap(){

        float target = -mContext.getResources().getDimension(R.dimen.pair_cap_margin_top);
        if ( mIvCap.getTranslationY() != target ) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator translationY = ObjectAnimator.ofFloat(mIvCap, "translationY", 0.f,target).setDuration(300);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mIvCap, "alpha", 1f, 0f).setDuration(200);
            alpha.setStartDelay(100);
            translationY.setInterpolator(new AccelerateDecelerateInterpolator());
            set.play(translationY).with(alpha);
            set.start();
        }
    }
}
