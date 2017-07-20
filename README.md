# 自定义view————雷达图（RadarView）

最近一直迷恋着自定义view，久久不能自拔（其实就是想把自定义学好，然后可以装逼），然后就撸了一个雷达图，接下来就能介绍一下雷达图的究竟

装逼必备图

![效果图](https://github.com/madreain/AndroidNotes/blob/master/images/RadarView.png)

具体实现思路

### 为了效果好看，找中心来开始画图

onSizeChanged(int w, int h, int oldw, int oldh)方法里面，根据View的长宽，获取整个布局的中心坐标，因为整个雷达都是以整个中心开始绘制的。

```

   @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(h, w) / 2 * 0.9f;//根据项目中需求修改半径
        //中心坐标
        centerX = w / 2;
        centerY = h / 2;
    }
    
```

装逼的第一步，中心已经拿到来，先默默装会逼，接下来开始画蜘蛛网

### 画个蜘蛛网，来看看会不会有蜘蛛

蜘蛛网就是一个正多边形，找到起点、经过点的坐标，然后闭合在一起，经过点的坐标将会用到Math.cos()（余弦值）和Math.sin()（正弦值）函数，可以去了解[数学上的三角函数](https://baike.baidu.com/item/%E4%B8%89%E8%A7%92%E5%87%BD%E6%95%B0/1652457?fr=aladdin)

```

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
    
```

发现蜘蛛网间没有被连接，这时候需要将网与网之间进行连接

中心点位置到末端位置就能连接起来组成蜘蛛网来

```
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
    
```
再一次体现来三角函数的重要性，没事看看去[数学上的三角函数](https://baike.baidu.com/item/%E4%B8%89%E8%A7%92%E5%87%BD%E6%95%B0/1652457?fr=aladdin)

持续装逼成功，蜘蛛网完成，你也动起你的小手，今天画个蜘蛛网，明天画个爱心表白女神呀

### 项目中得有文本介绍

对于文本的绘制，首先要找到末端的坐标，由于末端和文本有一定距离，给每个末端加上这个距离以后，再绘制文本。 
另外，当文本在左边时，由于不希望文本和蜘蛛网交叉，我们可以先计算出文本的长度，然后使起始绘制坐标向左偏移这个长度。
[涉及到第一象限等](https://baike.baidu.com/item/%E7%AC%AC%E4%B8%80%E8%B1%A1%E9%99%90/2727462?fr=aladdin)

```

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

```

### 蜘蛛网上得来个覆盖区

path记录坐标点，drawPath()画填充区

```

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
        valuePaint.setAlpha(valuePaintAlpha);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }
    
```

### xml/代码双管齐下设置属性

xml下设置属性，需要用到attrs.xml，将相关属性进行提取出来

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!--//雷达图-->
    <!--mainPaint                               //雷达区画笔-->
    <!--private Paint valuePaint;               //数据区画笔-->
    <!--private Paint textPaint;                //文本画笔-->
    <declare-styleable name="RadarView">
        <attr name="mPaintColor" format="color"/>
        <attr name="mPaintStrokeWidth" format="integer"/>

        <attr name="textPaintColor" format="color"/>
        <attr name="textPaintStrokeWidth" format="integer"/>
        <attr name="textPaintTextSize" format="integer"/>

        <attr name="valuePaintColor" format="color"/>
        <attr name="valuePaintAlpha" format="integer"/>
    </declare-styleable>
</resources>

```

属性写了，属性值的获取
```

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
```

xml中的属性设置

```

<com.radarview.RadarView
            android:id="@+id/rview1"
            app:mPaintColor="@color/colorAccent"
            app:mPaintStrokeWidth="6"
            app:textPaintColor="@color/colorAccent"
            app:textPaintStrokeWidth="6"
            app:textPaintTextSize="50"
            app:valuePaintAlpha="60"
            app:valuePaintColor="@color/colorAccent"
            android:layout_width="match_parent"
            android:layout_height="@dimen/dp400" />

```

代码中设置属性

```

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

```

### 自定义view雷达图全部代码

```
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
        radius = Math.min(h, w) / 2 * 0.8f;//根据项目中需求修改半径
        //中心坐标
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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
        valuePaint.setAlpha(valuePaintAlpha);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }


}



```


[个人博客](https://madreain.github.io)
