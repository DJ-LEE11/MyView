package com.example.floatwindow.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.floatwindow.R;


/**
 * @author 李栋杰
 * @time 2019/3/14  17:10
 * @desc ${TODD}
 */
public class FloatWindowLayout extends RelativeLayout {

    private Context mContext;
    private ImageView mIvHeader;
    private FlowWaveView mWaveTop;
    private FlowWaveView mWaveBottom;

    public FloatWindowLayout(Context context) {
        this(context,null);
    }

    public FloatWindowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FloatWindowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.float_window_layout, this);
        mIvHeader = inflate.findViewById(R.id.ivHeader);
        mWaveBottom = inflate.findViewById(R.id.waveBottom);
        mWaveTop = inflate.findViewById(R.id.waveTop);
        mIvHeader.setImageResource(R.drawable.icon);
    }

    public void setHead(String url){
        if (!TextUtils.isEmpty(url)){

        }
    }

    public void startAnim(){
        mWaveBottom.startAnimation();
        mWaveTop.startAnimation();
    }

    public void stopAnim(){
        mWaveBottom.stopAnimation();
        mWaveTop.stopAnimation();
    }

}
