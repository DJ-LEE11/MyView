package com.example.bubble.wave;

import android.content.res.Resources;
import android.graphics.Path;
import android.util.TypedValue;

/**
 * 水波对象
 */
public class Wave {

    Path path;          //水波路径
    int width;          //画布宽度（2倍波长）
    int wave;           //波幅（振幅）
    float offsetX;        //水波的水平偏移量
    float offsetY;        //水波的竖直偏移量
    float velocity;       //水波移动速度（像素/秒）
    private float scaleX;       //水平拉伸比例
    private float scaleY;       //竖直拉伸比例

    /**
     * 通过参数构造一个水波对象
     *
     * @param offsetX  水平偏移量
     * @param offsetY  竖直偏移量
     * @param velocity 移动速度（像素/秒）
     * @param scaleX   水平拉伸量
     * @param scaleY   竖直拉伸量
     * @param w        波长
     * @param h        画布高度
     * @param wave     波幅（波宽度）
     */
    Wave(int offsetX, int offsetY, int velocity, float scaleX, float scaleY, int w, int h, int wave) {
        this.width = (int) (2 * scaleX * w); //画布宽度（2倍波长）
        this.wave = wave;           //波幅（波宽）
        this.scaleX = scaleX;       //水平拉伸量
        this.scaleY = scaleY;       //竖直拉伸量
        this.offsetX = offsetX;     //水平偏移量
        this.offsetY = offsetY;     //竖直偏移量
        this.velocity = velocity;   //移动速度（像素/秒）
        this.path = buildWavePath(width, h);
    }

    public void updateWavePath(int w, int h, int waveHeight) {
        this.wave = (wave > 0) ? wave : waveHeight / 2;
        this.width = (int) (2 * scaleX * w);  //画布宽度（2倍波长）
        this.path = buildWavePath(width, h);
    }


    private Path buildWavePath(int width, int height) {
        int DP = dp2px(1);//一个dp在当前设备表示的像素量（水波的绘制精度设为一个dp单位）
        if (DP < 1) {
            DP = 1;
        }

        int wave = (int) (scaleY * this.wave);//计算拉伸之后的波幅

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(0, height - wave);

        for (int x = DP; x < width; x += DP) {
            path.lineTo(x, height - wave - wave * (float) Math.sin(4.0 * Math.PI * x / width));
        }

        path.lineTo(width, height - wave);
        path.lineTo(width, 0);
        path.close();
        return path;
    }

    private static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, Resources.getSystem().getDisplayMetrics());
    }
}