package com.example.banner;


import java.util.ArrayList;
import java.util.List;

public class LoopBean {
    public String title;
    public String des;
    public int res;

    private static final int[] RESURL = {
            R.mipmap.ic_loading1,
            R.mipmap.ic_loading2,
            R.mipmap.ic_loading3,
            R.mipmap.ic_loading4};


    private static final String[] TITLE = {"欢聊", "精准声鉴", "精准匹配", "私密聊天"};

    private static final String[] DES = {"用声音遇见有趣的人"
            , "用声音制作一张独一无二的名片吧"
            , "喜欢TA的声音，一键立刻开聊"
            , "和喜欢的人愉快的聊起来吧"};


    public static List<LoopBean> getLoopData() {
        List<LoopBean> data = new ArrayList<>();
        for (int i = 0; i < TITLE.length; i++) {
            LoopBean bean = new LoopBean();
            bean.res = RESURL[i];
            bean.title = TITLE[i];
            bean.des = DES[i];
            data.add(bean);
        }
        return data;
    }
}
