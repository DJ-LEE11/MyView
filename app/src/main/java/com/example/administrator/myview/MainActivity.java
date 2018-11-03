package com.example.administrator.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.testView.TestActivity;
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
        findViewById(R.id.tvTest).setOnClickListener(this);
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
            case R.id.tvTest:
                goTo(TestActivity.class);
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
