package com.radarview;

/**
 * Created by wujun on 2017/7/20.
 * 雷达实体类
 * @author madreain
 * @desc
 */

public class RadarModel {
    String title;
    double value;

    public RadarModel(String title, double value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
