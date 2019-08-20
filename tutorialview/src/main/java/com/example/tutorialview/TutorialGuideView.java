package com.example.tutorialview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author 李栋杰
 * @time 2019/8/14  16:53
 * @desc 引导页
 */
public class TutorialGuideView extends RelativeLayout {

    private Paint mPaintText;
    private Paint mPaintHole;
    private Paint mPaintBackground;
    private Paint mPaintBtn;
    private Paint mPaintGrayCircle;
    private RectF mHoleRect;

    private RectF mBtnRect;

    // 绘制
    private Context mContext;
    private int mWidth;
    private int mHeight;

    private ICloseListener mICloseListener;

    public interface ICloseListener {
        void close();
    }

    public void setCloseListener(ICloseListener closeListener) {
        this.mICloseListener = closeListener;
    }

    public TutorialGuideView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        removeAllViews();
        inflate(context, R.layout.view_tutorial, null);
        setBackgroundColor(Color.parseColor("#00000000"));
    }

    public void setHoleRect(RectF rect) {
        if (mHoleRect == null) {
            mHoleRect = new RectF();
        }
        mHoleRect.set(rect);
    }

    private void prepareDraw() {
        // 文字画笔
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(0xfffffcf5);
        mPaintText.setTextAlign(Paint.Align.LEFT);
        mPaintText.setTextSize(dip2px(17));

        // 背景画笔
        mPaintBackground = new Paint();
        mPaintBackground.setColor(0xB2000000);
        mPaintBackground.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

        // 镂空画笔
        mPaintHole = new Paint();
        mPaintHole.setAntiAlias(true);
        mPaintHole.setColor(0xFFFFFFFF);
        mPaintHole.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

        //按钮画笔
        mPaintBtn = new Paint();
        mPaintBtn.setAntiAlias(true);
        mPaintBtn.setColor(0xFFFFFFFF);


        //阴影画笔
        mPaintGrayCircle = new Paint();
        mPaintGrayCircle.setAntiAlias(true);
        mPaintGrayCircle.setColor(0xFFFFFFFF);
        mPaintGrayCircle.setAlpha(25);
        mPaintGrayCircle.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if (isValidClick(x, y)) {
                    setVisibility(GONE);
                    if (mICloseListener != null) {
                        mICloseListener.close();
                    }
                }
                break;
            default:

                break;
        }
        return true;
    }

    private boolean isValidClick(float x, float y) {
        if (mBtnRect == null) {
            return false;
        }
        if (mBtnRect.left < x && x < mBtnRect.right && mBtnRect.top < y && y < mBtnRect.bottom) {
            return true;
        }
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mWidth != w) {
            mWidth = w;
            mHeight = h;
            prepareDraw();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
        float startLeftPosition;
        float startTopPosition;
        float centerXPosition = getScreenWidth(getContext()) / 2;
        //绘制黑色底图颜色
        canvas.drawRect(0, 0, mWidth, mHeight, mPaintBackground);
        //绘制外层圆阴影
        float centerX = (mHoleRect.right - mHoleRect.left) / 2 + mHoleRect.left;
        float centerY = (mHoleRect.bottom - mHoleRect.top) / 2 + mHoleRect.top- dip2px(21);
        canvas.drawCircle(centerX, centerY, dip2px(60), mPaintGrayCircle);
        //绘制镂空圆
        canvas.drawCircle(centerX, centerY, dip2px(48), mPaintHole);
        //绘制图片
        startTopPosition = centerY + dip2px(60);
        startLeftPosition = centerX - dip2px(90);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_tutorial)).getBitmap();
        canvas.drawBitmap(bitmap, startLeftPosition, startTopPosition, null);
        //绘制文字
        mPaintText.setTextAlign(Paint.Align.CENTER);
        startTopPosition += dip2px(65);
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        float ascent = fontMetrics.ascent;
        float descent = fontMetrics.descent;
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        float textHeight = bottom - top;
        canvas.drawText("这是一个引导页示例~~", centerXPosition, startTopPosition, mPaintText);
        //绘制按钮
        startTopPosition =  startTopPosition+textHeight-dip2px(5);
        mBtnRect = new RectF(centerXPosition - dip2px(45), startTopPosition, centerXPosition + dip2px(45), startTopPosition + dip2px(44));
        canvas.drawRoundRect(mBtnRect, dip2px(24), dip2px(24), mPaintBtn);
        float distance = (bottom - top) / 2 - bottom;
        float baseline = mBtnRect.centerY() + distance;
        mPaintText.setColor(0xff000000);
        mPaintText.setTextSize(dip2px(14));
        canvas.drawText("点击看看", mBtnRect.centerX(), baseline, mPaintText);
        canvas.restoreToCount(layerId);
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int getScreenWidth(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

}
