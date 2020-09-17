package com.tc.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;


import java.math.BigDecimal;

public class CircleTickView extends View {
    //===================整体
    private static int fixAngleRange = 160;  //角度范围必须在使用前指定
    private static int fixCircleRadius = 400;  //控件半径为<=0，那么使用布局半径，一般使用时肯定会设置半径
    private static boolean isDebug = false;  //可以动态设置刻度参数，以供调试
    /**
     * 背景颜色
     */
    private int mRoundBackgroundColor;
    /**
     * 布局整体半径
     */
    private int mCircleRadius;
    /**
     * 布局整体高
     */
    private int mHeight;
    /**
     * 布局整体宽
     */
    private int mWidth;
    /**
     * 布局整体圆心的x坐标
     */
    private int mCircleCenterX;
    /**
     * 布局整体圆心坐标y
     */
    private int mCircleCenterY;
    /**
     * 刻度的总数
     */
    private int mTickMaxCount;
    /**
     * 角度，单个间隔块所占的角度
     */
    private int mSinglPoint;
    private int[] angleList;
    /**
     * 触摸半径
     */
    private int touchRadius;
    /**
     * 触摸精度
     */
    private int touchPrecision;
    /**
     * 动态调整角度
     */
    private int touchRangle;
    /**
     * 倍数
     */
    private int mValue = 1;
    //===================big
    /**
     * 大刻度宽度
     */
    private float mLineWidthBig = 0.3f;
    /**
     * 大圆环内的半径
     */
    private int mCircleRingRadiusBig;
    /**
     * 大刻度长度
     */
    private int mTickStrokeSizeBig;
    /**
     * 默认大刻度的颜色
     */
    private int mDefaultTickColorBig;
    private RectF mRecfBig;
    /**
     * 大圆环的paint
     */
    private Paint mCircleRingPaintBig;
    //===================small
    /**
     * 小刻度宽度
     */
    private float mLineWidthSmall = 0.3f;
    /**
     * 小圆环内的半径
     */
    private int mCircleRingRadiusSmall;
    /**
     * 小刻度长度
     */
    private int mTickStrokeSizeSmall;
    /**
     * 默认小刻度的颜色
     */
    private int mDefaultTickColorSmall;
    /**
     * 小刻度动态变化颜色
     */
    private int mDefaultTickColorSmallDynamic;
    private RectF mRecfSmall;
    /**
     * 小圆环的paint
     */
    private Paint mCircleRingPaintSmall;
    //==================triangle
    private Path mCircleTrianglePath;
    /**
     * 小圆环内的半径
     */
    private Paint mCircleTrianglePaint;
    /**
     * 默认小刻度的颜色
     */
    private int mDefaultTriangleColor;
    //===================other
    /**
     * 中间文本颜色
     */
    private int mCenterTextColor;
    /**
     * 中间文本描述
     */
    private String mCenterText = "";
    /**
     * 中间文本大小
     */
    private float mCenterTextSize;
    /**
     * 中间文本的paint
     */
    private Paint mCenterTextPaint;
    /**
     * 中间文字的属性
     */
    private Rect mCenterTextBounds;

