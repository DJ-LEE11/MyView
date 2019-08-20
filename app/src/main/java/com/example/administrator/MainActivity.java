package com.example.administrator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.myview.BubbleActivity;
import com.example.administrator.myview.CanWaveActivity;
import com.example.administrator.myview.FloatWindowActivity;
import com.example.administrator.myview.R;
import com.example.administrator.myview.RecordLayoutActivity;
import com.example.administrator.myview.RippleActivity;
import com.example.administrator.myview.TutorialActivity;
import com.example.administrator.util.IntentUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.tvBubble).setOnClickListener(this);
        findViewById(R.id.tvCanWave).setOnClickListener(this);
        findViewById(R.id.tvRecordLayout).setOnClickListener(this);
        findViewById(R.id.tvFloatWindow).setOnClickListener(this);
        findViewById(R.id.tvRipple).setOnClickListener(this);
        findViewById(R.id.tvTutorial).setOnClickListener(this);
        findViewById(R.id.tvBanner).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBubble:
                goTo(BubbleActivity.class);
                break;
            case R.id.tvCanWave:
                goTo(CanWaveActivity.class);
                break;
            case R.id.tvRecordLayout:
                goTo(RecordLayoutActivity.class);
                break;
            case R.id.tvFloatWindow:
                goTo(FloatWindowActivity.class);
                break;
            case R.id.tvRipple:
                goTo(RippleActivity.class);
                break;
            case R.id.tvTutorial:
                goTo(TutorialActivity.class);
                break;
            case R.id.tvBanner:
                goTo(RippleActivity.class);
                break;
        }
    }

    private void goTo(Class clazz) {
        new IntentUtils.Builder(this)
                .to(clazz)
                .build()
                .start();
    }

}
