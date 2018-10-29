package com.example.administrator.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.bubble.BubbleView;

public class BubbleActivity extends AppCompatActivity {

    private BubbleView mBubbleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        mBubbleView = findViewById(R.id.bubbleView);
        mBubbleView.startAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBubbleView.stop();
    }
}
