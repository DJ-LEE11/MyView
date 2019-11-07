package com.example.administrator.myview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banner.BannerViewPager;
import com.example.banner.LoopBean;
import com.example.banner.NormalIndicator;
import com.example.banner.PageBean;
import com.example.banner.PageHelperListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BannerActivity extends BaseActivity {

    private BannerViewPager vBannerViewPager;
    private NormalIndicator vIndicator;
    private int tag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        initView();
        initBanner(null);
    }

    private void initView() {
        vBannerViewPager = findViewById(R.id.vBannerViewPager);
        vIndicator = findViewById(R.id.vIndicator);
        findViewById(R.id.tvTest1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject("{\"tiyaGetInvitation\":{\"name\": 6,\"portrait\":\"xxxxxx\",\"code\":\"9527\"}}");
                    if (jsonObject.has("tiyaGetInvitation")){
                        String  invitation = jsonObject.getString("tiyaGetInvitation");
                        Log.d("jie",invitation);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.tvTest2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBanner(null);
            }
        });
    }

    private void initBanner(String target) {
        List<LoopBean> loopBeen = LoopBean.getLoopData();
        if (target!=null){
            LoopBean loopBean = new LoopBean();
            loopBean.target = target;
            loopBeen.add(0,loopBean);
        }
        PageBean bean = new PageBean.Builder<LoopBean>()
                .setDataObjects(loopBeen)
                .setIndicator(vIndicator)
                .builder();

        vBannerViewPager.setPageListener(bean, R.layout.loop_layout, new PageHelperListener<LoopBean>() {
            @Override
            public void getItemView(View view, final LoopBean bean, int position) {
                ImageView imageView = view.findViewById(R.id.loop_icon);
                TextView tvTitle = view.findViewById(R.id.loop_Title);
                TextView tvDes = view.findViewById(R.id.loop_Des);
                TextView tvtarget = view.findViewById(R.id.tvTarget);
                imageView.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                tvDes.setVisibility(View.GONE);
                tvtarget.setVisibility(View.GONE);
                if (bean.target!=null && !bean.target.isEmpty()){
                    tvtarget.setVisibility(View.VISIBLE);
                    tvtarget.setText(bean.target);
                    tvtarget.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(BannerActivity.this,bean.target,Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    imageView.setVisibility(View.VISIBLE);
                    tvTitle.setVisibility(View.VISIBLE);
                    tvDes.setVisibility(View.VISIBLE);
                    imageView.setImageResource(bean.res);
                    tvTitle.setText(bean.title);
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
            }
        });
    }
}
