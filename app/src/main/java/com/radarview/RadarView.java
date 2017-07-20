package com.radarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by wujun on 2017/7/20.
 * 雷达图／蜘蛛网图
 *
 * @author madreain
 * @desc 根据传递的数据进行画图，支持雷达区颜色、宽度的修改；支持字体展示的大小、颜色、宽度相关修改；填充区颜色和透明度的修改；支持xml和代码双重设置
 */

public class RadarView extends View {

    private int count;                //数据个数
    private float angle;               //角度
    private float radius;                   //网格最大半径
    private int centerX;                  //中心X
    private int centerY;                  //中心Y
    private double maxValue;             //数据最大值

    private Paint mainPaint;                //雷达区画笔
    private int mPaintColor;                //颜色
    private int mPaintStrokeWidth;          //宽度

    private Paint textPaint;                //文本画笔
    private int textPaintColor;
    private int textPaintStrokeWidth;
    private int textPaintTextSize;         //字体大小

    private Paint valuePaint;               //数据区画笔
    private int valuePaintColor;
    private int valuePaintAlpha;            //透明度

    ArrayList<RadarModel> radarModelArrayList = new ArrayList<>();

    public RadarView(Context context) {
        super(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView);
        mPaintColor = typedArray.getColor(R.styleable.RadarView_mPaintColor, Color.BLACK);
        mPaintStrokeWidth = typedArray.getInteger(R.styleable.RadarView_mPaintStrokeWidth, 1);

        textPaintColor = typedArray.getColor(R.styleable.RadarView_textPaintColor, Color.BLACK);
        textPaintStrokeWidth = typedArray.getInteger(R.styleable.RadarView_textPaintStrokeWidth, 1);
        textPaintTextSize = typedArray.getInteger(R.styleable.RadarView_textPaintTextSize, 30);


        valuePaintColor = typedArray.getColor(R.styleable.RadarView_valuePaintColor, Color.BLACK);
        valuePaintAlpha = typedArray.getInteger(R.styleable.RadarView_valuePaintAlpha, 127);

    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(h, w) / 2 * 0.9f;
        //中心坐标
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        moveTo、 setLastPoint、 lineTo 和 close
//        addXxx与arcTo
//        isEmpty、 isRect、isConvex、 set 和 offset
        initData();

        mainPaint = new Paint();
        mainPaint.setStyle(Paint.Style.STROKE);

        mainPaint.setStrokeWidth(mPaintStrokeWidth);
        mainPaint.setColor(mPaintColor);

        drawPolygon(canvas);
        drawLines(canvas);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);

        textPaint.setTextSize(textPaintTextSize);
        textPaint.setColor(textPaintColor);
        textPaint.setStrokeWidth(textPaintStrokeWidth);


        drawText(canvas);

        valuePaint = new Paint();
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        valuePaint.setColor(valuePaintColor);
        valuePaint.setAlpha(valuePaintAlpha);

