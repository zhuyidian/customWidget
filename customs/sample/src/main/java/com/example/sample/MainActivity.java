package com.example.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView list;
    private CustomAdapter mAdapter;
    private ArrayList<CustomBean> mListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);

        initValue();
        initListen();
    }

    private void initValue(){
        mListData = new ArrayList<>();

        //add circleTick
        CustomBean beanCircleTick = new CustomBean("circleTick",
                "１,父布局自适应大小 2,测量指定大小 3,onDraw",
                "com.example.sample.CircleTickActivity");
        mListData.add(beanCircleTick);
        //add circlePan
        CustomBean beanCirclePan = new CustomBean("circlePan",
                "１,父布局指定大小 2,资源自适应大小 3,onLayout",
                "com.example.sample.CirclePanActivity");
        mListData.add(beanCirclePan);
        //add calendar
        CustomBean beanCalendar = new CustomBean("calendar",
                "１,父布局自适应大小 2,测量指定大小 3,onDraw",
                "com.example.sample.CalendarActivity");
        mListData.add(beanCalendar);

        CustomBean beanDraw = new CustomBean("draw",
                "区域绘制",
                "com.example.sample.MoveDarwActivity");
        mListData.add(beanDraw);

        CustomBean beanlight = new CustomBean("Light",
                "智能设想灯",
                "com.example.sample.LightActivity");
        mListData.add(beanlight);

        mAdapter = new CustomAdapter(MainActivity.this,mListData);
        list.setAdapter(mAdapter);
    }

    private void initListen(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomBean bean = mListData!=null?mListData.get(position):null;
                if(bean!=null){
                    Intent in = new Intent();
                    in.setClassName(MainActivity.this, bean.getActivityUri());
                    MainActivity.this.startActivity(in);
                }
            }
        });
    }
}
