package com.example.floatwindow.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.example.floatwindow.base.FloatView;
import com.example.floatwindow.base.IFloatWindow;
import com.example.floatwindow.listener.FloatLifecycle;
import com.example.floatwindow.listener.LifecycleListener;
import com.example.floatwindow.type.MoveType;
import com.example.floatwindow.type.Screen;
import com.example.floatwindow.util.Util;


/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

public class IFloatWindowImpl extends IFloatWindow {


    private FloatWindow.B mB;
    private FloatView mFloatView;
    private FloatLifecycle mFloatLifecycle;
    private boolean isInit;
    private boolean isShow;
    private ValueAnimator mAnimator;
    private TimeInterpolator mDecelerateInterpolator;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private boolean mClick = false;
    private int mSlop;
    private boolean mIsLocationLeft;

    private IFloatWindowImpl() {

    }

    IFloatWindowImpl(FloatWindow.B b) {
        mB = b;
        mFloatView = new FloatPhone(b.mApplicationContext);
        initTouchEvent();
        mFloatView.setSize(mB.mWidth, mB.mHeight);
        mFloatView.setGravity(mB.gravity, Util.getScreenWidth(mB.mApplicationContext) - mB.mWidth - mB.mSlideRightMargin, mB.yOffset);
        mFloatView.setView(mB.mView);
        mFloatLifecycle = new FloatLifecycle(mB.mApplicationContext, mB.mShow, mB.mActivities, new LifecycleListener() {
            @Override
            public void onShow() {
                show();
            }

            @Override
            public void onHide() {
                hide();
            }

            @Override
            public void onBackToDesktop() {
                if (!mB.mDesktopShow) {
                    hide();
                }
                if (mB.mViewStateListener != null) {
                    mB.mViewStateListener.onBackToDesktop();
                }
            }
        });
    }

    @Override
    public void show() {
        if (!isShow) {
            if (!isInit) {
                mFloatView.addView();
                isInit = true;
            }
            getView().setVisibility(View.VISIBLE);
            isShow = true;
            if (mB.mViewStateListener != null) {
                mB.mViewStateListener.onShow();
            }
        }
    }


    @Override
    public void hide() {
        if (!isShow) {
            return;
        }
        getView().setVisibility(View.INVISIBLE);
        isShow = false;
        if (mB.mViewStateListener != null) {
            mB.mViewStateListener.onHide();
        }
    }

