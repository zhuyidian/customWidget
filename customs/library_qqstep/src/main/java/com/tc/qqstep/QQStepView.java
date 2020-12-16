package com.tc.qqstep;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.tc.lock.R;

/**
 * Email 240336124@qq.com
 * Created by Darren on 2017/5/20.
 * Version 1.0
 * Description: 仿QQ运动步数
 */
public class QQStepView extends View {

    private int mOuterColor = Color.RED;
    private int mInnerColor = Color.BLUE;
    private int mBorderWidth = 20;// 20px
    private int mStepTextSize;
    private int mStepTextColor;

    private Paint mOutPaint,mInnerPaint,mTextPaint;

    // 总共的，当前的步数
    private int mStepMax = 0;
    private int mCurrentStep = 0;

    public QQStepView(Context context) {
        this(context,null);
    }

    public QQStepView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QQStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 1.分析效果；
        // 2.确定自定义属性，编写attrs.xml
        // 3.在布局中使用
        // 4.在自定义View中获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.QQStepView);
        mOuterColor = array.getColor(R.styleable.QQStepView_outerColor,mOuterColor);
        mInnerColor = array.getColor(R.styleable.QQStepView_innerColor, mInnerColor);
        mBorderWidth = (int) array.getDimension(R.styleable.QQStepView_borderWidth,mBorderWidth);
        mStepTextSize = array.getDimensionPixelSize(R.styleable.QQStepView_stepTextSize,mStepTextSize);
        mStepTextColor = array.getColor(R.styleable.QQStepView_stepTextColor, mStepTextColor);
        array.recycle();

        mOutPaint = new Paint();
        mOutPaint.setAntiAlias(true);  //抗锯齿
        mOutPaint.setStrokeWidth(mBorderWidth);  //画笔宽度
        mOutPaint.setColor(mOuterColor);
        mOutPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutPaint.setStyle(Paint.Style.STROKE);// 画笔空心　　FILL实心

        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStrokeWidth(mBorderWidth);
        mInnerPaint.setColor(mInnerColor);
        mInnerPaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerPaint.setStyle(Paint.Style.STROKE);// 画笔空心


        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mStepTextColor);
        mTextPaint.setTextSize(mStepTextSize);
        // 5.onMeasure()
        // 6.画外圆弧 ，内圆弧 ，文字
        // 7.其他
    }


    // 5.onMeasure()
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 调用者在布局文件中可能  wrap_content
        // 获取模式 AT_MOST  40DP

        // 宽度高度不一致 取最小值，确保是个正方形
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width>height?height:width,width>height?height:width);
    }

    // 6.画外圆弧 ，内圆弧 ，文字
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 6.1 画外圆弧    分析：圆弧闭合了  思考：边缘没显示完整  描边有宽度 mBorderWidth  圆弧

        // int center = getWidth()/2;
        // int radius = getWidth()/2 - mBorderWidth/2;
        // RectF rectF = new RectF(center-radius,center-radius
        // ,center+radius,center+radius);

        RectF rectF = new RectF(mBorderWidth/2,mBorderWidth/2
        ,getWidth()-mBorderWidth/2,getHeight()-mBorderWidth/2);
        // 研究研究

        canvas.drawArc(rectF,135,270,false,mOutPaint);

        if(mStepMax == 0)return;
        // 6.2 画内圆弧  怎么画肯定不能写死  百分比  是使用者设置的从外面传
        float sweepAngle = (float)mCurrentStep/mStepMax;
        canvas.drawArc(rectF,135,sweepAngle*270,false,mInnerPaint);

        // 6.3 画文字
        String stepText = mCurrentStep+"";
        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(stepText, 0, stepText.length(), textBounds);
        int dx = getWidth()/2 - textBounds.width()/2;
        // 基线 baseLine
        Paint.FontMetricsInt  fontMetrics = mTextPaint.getFontMetricsInt();
        int dy = (fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
        int baseLine = getHeight()/2 + dy;
        canvas.drawText(stepText,dx,baseLine,mTextPaint);
    }

    // 7.其他 写几个方法动起来
    public synchronized void setStepMax(int stepMax){
        this.mStepMax = stepMax;
    }

    public synchronized void setCurrentStep(int currentStep){
        this.mCurrentStep = currentStep;
        // 不断绘制  onDraw()
        invalidate();
    }

    public synchronized void startPlay(){
        // 属性动画 后面讲的内容
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, 3000);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentStep = (float) animation.getAnimatedValue();
                setCurrentStep((int)currentStep);
            }
        });
        valueAnimator.start();
    }
}
