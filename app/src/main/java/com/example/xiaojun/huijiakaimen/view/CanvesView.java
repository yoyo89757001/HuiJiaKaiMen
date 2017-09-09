package com.example.xiaojun.huijiakaimen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017/9/9.
 */

public class CanvesView extends View {
    private Paint mPaint;

    public CanvesView(Context context) {
        super(context);
        //new 出来会调用此方法
    }

    public CanvesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //在布局中使用会调用此方法
        mPaint=new Paint();
        // 设置Paint为无锯齿
        mPaint.setAntiAlias(true);
        // 设置Paint的颜色
        mPaint.setColor(Color.RED);
        // 设置paint的风格为“空心”
        // 当然也可以设置为"实心"(Paint.Style.FILL)
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置paint的外框宽度
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        //RectF(float left, float top, float right, float bottom)
       // drawRect(RectF rect, Paint paint) //绘制区域，参数一为RectF一个区域
        Log.d("CanvesView", "gggggggggggg");
        // 绘制一空心个矩形
        canvas.drawRect((320 - 80), 20, 500, 700, mPaint);
        // 画一个圆
        canvas.drawCircle(40, 30, 20, mPaint);
        // 画一个正放形
        canvas.drawRect(20, 70, 70, 120, mPaint);
        // 画一个长方形
        canvas.drawRect(20, 170, 90, 130, mPaint);
        // 画一个椭圆
        RectF re = new RectF(20, 230, 100, 190);
        canvas.drawOval(re, mPaint);
    }
}