    public static int[] getPositionForAngle(int screenWidth,int screenHeight){
        int[] position = new int[2];
        int rr = fixCircleRadius;
        double radian1 = Math.toRadians((180-fixAngleRange)/2+90); //角度转弧度
        double x1 = rr*Math.cos(radian1);
        double y1 = rr*Math.sin(radian1);

        position[0] = screenWidth-fixCircleRadius-(int)x1;
        position[1] = screenHeight/2-fixCircleRadius;
        return position;
    }
    public CircleTickView(Context context) {
        this(context, null);
        Log.v("circleTick", "---CircleTickView---1");
        init(context, null);
    }
    public CircleTickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setWillNotDraw(false);  //更换成ＶiewGroup后不执行OnDraw
        Log.v("circleTick", "---CircleTickView---2");
        init(context, attrs);
    }
    public CircleTickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.v("circleTick", "---CircleTickView---3");
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        try {
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CircleTickView);

            //整体
            mCircleRadius = attr.getInt(R.styleable.CircleTickView_circleRadius, 200);
            if(fixCircleRadius>0){
                mCircleRadius = fixCircleRadius;
            }
            mTickMaxCount = attr.getInt(R.styleable.CircleTickView_tickMaxCount, 31);
            touchPrecision = attr.getInt(R.styleable.CircleTickView_touchPrecision, 5);
            mRoundBackgroundColor = attr.getColor(R.styleable.CircleTickView_roundBackgroundColor, getResources().getColor(R.color.gray_c));
            //big
            mLineWidthBig = attr.getFloat(R.styleable.CircleTickView_lineWidthBig, 0.3f);
            mDefaultTickColorBig = attr.getColor(R.styleable.CircleTickView_defaultTickColorBig, getResources().getColor(R.color.purple_83));
            mTickStrokeSizeBig = attr.getDimensionPixelSize(R.styleable.CircleTickView_tickStrokeSizeBig, getDpValue(20));
            //small
            mLineWidthSmall = attr.getFloat(R.styleable.CircleTickView_lineWidthSmall, 0.3f);
            mDefaultTickColorSmall = attr.getColor(R.styleable.CircleTickView_defaultTickColorSmall, getResources().getColor(R.color.purple_82));
            mDefaultTickColorSmallDynamic = mDefaultTickColorSmall;
            mTickStrokeSizeSmall = attr.getDimensionPixelSize(R.styleable.CircleTickView_tickStrokeSizeSmall, getDpValue(20));
            //Triangle
            mDefaultTriangleColor = attr.getColor(R.styleable.CircleTickView_defaultTriangleColor, getResources().getColor(R.color.gray_c));
            //other
            mCenterTextColor = attr.getColor(R.styleable.CircleTickView_centerTextColor, getResources().getColor(R.color.purple_83));
            mCenterTextSize = attr.getDimension(R.styleable.CircleTickView_centerTextSize, getSpValue(50));
            mValue = attr.getInt(R.styleable.CircleTickView_value,1);
            mCenterText = mValue + "X";

            Log.v("circleTick", "init ###整体### 整体布局背景mRoundBackgroundColor=" + mRoundBackgroundColor);
            Log.v("circleTick", "init ###整体### 整体布局半径mCircleRadius=" + mCircleRadius);
            Log.v("circleTick", "init ###整体### 总的刻度数mTickMaxCount=" + mTickMaxCount);
            Log.v("circleTick", "init ###整体### 触摸精度touchPrecision=" + touchPrecision);
            Log.v("circleTick", "init ###big### 大刻度宽度mLineWidthBig=" + mLineWidthBig);
            Log.v("circleTick", "init ###big### 大刻度长度mTickStrokeSizeBig=" + mTickStrokeSizeBig);
            Log.v("circleTick", "init ###big### 大刻度颜色mDefaultTickColorBig=" + mDefaultTickColorBig);
            Log.v("circleTick", "init ###small### 小刻度宽度mLineWidthSmall=" + mLineWidthSmall);
            Log.v("circleTick", "init ###small### 小刻度长度mTickStrokeSizeSmall=" + mTickStrokeSizeSmall);
            Log.v("circleTick", "init ###small### 小刻度颜色mDefaultTickColorSmall=" + mDefaultTickColorSmall);
            Log.v("circleTick", "init ###other### 默认值mValue=" + mValue);
            Log.v("circleTick", "init ###other### 默认显示时间mCenterText=" + mCenterText);
            Log.v("circleTick", "init ###other### 中间文字大小mCenterTextSize=" + mCenterTextSize);

            initValue();
        }catch (Exception e){
            e.printStackTrace();
            Log.v("circleTick", "e="+e);
        }
    }
    private void initValue(){
        try {
            //======整体参数
            mWidth = mHeight = mCircleRadius << 1;
            mCircleCenterX = mCircleRadius;
            mCircleCenterY = mCircleRadius;
            Log.v("circleTick", "initValue mWidth=" + mWidth + ", mHeight=" + mHeight +
                    ", mCircleCenterX=" + mCircleCenterX + ", mCircleCenterY=" + mCircleCenterY +
                    ", mTickMaxCount=" + mTickMaxCount);
            //=====大圆环 长方形或矩形，圆形view在这里作为内切圆
            mCircleRingRadiusBig = mCircleRadius - mTickStrokeSizeBig / 2;
            touchRadius = mCircleRingRadiusBig + mTickStrokeSizeBig / 2;
            Log.v("circleTick", "initValue mCircleRingRadiusBig=" + mCircleRingRadiusBig + ", touchRadius=" + touchRadius);
            int bigLeft = mCircleCenterX - mCircleRingRadiusBig;
            int bigRight = mCircleCenterX + mCircleRingRadiusBig;
            int bigTop = mCircleCenterY - mCircleRingRadiusBig;
            int bigBottom = mCircleCenterY + mCircleRingRadiusBig;
            mRecfBig = new RectF();
            mRecfBig.set(bigLeft, bigTop, bigRight, bigBottom);
            //=====小圆环 长方形或矩形，圆形view在这里作为内切圆
            mCircleRingRadiusSmall = mCircleRadius - mTickStrokeSizeBig + mTickStrokeSizeSmall / 2;
            Log.v("circleTick", "initValue mCircleRingRadiusSmall=" + mCircleRingRadiusSmall);
            int smallLeft = mCircleCenterX - mCircleRingRadiusSmall;
            int smallRight = mCircleCenterX + mCircleRingRadiusSmall;
            int smallTop = mCircleCenterY - mCircleRingRadiusSmall;
            int smallBottom = mCircleCenterY + mCircleRingRadiusSmall;
            mRecfSmall = new RectF();
            mRecfSmall.set(smallLeft, smallTop, smallRight, smallBottom);
            //======大圆环画笔
            mCircleRingPaintBig = new Paint();
            mCircleRingPaintBig.setColor(mDefaultTickColorBig);
            mCircleRingPaintBig.setStyle(Paint.Style.FILL_AND_STROKE);   //Paint.Style.FILL :填充内部     Paint.Style.FILL_AND_STROKE ：填充内部和描边    Paint.Style.STROKE ：仅描边
            mCircleRingPaintBig.setStrokeWidth(mTickStrokeSizeBig);
            mCircleRingPaintBig.setAntiAlias(true);
            //======小圆环画笔
            mCircleRingPaintSmall = new Paint();
            mCircleRingPaintSmall.setColor(mDefaultTickColorSmallDynamic);
            mCircleRingPaintSmall.setStyle(Paint.Style.FILL_AND_STROKE);   //Paint.Style.FILL :填充内部     Paint.Style.FILL_AND_STROKE ：填充内部和描边    Paint.Style.STROKE ：仅描边
            mCircleRingPaintSmall.setStrokeWidth(mTickStrokeSizeSmall);
            mCircleRingPaintSmall.setAntiAlias(true);
            //=========Triangle
            mCircleTrianglePath = new Path();
            mCircleTrianglePaint = new Paint();
            mCircleTrianglePaint.setColor(mDefaultTriangleColor);
            mCircleTrianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mCircleTrianglePaint.setAntiAlias(true);
            //======中间文字画笔
            mCenterTextPaint = new Paint();
            mCenterTextPaint.setColor(mCenterTextColor);
            mCenterTextPaint.setTextAlign(Paint.Align.CENTER);
            mCenterTextPaint.setTextSize(mCenterTextSize);
            mCenterTextPaint.setAntiAlias(true);
            mCenterTextBounds = new Rect();
            mCenterTextPaint.getTextBounds(mCenterText != null ? mCenterText : "", 0, mCenterText.length(), mCenterTextBounds);

            initParam();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initParam(){
        try {
            angleList = new int[mTickMaxCount];
            mSinglPoint = (int) ((float) fixAngleRange / (float) (mTickMaxCount));  //角度平均分配
            if (mTickMaxCount > 20 && mTickMaxCount <= 30) {    //30X  mSinglPoint=5
                //每滑动一次，增加平均角度。所以滑动精度为５
                touchPrecision = 5;
                touchRangle = mSinglPoint;
            } else if (mTickMaxCount > 10 && mTickMaxCount <= 20) {   //18X  mSinglPoint=8
                touchPrecision = 5;
                touchRangle = mSinglPoint/2;
            } else if (mTickMaxCount > 5 && mTickMaxCount <= 10) {   //10X  mSinglPoint=16
                touchPrecision = 2;
                touchRangle = mSinglPoint/4;
            } else {   //5X  mSinglPoint=32
                touchPrecision = 2;
                touchRangle = mSinglPoint/8;
            }
            Log.v("circleTick", "initParam mSinglPoint=" + mSinglPoint + ", touchPrecision=" + touchPrecision +
                    ", touchRangle=" + touchRangle+", mTickMaxCount="+mTickMaxCount+", mValue="+mValue);
            updateValue(mValue);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateValue(int value){  //6
        if(value<=0 || value>mTickMaxCount) return;
        value = value - 1;
        try {
            int startAngle1 = 180;
            angleList[value] = startAngle1;
            for (int i = 0; i < value; i++) {  //0 1 2 3 4 5
                startAngle1 = startAngle1 - mSinglPoint;
                angleList[value - 1 - i] = startAngle1;
            }
            int startAngle2 = 180;
            for (int i = value + 1; i < mTickMaxCount; i++) {  //7 8 9
                startAngle2 = startAngle2 + mSinglPoint;
                angleList[i] = startAngle2;
            }

            for(int m=0;m<angleList.length;m++){
                Log.v("circleTick", "updateValue angleList=" + angleList[m]);
            }

            mCenterText = value + 1 + "X";
            mValue = value + 1;
            invalidate();
        }catch (Exception e){
            e.printStackTrace();
            Log.v("circleTick", "e="+e);
        }
    }
    public void updateParamForDebug(int maxValue){
        if(isDebug){
            if(maxValue<0) return;

            mTickMaxCount = maxValue;
            mValue = 1;
            mCenterText = mValue + "X";

            initParam();

            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v("circleTick", "---onMeasure---widthMeasureSpec="+widthMeasureSpec+", heightMeasureSpec="+heightMeasureSpec);
        measureMySelf(widthMeasureSpec,heightMeasureSpec);
    }
    private void measureMySelf(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //Log.v("circleTick","---measureMySelf--- width="+width+", widthMode="+widthMode+", height="+height+", heightMode="+heightMode);
        //Log.v("circleTick","---measureMySelf--- mWidth="+mWidth+", mHeight="+mHeight);
        setMeasuredDimension(mWidth,mHeight);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.v("circleTick", "---onLayout--- changed=" + changed+", l="+l+", t="+t+", r="+r+", b="+b);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v("circleTick", "---onDraw---");
        try {
            canvas.drawColor(mRoundBackgroundColor);

            //==========绘制小刻度
            mDefaultTickColorSmallDynamic = mDefaultTickColorSmall;
            mCircleRingPaintSmall.setColor(mDefaultTickColorSmallDynamic);
            for (int i = 0; i < mTickMaxCount; i++) {
                if(mTickMaxCount==30){
                    if( i%5==0 && i!=0 ){
                        mDefaultTickColorSmallDynamic = mDefaultTickColorBig;
                        mCircleRingPaintSmall.setColor(mDefaultTickColorSmallDynamic);
                        canvas.drawArc(mRecfSmall, angleList[i], mLineWidthSmall, false, mCircleRingPaintSmall); // 绘制间隔块
                    }else{
                        mDefaultTickColorSmallDynamic = mDefaultTickColorSmall;
                        mCircleRingPaintSmall.setColor(mDefaultTickColorSmallDynamic);
                        canvas.drawArc(mRecfSmall, angleList[i], mLineWidthSmall, false, mCircleRingPaintSmall); // 绘制间隔块
                    }
                }else{
                    canvas.drawArc(mRecfSmall, angleList[i], mLineWidthSmall, false, mCircleRingPaintSmall); // 绘制间隔块
                }
            }

            //===========固定大刻度
            mCircleRingPaintBig.setColor(mDefaultTickColorBig);
            float start = 180f;
            canvas.drawArc(mRecfBig, start, mLineWidthBig, false, mCircleRingPaintBig); // 绘制间隔块

            //===========固定Triangle
            int[] point = getTriangleFirstPoint();
            //Path path = new Path();
            mCircleTrianglePath.moveTo(point[0], point[1]);// 此点为多边形的起点
            mCircleTrianglePath.lineTo(point[0]+15, point[1]-10);
            mCircleTrianglePath.lineTo(point[0]+15, point[1]+10);
            mCircleTrianglePath.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(mCircleTrianglePath, mCircleTrianglePaint);

            //===============绘制字体
            //Paint.FontMetricsInt fontMetrics = mCenterTextPaint.getFontMetricsInt();
            int fontWidth = mCenterTextBounds.width();
            int fontHeight = mCenterTextBounds.height();
            int fontLeft = mCircleCenterX - (mCircleRingRadiusSmall - mTickStrokeSizeSmall / 2) / 2;
            int fontTop = mCircleCenterY + (int) (fontHeight / 2);
            //int baseline = (mHeight - getPaddingTop() / 2 - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(mCenterText, fontLeft, fontTop, mCenterTextPaint);
        }catch (Exception e){
            e.printStackTrace();
            Log.v("circleTick", "e="+e);
        }
    }

    //---------------touch--------------------------------------------------------------------------
    private int angleTrack = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        try {
            int distance = getTouchDistance((int) x, (int) y);
            //Log.v("circleTick", "---onTouchEvent--- x=" + x + ", y=" + y + ", distance=" + distance);
            if (distance > touchRadius) {
                //Log.v("circleTick", "---onTouchEvent--- 在圆外了");
                //获取接近值索引
                int index = getApproximate(180, angleList);
                //校准刻度
                updateValue(index+1);
                if (mOnTimeChangeListener != null) {
                    mOnTimeChangeListener.onChange(mValue);
                }
                return false;
            }
            int angle = getTouchAngle((int) x, (int) y);
            //Log.v("circleTick", "---onTouchEvent--- angle=" + angle);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //judgeQuadrantAndSetCurrentProgress(x, y, angle, true);
                    angleTrack = angle;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //judgeQuadrantAndSetCurrentProgress(x, y, angle, false);
                    if (angleTrack != angle) {
                        if (angle > angleTrack + touchPrecision) {
                            angleTrack = angle;
                            //检查一次
                            if (angleList[0] >= 180) {
                                int startAngle = 180;
                                for (int i = 0; i < mTickMaxCount; i++) {
                                    angleList[i] = startAngle + i * mSinglPoint;
                                }
                            } else {
                                for (int i = 0; i < mTickMaxCount; i++) {
                                    angleList[i] = angleList[i] + touchRangle;
                                }
                                //再次检查一次
                                if (angleList[0] >= 180) {
                                    int startAngle = 180;
                                    for (int i = 0; i < mTickMaxCount; i++) {
                                        angleList[i] = startAngle + i * mSinglPoint;
                                    }
                                }
                            }
                            //获取接近值索引
                            int index = getApproximate(180, angleList);
                            mCenterText = index + 1 + "X";
                            invalidate();
                        } else if (angle < angleTrack - touchPrecision) {
                            angleTrack = angle;
                            //检查一次
                            if (angleList[mTickMaxCount - 1] <= 180) {
                                int endAngle = 180;
                                for (int i = 0; i < mTickMaxCount; i++) {
                                    angleList[mTickMaxCount - 1 - i] = endAngle - i * mSinglPoint;
                                }
                            } else {
                                for (int i = 0; i < mTickMaxCount; i++) {
                                    angleList[i] = angleList[i] - touchRangle;
                                }
                                //再次检查一次
                                if (angleList[mTickMaxCount - 1] <= 180) {
                                    int endAngle = 180;
                                    for (int i = 0; i < mTickMaxCount; i++) {
                                        angleList[mTickMaxCount - 1 - i] = endAngle - i * mSinglPoint;
                                    }
                                }
                            }
                            //获取接近值索引
                            int index = getApproximate(180, angleList);
                            mCenterText = index + 1 + "X";
                            invalidate();
                        }
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    //获取接近值索引
                    int index = getApproximate(180, angleList);
                    //校准刻度
                    updateValue(index+1);
                    if (mOnTimeChangeListener != null) {
                        mOnTimeChangeListener.onChange(mValue);
                    }
                    return true;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.v("circleTick", "e="+e);
        }
        return super.onTouchEvent(event);
    }

    //--------------------------------监听----------------------------------------------------------
    public interface OnTimeChangeListener {
        void onChange(int value);
    }
    private OnTimeChangeListener mOnTimeChangeListener;
    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        mOnTimeChangeListener = onTimeChangeListener;
    }

    //---------------------------------other--------------------------------------------------------
    private int getDpValue(int w) throws Exception{
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getContext()
                .getResources().getDisplayMetrics());
    }
    private int getSpValue(int w) throws Exception{
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, w, getContext()
                .getResources().getDisplayMetrics());
    }
    /**
     * 求触摸位置与圆心的举例
     */
    private int getTouchDistance(int touchX,int touchY) throws Exception{
        //点击位置x坐标与圆心的x坐标的距离
        int distanceX = Math.abs(touchX-mCircleCenterX);
        //点击位置y坐标与圆心的y坐标的距离
        int distanceY = Math.abs(touchY-mCircleCenterY);
        //点击位置与圆心的直线距离
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX,2)+Math.pow(distanceY,2));

        return distanceZ;
    }
    /**
     * 计算触摸角度
     */
    private int getTouchAngle(int touchX,int touchY) throws Exception{
        int xx = mCircleCenterX,yy = mCircleCenterY; //圆心坐标
        int x1 = touchX,y1 = touchY; //坐标点
        double distanceX1 = Math.abs(x1-xx); //x坐标与圆心的x坐标的距离
        double distanceY1 = Math.abs(y1-yy); //y坐标与圆心的y坐标的距离
        double distanceZ1 = Math.sqrt(Math.pow(distanceX1,2)+Math.pow(distanceY1,2)); //坐标点与圆心的直线距离
        double cos1 = (x1-xx) / distanceZ1; //得到cos值
        double radian1=Math.acos(cos1); //得到cos对应的弧度
        //double angle1 = v1/2/Math.PI*360; //方式１：弧度转角度
        double angle1 = radian1 * 180 / Math.PI; //方式2：弧度转角度
        if ((y1-yy) < 0) {
            angle1 = 360 - angle1;
        }

        return (int) angle1;
    }
    /**
     * 获取接近值的索引
     *
     * @param x
     * @param src
     * @return
     */
    private int getApproximate(int x, int[] src) throws Exception{
        if (src == null) {
            return -1;
        }
        if (src.length == 1) {
            return 0;
            //return src[0];
        }
        int index = -1;
        for (int i = 0; i < src.length; i++) {
            if (src[i] > x) {
                index = i;
                break;
            } else if (src[i] == x) {
                return i;
                //return x;
            }
        }
        if (index == -1) {
            return src.length - 1;
            //return src[src.length - 1];
        } else if (index == 0) {
            return 0;
            //return src[0];
        } else {
            return x - src[index - 1] < src[index] - x ? (index - 1) : index;
            //return x - src[index - 1] < src[index] - x ? src[index - 1] : src[index];
        }
    }
    /**
     * 获取Triangle顶点坐标
     */
    private int[] getTriangleFirstPoint() throws Exception{
        int[] point = new int[2];

        int x1 = mCircleRingRadiusSmall - mTickStrokeSizeSmall / 2;
        int x = mCircleCenterX - (x1-10);
        int y = mCircleCenterY - 4;
        point[0] = x;
        point[1] = y;

        return point;
    }

}
