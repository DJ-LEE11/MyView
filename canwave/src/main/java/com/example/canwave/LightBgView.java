package com.example.canwave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author 李栋杰
 * @time 2018/10/25  17:12
 * @desc ${TODD}
 */
public class LightBgView extends RelativeLayout {

    private Paint mPaint;
    private int mStartColor =  Color.parseColor("#19fbfbfb");
    private int mEndColor =  Color.parseColor("#00fdfdfd");


    public LightBgView(Context context) {
        this(context,null);
    }

    public LightBgView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LightBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        clipCanvas(canvas);
        int width = getWidth();
        int height = getHeight();
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, height, mStartColor, mEndColor, Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);

        Path pathBottom = new Path();
        pathBottom.moveTo(dp2pix(0),0);
        pathBottom.lineTo((float) (width*0.9),0);
        pathBottom.lineTo((float) (width*0.3),height);
        pathBottom.lineTo(0,height);
        pathBottom.close();


        Path pathTop = new Path();
        pathTop.moveTo(0,0);
        pathTop.lineTo((float) (width*0.6),0);
        pathTop.lineTo(0,height);
        pathTop.close();

        canvas.drawPath(pathBottom,mPaint);
        canvas.drawPath(pathTop,mPaint);
    }

    //裁剪波浪圆角
    private void clipCanvas(Canvas canvas) {
        Path path = new Path();
        float radius  = dp2pix(12);
        float[] radiusArray = {radius, radius, radius, radius, radius, radius, radius, radius};
        path.addRoundRect(new RectF(0, 0, getWidth() , getHeight()), radiusArray, Path.Direction.CW);
        canvas.clipPath(path);
    }

    private int dp2pix(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pix = (int) (dp * scale + 0.5f);
        return pix;
    }
}
