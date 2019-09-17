package com.example.administrator.myview;

import android.os.Bundle;
import android.view.View;

import com.example.floatwindow.FloatWindowManager;

public class FloatWindowActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_window);
    }

    public void initF(View view) {
        FloatWindowManager.getInstance(getApplicationContext()).open();
    }

    public void hide(View view) {
        FloatWindowManager.getInstance(getApplicationContext()).hide();
    }


    public void des(View view) {
        FloatWindowManager.getInstance(getApplicationContext()).close();
    }

}
