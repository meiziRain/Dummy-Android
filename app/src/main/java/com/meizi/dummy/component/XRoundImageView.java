package com.meizi.dummy.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Classname XRoundImageView
 * @Description TODO
 * @Date 2020/2/11 10:38
 * @Created by jion
 */
public class XRoundImageView extends View {

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 圆形半径
     */
    private int mCircleRadius;

    public XRoundImageView(Context context) {
        this(context, null);
    }

    /**
     * 第一步：构造器
     *
     * @param context 上下文
     */
    public XRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化画笔
        mPaint = new Paint();
        // 防止边缘出现锯齿
        mPaint.setAntiAlias(true);
    }

    /**
     * 第二步：测量
     * 强制宽高相等
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 取宽高最小值
        int length = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // 圆的半径是边长的一半
        mCircleRadius = length / 2;
        // 强制宽高相等
        setMeasuredDimension(length, length);
    }

    /**
     * 第三步：绘画
     * 画圆、缩放头像
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 画圆形
        canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius, mPaint);
    }
}
