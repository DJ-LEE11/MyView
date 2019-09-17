package com.example.administrator.myview;

import android.support.v7.app.AppCompatActivity;

import com.example.administrator.util.DensityUtils;

/**
 * @author 李栋杰
 * @time 2019/9/17  14:24
 * @desc ${TODD}
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        DensityUtils.setCustomDensity(this, getApplication());
    }
}