        drawRegion(canvas);
    }


    /**
     * 设置你想要展示的数据
     * 这里RadarModel 根据要显示的标题及其值 建的model  实际项目根据实际项目修改
     *
     * @param radarModelArrayList
     */
    public void setData(ArrayList<RadarModel> radarModelArrayList) {
        this.radarModelArrayList = radarModelArrayList;
    }


    /***
     * 初始化加载数据
     */
    private void initData() {
        count = radarModelArrayList.size();
        //根据展示的个数设置角度
        angle = (float) (Math.PI * 2 / count);
        //如果是默认值0就去取数据里面的最大值   反之就说明已经设置过最大值了
        if (maxValue == 0) {
            //设置展示数据的最大值
            maxValue = getmaxValue();
        }
    }

    /**
     * 默认是从数据里面取最大值
     *
     * @return
     */
    private double getmaxValue() {
        double maxValue = 0;
        for (RadarModel radarModel : radarModelArrayList) {
            if (radarModel.getValue() > maxValue) {
                maxValue = radarModel.getValue();
            }
        }
        return maxValue;
    }

    /**
     * 设置最大数值
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    /***
     *
     * @param mPaintColor
     */
    public void setmPaintColor(int mPaintColor) {
        this.mPaintColor = mPaintColor;
    }

    /***
     *
     * @param mPaintStrokeWidth
     */
    public void setmPaintStrokeWidth(int mPaintStrokeWidth) {
        this.mPaintStrokeWidth = mPaintStrokeWidth;
    }

    /**
     *
     * @param textPaintColor
     */
    public void settextPaintColor(int textPaintColor) {
        this.textPaintColor = textPaintColor;
    }

    /***
     *
     * @param textPaintStrokeWidth
     */
    public void settextPaintStrokeWidth(int textPaintStrokeWidth) {
        this.textPaintStrokeWidth = textPaintStrokeWidth;
    }


    public void settextPaintTextSize(int textPaintTextSize) {
        this.textPaintTextSize = textPaintTextSize;
    }

    /**
     *
     * @param valuePaintColor
     */
    public void setvaluePaintColor(int valuePaintColor) {
        this.valuePaintColor = valuePaintColor;
    }

    /**
     *
     * @param valuePaintAlpha
     */
    public void setvaluePaintAlpha(int valuePaintAlpha) {
        this.valuePaintAlpha = valuePaintAlpha;
    }



    /**
     * 绘制正多边形
     *
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        //r是蜘蛛丝之间的间距
        float r = radius / (count - 1);
        //中心点不用绘制
        for (int i = 0; i < count; i++) {
            float curR = r * i;//当前半径
//           清除Path中的内容
//           reset不保留内部数据结构，但会保留FillType.
//           rewind会保留内部的数据结构，但不保留FillType
            path.reset();
            for (int j = 0; j < count; j++) {
                if (j == 0) {
                    //移到正多边形的起初位置
                    path.moveTo(centerX + curR, centerY);
                } else {
                    //根据半径，计算出蜘蛛丝上每个点的坐标
                    float x = (float) (centerX + curR * Math.cos(angle * j));
                    float y = (float) (centerY + curR * Math.sin(angle * j));
                    path.lineTo(x, y);
                }
            }

            path.close();//闭合路径
            canvas.drawPath(path, mainPaint);
        }
    }


    /**
     * 绘制从中心到末端的直线
     *
     * @param canvas
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            path.reset();
            //中心点开始
            path.moveTo(centerX, centerY);
            //最外层正多边形的点
            float x = (float) (centerX + radius * Math.cos(angle * i));
            float y = (float) (centerY + radius * Math.sin(angle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < count; i++) {
            String title = radarModelArrayList.get(i).getTitle();
            float x = (float) (centerX + (radius + fontHeight / 2) * Math.cos(angle * i));
            float y = (float) (centerY + (radius + fontHeight / 2) * Math.sin(angle * i));
            if (angle * i >= 0 && angle * i <= Math.PI / 2) {//第4象限
                canvas.drawText(title, x, y, textPaint);
            } else if (angle * i >= 3 * Math.PI / 2 && angle * i <= Math.PI * 2) {//第3象限
                canvas.drawText(title, x, y, textPaint);
            } else if (angle * i > Math.PI / 2 && angle * i <= Math.PI) {//第2象限
                float dis = textPaint.measureText(title);//文本长度
                canvas.drawText(title, x - dis, y, textPaint);
            } else if (angle * i >= Math.PI && angle * i < 3 * Math.PI / 2) {//第1象限
                float dis = textPaint.measureText(title);//文本长度
                canvas.drawText(title, x - dis, y, textPaint);
            }
        }
    }

    /**
     * 绘制覆盖区域
     *
     * @param canvas
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        valuePaint.setAlpha(255);
        for (int i = 0; i < count; i++) {
            double percent = radarModelArrayList.get(i).getValue() / maxValue;
            float x = (float) (centerX + radius * Math.cos(angle * i) * percent);
            float y = (float) (centerY + radius * Math.sin(angle * i) * percent);
            if (i == 0) {
                path.moveTo(x, centerY);
            } else {
                path.lineTo(x, y);
            }
            //绘制小圆点
            canvas.drawCircle(x, y, 10, valuePaint);
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }


}

