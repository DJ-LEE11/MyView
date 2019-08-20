package com.example.banner;

import android.view.View;

import java.util.List;


public class PageBean {

    public View bottomLayout;
    public View openView;
    public List<Object> datas;
    public PageBean(Builder builder) {
        this.bottomLayout = builder.bottomLayout;
        this.openView = builder.openView;
        this.datas = builder.datas;
    }



    public static class Builder<T>{
        View bottomLayout;
        View openView;
        List<T> datas;
        public Builder setIndicator(View bottomLayout){
            this.bottomLayout = bottomLayout;
            return this;
        }
        public Builder setOpenView(View openView){
            this.openView = openView;
            return this;
        }

        public Builder setDataObjects(List<T> datas){
            this.datas = datas;
            return this;
        }


        public PageBean builder(){
            return new PageBean(this);
        }
    }

}
