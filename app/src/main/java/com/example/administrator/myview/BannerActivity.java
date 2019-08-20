package com.example.administrator.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banner.BannerViewPager;
import com.example.banner.LoopBean;
import com.example.banner.NormalIndicator;
import com.example.banner.PageBean;
import com.example.banner.PageHelperListener;

import java.util.List;

public class BannerActivity extends AppCompatActivity {

    private BannerViewPager vBannerViewPager;
    private NormalIndicator vIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        initView();
        initBanner();
    }

    private void initView() {
        vBannerViewPager = findViewById(R.id.vBannerViewPager);
        vIndicator = findViewById(R.id.vIndicator);
    }

    private void initBanner() {
        List<LoopBean> loopBeen = LoopBean.getLoopData();
        PageBean bean = new PageBean.Builder<LoopBean>()
                .setDataObjects(loopBeen)
                .setIndicator(vIndicator)
                .builder();

        vBannerViewPager.setPageListener(bean, R.layout.loop_layout, new PageHelperListener<LoopBean>() {
            @Override
            public void getItemView(View view, final LoopBean bean, int position) {
                ImageView imageView = view.findViewById(R.id.loop_icon);
                imageView.setImageResource(bean.res);
                TextView tvTitle = view.findViewById(R.id.loop_Title);
                tvTitle.setText(bean.title);
                TextView tvDes = view.findViewById(R.id.loop_Des);
                tvDes.setText(bean.des);
                tvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(BannerActivity.this,bean.title,Toast.LENGTH_LONG).show();
                    }
                });
                tvTitle.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });
            }
        });
    }
}