    @Override
    public void moveHide() {
        if (!isShow) {
            return;
        }
        if (mIsLocationLeft) {
            mAnimator = ObjectAnimator.ofInt(0, -mB.mWidth);
        } else {
            mAnimator = ObjectAnimator.ofInt(Util.getScreenWidth(mB.mApplicationContext) - mB.mWidth, Util.getScreenWidth(mB.mApplicationContext));
        }
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                mFloatView.updateX(x);
                if (mB.mViewStateListener != null) {
                    mB.mViewStateListener.onPositionUpdate(x, (int) upY);
                }
            }
        });
        startAnimator();
    }

    @Override
    public void resumeHide() {
        if (!isShow) {
            return;
        }
        if (mIsLocationLeft){
            mAnimator = ObjectAnimator.ofInt(-mB.mWidth,0);
        }else {
            mAnimator = ObjectAnimator.ofInt(Util.getScreenWidth(mB.mApplicationContext),Util.getScreenWidth(mB.mApplicationContext) - mB.mWidth);
        }
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                mFloatView.updateX(x);
                if (mB.mViewStateListener != null) {
                    mB.mViewStateListener.onPositionUpdate(x, (int) upY);
                }
            }
        });
        startAnimator();
    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void dismiss() {
        mFloatView.dismiss();
        isShow = false;
        isInit = false;
        if (mFloatLifecycle != null) {
            mFloatLifecycle.destroy();
        }
        if (mB.mViewStateListener != null) {
            mB.mViewStateListener.onDismiss();
        }
    }

    @Override
    public void updateX(int x) {
        checkMoveType();
        mB.xOffset = x;
        mFloatView.updateX(x);
    }

    @Override
    public void updateY(int y) {
        checkMoveType();
        mB.yOffset = y;
        mFloatView.updateY(y);
    }

    @Override
    public void updateX(int screenType, float ratio) {
        checkMoveType();
        mB.xOffset = (int) ((screenType == Screen.width ?
                Util.getScreenWidth(mB.mApplicationContext) :
                Util.getScreenHeight(mB.mApplicationContext)) * ratio);
        mFloatView.updateX(mB.xOffset);

    }

    @Override
    public void updateY(int screenType, float ratio) {
        checkMoveType();
        mB.yOffset = (int) ((screenType == Screen.width ?
                Util.getScreenWidth(mB.mApplicationContext) :
                Util.getScreenHeight(mB.mApplicationContext)) * ratio);
        mFloatView.updateY(mB.yOffset);

    }

    @Override
    public int getX() {
        return mFloatView.getX();
    }

    @Override
    public int getY() {
        return mFloatView.getY();
    }


    @Override
    public View getView() {
        mSlop = ViewConfiguration.get(mB.mApplicationContext).getScaledTouchSlop();
        return mB.mView;
    }


    private void checkMoveType() {
        if (mB.mMoveType == MoveType.fixed) {
            throw new IllegalArgumentException("FloatWindow of this tag is not allowed to move!");
        }
    }


    private void initTouchEvent() {
        switch (mB.mMoveType) {
            case MoveType.inactive:
                break;
            default:
                getView().setOnTouchListener(new View.OnTouchListener() {
                    float lastX, lastY, changeX, changeY;
                    int newX, newY;

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getRawX();
                                downY = event.getRawY();
                                lastX = event.getRawX();
                                lastY = event.getRawY();
                                cancelAnimator();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                changeX = event.getRawX() - lastX;
                                changeY = event.getRawY() - lastY;
                                newX = (int) (mFloatView.getX() + changeX);
                                newY = (int) (mFloatView.getY() + changeY);
                                mFloatView.updateXY(newX, newY);
                                if (mB.mViewStateListener != null) {
                                    mB.mViewStateListener.onPositionUpdate(newX, newY);
                                }
                                lastX = event.getRawX();
                                lastY = event.getRawY();
                                break;
                            case MotionEvent.ACTION_UP:
                                upX = event.getRawX();
                                upY = event.getRawY();
                                mClick = (Math.abs(upX - downX) > mSlop) || (Math.abs(upY - downY) > mSlop);
                                switch (mB.mMoveType) {
                                    case MoveType.slide:
                                        int startX = mFloatView.getX();
                                        int endX = (startX * 2 + v.getWidth() > Util.getScreenWidth(mB.mApplicationContext)) ?
                                                Util.getScreenWidth(mB.mApplicationContext) - v.getWidth() - mB.mSlideRightMargin :
                                                mB.mSlideLeftMargin;
                                        mIsLocationLeft = endX == mB.mSlideLeftMargin;
                                        if (startX == endX) {
                                            break;
                                        }
                                        mAnimator = ObjectAnimator.ofInt(startX, endX);
                                        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                int x = (int) animation.getAnimatedValue();
                                                mFloatView.updateX(x);
                                                if (mB.mViewStateListener != null) {
                                                    mB.mViewStateListener.onPositionUpdate(x, (int) upY);
                                                }
                                            }
                                        });
                                        startAnimator();
                                        break;
                                    case MoveType.back:
                                        PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", mFloatView.getX(), mB.xOffset);
                                        PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", mFloatView.getY(), mB.yOffset);
                                        mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                                        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                int x = (int) animation.getAnimatedValue("x");
                                                int y = (int) animation.getAnimatedValue("y");
                                                mFloatView.updateXY(x, y);
                                                if (mB.mViewStateListener != null) {
                                                    mB.mViewStateListener.onPositionUpdate(x, y);
                                                }
                                            }
                                        });
                                        startAnimator();
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            default:
                                break;
                        }
                        return mClick;
                    }
                });
        }
    }


    private void startAnimator() {
        if (mB.mInterpolator == null) {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = new DecelerateInterpolator();
            }
            mB.mInterpolator = mDecelerateInterpolator;
        }
        mAnimator.setInterpolator(mB.mInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (mB.mViewStateListener != null) {
                    mB.mViewStateListener.onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mB.mDuration).start();
        if (mB.mViewStateListener != null) {
            mB.mViewStateListener.onMoveAnimStart();
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

}
