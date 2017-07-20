package com.radarview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RadarView default_rview;
    private RadarView rview1;
    private RadarView rview2;

    ArrayList<RadarModel> radarModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        default_rview = (RadarView) findViewById(R.id.default_rview);
        rview1 = (RadarView) findViewById(R.id.rview1);
        rview2 = (RadarView) findViewById(R.id.rview2);

        radarModelArrayList.add(new RadarModel("a", 100));
        radarModelArrayList.add(new RadarModel("b", 50));
        radarModelArrayList.add(new RadarModel("c", 60));
        radarModelArrayList.add(new RadarModel("d", 300));
        radarModelArrayList.add(new RadarModel("e", 20));
        initDefaultView();
        initRView1();
        initRView2();
    }


    //默认的设置
    private void initDefaultView() {
        default_rview.setData(radarModelArrayList);
    }

    //xml修改相关属性
    private void initRView1() {
        rview1.setData(radarModelArrayList);
    }

    //代码中修改
    private void initRView2() {
        rview2.setData(radarModelArrayList);
        //设置最大值
        rview2.setMaxValue(400);
        //雷达区相关设置
        rview2.setmPaintColor(Color.GREEN);
        rview2.setmPaintStrokeWidth(3);
        //文字相关设置
        rview2.settextPaintColor(Color.GREEN);
        rview2.settextPaintStrokeWidth(3);
        rview2.settextPaintTextSize(16);
        //填充区相关设置
        rview2.setvaluePaintAlpha(10);
        rview2.setvaluePaintColor(Color.GREEN);
    }
}
