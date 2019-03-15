package com.example.administrator.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.rippleview.RippleView;

public class RippleActivity extends AppCompatActivity {

    private RippleView mRippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);
        mRippleView = findViewById(R.id.rippleView);
    }

    public void start(View view) {
        mRippleView.startAnim();
    }

    public void pause(View view) {
        mRippleView.stopAnim();
    }

    public void destroy(View view) {
        mRippleView.realStop();
    }
}
