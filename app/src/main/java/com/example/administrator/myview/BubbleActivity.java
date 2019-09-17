package com.example.administrator.myview;

import android.os.Bundle;

import com.example.bubble.BubbleView;
import com.example.bubble.wave.MultiWave;

public class BubbleActivity extends BaseActivity {

    private BubbleView mBubbleView;
    private MultiWave mMultiWave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        mBubbleView = findViewById(R.id.bubbleView);
        mMultiWave = findViewById(R.id.multiWave);
        mBubbleView.startAnimation();
        mMultiWave.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBubbleView.stop();
        mMultiWave.stop();
    }
}
