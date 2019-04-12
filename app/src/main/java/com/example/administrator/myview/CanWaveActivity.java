package com.example.administrator.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.util.ButtonUtils;
import com.example.canwave.CanLayoutView;

public class CanWaveActivity extends AppCompatActivity implements View.OnClickListener {

    private CanLayoutView mCanWave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_wave);
        mCanWave = findViewById(R.id.canLayout);
        findViewById(R.id.tvStart).setOnClickListener(this);
        findViewById(R.id.tvStop).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvStart:
                if (ButtonUtils.isFastDoubleClick(-1,500)){
                    return;
                }
                mCanWave.startAnim();
                mCanWave.riseCap();
                break;
            case R.id.tvStop:
                if (ButtonUtils.isFastDoubleClick()){
                    return;
                }
                mCanWave.stopAnim();
                mCanWave.dropCap();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCanWave.stopAnim(true);
    }
}
