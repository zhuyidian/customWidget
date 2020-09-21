package com.tc.lock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CircleLock extends View {
    //===============================================整体
    private static int fixAngleRange = 360;  //角度范围必须在使用前指定
    private static int fixCircleRadius = 0;  //控件半径为<=0，那么使用布局半径，一般使用时肯定会设置半径
    private int mRoundBackgroundColor;  //背景颜色
    private int mCircleRadius;  //布局整体半径
    private int mHeight;  //布局整体高
    private int mWidth;  //布局整体宽
    private int mCircleCenterX;  //布局整体圆心的x坐标
    private int mCircleCenterY;  //布局整体圆心坐标y
    private float mLineWidthNormal = 3.0f;
    private RectF mRecfNormal;
    private Paint mPaintNormal;
    //=========================================circle1
    private int mTickMaxCount; //刻度的总数
    private int mSinglPoint; //角度，单个间隔块所占的角度
    private int[] angleList;
    private int mValueCircle1 = 1;
    //*******touch
    private int touchRadius;  //触摸半径
    private int touchPrecision;  //触摸精度
    private int touchRangle;  //动态调整角度
    private Paint mTouchPaint;  //touch 背景
    //*******big
    private int mCircleRingRadiusBig;  //大圆环内的半径
    private float mLineWidthBig = 0.3f;  //大刻度宽度
    private int mTickStrokeSizeBig;  //大刻度长度
    private int mDefaultTickColorBig;  //默认大刻度的颜色
    private RectF mRecfBig;
    private Paint mCircleRingPaintBig;  //大刻度的paint
    //*******small
    private float mLineWidthSmall = 0.3f;   //小刻度宽度
    private int mCircleRingRadiusSmall;  //小刻度的半径
    private int mTickStrokeSizeSmall; //小刻度长度
    private int mDefaultTickColorSmall;  //默认小刻度的颜色
    private RectF mRecfSmall;
    private Paint mCircleRingPaintSmall;  //小刻度的paint
    private Paint mCircleTrianglePaint;  //小圆环内的半径
    private Path mCircleTrianglePath;
    private int mDefaultTriangleColor;  //默认小刻度的颜色
    private int mCenterTextColor;
    private float mCenterTextSize;
    private Paint mCenterTextPaint;
    private Rect mCenterTextBounds;
    //=========================================circle2
    private int mValueCircle2 = 1;
    //圆环
    private int mCircle2_Radius;  //圆环半径
    private int mCircle2_Width;  //圆环宽度
    private Paint mCircle2_Paint;  //圆环的paint
    //圆环边
    private int mCircle2_RadiusL;  //圆环边半径
    private Paint mCircle2_PaintL;  //圆环边的paint
    //刻度
    private int mCircle2_TickMaxCount;
    private int mCircle2_SinglPoint;
    private int[] mCircle2_angleList;
    private float mCircle2_Width_Big = 1.0f;
    private float mCircle2_Width_Small = 0.5f;
    private RectF mCircle2_Recf_Big;
    private RectF mCircle2_Recf_Small;
    private Paint mCircle2_Paint_Big;
    private Paint mCircle2_Paint_Small;
    //=========================================circle3
    private int mValueCircle3 = 1;
    //圆环
    private int mCircle3_Radius;  //圆环半径
    private int mCircle3_Width;  //圆环宽度
    private Paint mCircle3_Paint;  //圆环的paint
    //圆环边
    private int mCircle3_RadiusL;  //圆环边半径
    private Paint mCircle3_PaintL;  //圆环边的paint
    //刻度
    private int mCircle3_TickMaxCount;
    private int mCircle3_SinglPoint;
    private int[] mCircle3_angleList;
    private float mCircle3_Width_Big = 0.5f;
    private float mCircle3_Width_Small = 0.5f;
    private RectF mCircle3_Recf_Big;
    private RectF mCircle3_Recf_Small;
    private Paint mCircle3_Paint_Big;
    private Paint mCircle3_Paint_Small;

    public CircleLock(Context context) {
        this(context, null);
        init(context, null);
    }
    public CircleLock(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setWillNotDraw(false);  //更换成ＶiewGroup后不执行OnDraw
        init(context, attrs);
    }
    public CircleLock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
            mRoundBackgroundColor = attr.getColor(R.styleable.CircleTickView_roundBackgroundColor, getResources().getColor(R.color.gray_c));
            //big
            mLineWidthBig = attr.getFloat(R.styleable.CircleTickView_lineWidthBig, 0.3f);
            mDefaultTickColorBig = attr.getColor(R.styleable.CircleTickView_defaultTickColorBig, getResources().getColor(R.color.purple_83));
            mTickStrokeSizeBig = attr.getDimensionPixelSize(R.styleable.CircleTickView_tickStrokeSizeBig, getDpValue(20));
            //small
            mLineWidthSmall = attr.getFloat(R.styleable.CircleTickView_lineWidthSmall, 0.3f);
            mDefaultTickColorSmall = attr.getColor(R.styleable.CircleTickView_defaultTickColorSmall, getResources().getColor(R.color.purple_82));
            mTickStrokeSizeSmall = attr.getDimensionPixelSize(R.styleable.CircleTickView_tickStrokeSizeSmall, getDpValue(20));
            //Triangle
            mDefaultTriangleColor = attr.getColor(R.styleable.CircleTickView_defaultTriangleColor, getResources().getColor(R.color.gray_c));
            //other
            mCenterTextColor = attr.getColor(R.styleable.CircleTickView_centerTextColor, getResources().getColor(R.color.purple_83));
            mCenterTextSize = attr.getDimension(R.styleable.CircleTickView_centerTextSize, getSpValue(50));
            mValueCircle1 = attr.getInt(R.styleable.CircleTickView_value,1);
            mValueCircle1 = mValueCircle2 = mValueCircle3 = 6;

            initValue();
        }catch (Exception e){
            e.printStackTrace();
            Log.v("lock", "e="+e);
        }
    }
    private void initValue(){
        try {
            //===========================整体参数
            mWidth = mHeight = mCircleRadius << 1;
            mCircleCenterX = mCircleRadius;
            mCircleCenterY = mCircleRadius;
            mPaintNormal = new Paint();
            mPaintNormal.setColor(Color.parseColor("#90ffffff"));
            mPaintNormal.setStyle(Paint.Style.FILL_AND_STROKE);   //Paint.Style.FILL :填充内部     Paint.Style.FILL_AND_STROKE ：填充内部和描边    Paint.Style.STROKE ：仅描边
            mPaintNormal.setStrokeWidth(mCircleRadius);
            mPaintNormal.setAntiAlias(true);
            int normalRadius = mCircleRadius/2;
            int NormalLeft = mCircleCenterX - normalRadius;
            int NormalRight = mCircleCenterX + normalRadius;
            int NormalTop = mCircleCenterY - normalRadius;
            int NormalBottom = mCircleCenterY + normalRadius;
            mRecfNormal = new RectF();
            mRecfNormal.set(NormalLeft, NormalTop, NormalRight, NormalBottom);
            touchPrecision = 0;
            touchRangle = 1;

            //===========================circle1
            mTickStrokeSizeBig = 50;
            mTickStrokeSizeSmall = 30;
            //rect big
            mCircleRingRadiusBig = mCircleRadius*1/3 - mTickStrokeSizeBig / 2;
            int bigLeft = mCircleCenterX - mCircleRingRadiusBig;
            int bigRight = mCircleCenterX + mCircleRingRadiusBig;
            int bigTop = mCircleCenterY - mCircleRingRadiusBig;
            int bigBottom = mCircleCenterY + mCircleRingRadiusBig;
            mRecfBig = new RectF();
            mRecfBig.set(bigLeft, bigTop, bigRight, bigBottom);
            //rect small
            mCircleRingRadiusSmall = mCircleRadius*1/3 - mTickStrokeSizeSmall / 2;
            int smallLeft = mCircleCenterX - mCircleRingRadiusSmall;
            int smallRight = mCircleCenterX + mCircleRingRadiusSmall;
            int smallTop = mCircleCenterY - mCircleRingRadiusSmall;
            int smallBottom = mCircleCenterY + mCircleRingRadiusSmall;
            mRecfSmall = new RectF();
            mRecfSmall.set(smallLeft, smallTop, smallRight, smallBottom);
            touchRadius = mCircleRingRadiusBig + mTickStrokeSizeBig / 2;
            mTouchPaint = new Paint();
            mTouchPaint.setColor(Color.parseColor("#d8d8d8"));
            mTouchPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mTouchPaint.setAntiAlias(true);
//            if (mTickMaxCount > 30 && mTickMaxCount <= 100) {    //90X  mSinglPoint=4
//                touchPrecision = 4;
//                touchRangle = mSinglPoint;
//            } else if (mTickMaxCount > 20 && mTickMaxCount <= 30) {    //30X  mSinglPoint=5
//                //每滑动一次，增加平均角度。所以滑动精度为５
//                touchPrecision = 5;
//                touchRangle = mSinglPoint;
//            } else if (mTickMaxCount > 10 && mTickMaxCount <= 20) {   //18X  mSinglPoint=8
//                touchPrecision = 5;
//                touchRangle = mSinglPoint/2;
//            } else if (mTickMaxCount > 5 && mTickMaxCount <= 10) {   //10X  mSinglPoint=16
//                touchPrecision = 2;
//                touchRangle = mSinglPoint/4;
//            } else {   //5X  mSinglPoint=32
//                touchPrecision = 2;
//                touchRangle = mSinglPoint/8;
//            }
            //paint big
            mCircleRingPaintBig = new Paint();
            mCircleRingPaintBig.setColor(mDefaultTickColorBig);
            mCircleRingPaintBig.setStyle(Paint.Style.FILL_AND_STROKE);
            mCircleRingPaintBig.setStrokeWidth(mTickStrokeSizeBig);
            mCircleRingPaintBig.setAntiAlias(true);
            //paint small
            mCircleRingPaintSmall = new Paint();
            mCircleRingPaintSmall.setColor(Color.parseColor("#626262"));
            mCircleRingPaintSmall.setStyle(Paint.Style.FILL_AND_STROKE);
            mCircleRingPaintSmall.setStrokeWidth(mTickStrokeSizeSmall);
            mCircleRingPaintSmall.setAntiAlias(true);
            //Triangle
            mCircleTrianglePath = new Path();
            mCircleTrianglePaint = new Paint();
            mCircleTrianglePaint.setColor(mDefaultTriangleColor);
            mCircleTrianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mCircleTrianglePaint.setAntiAlias(true);
            //中间文字画笔
            mCenterTextPaint = new Paint();
            mCenterTextPaint.setColor(mCenterTextColor);
            mCenterTextPaint.setTextAlign(Paint.Align.CENTER);
            mCenterTextPaint.setTextSize(mCenterTextSize);
            mCenterTextPaint.setAntiAlias(true);
            mCenterTextBounds = new Rect();
            //mCenterTextPaint.getTextBounds(mCenterText != null ? mCenterText : "", 0, mCenterText.length(), mCenterTextBounds);
            //刻度
            mTickMaxCount = 30;
            angleList = new int[mTickMaxCount];
            mSinglPoint = (int) ((float) 360 / (float) (mTickMaxCount));
//            int startAngle1 = 0;
//            for(int i = 0; i < mTickMaxCount; i++){
//                angleList[i] = startAngle1;
//                startAngle1 = startAngle1 + mSinglPoint;
//            }
//            //debug start
//            for(int m=0;m<angleList.length;m++){
//                Log.v("lock", "debug：initValue circle1 angleList=" + angleList[m]);
//            }
//            int index = getApproximate(180, angleList);
//            Log.v("lock", "debug：initValue circle1 获取180附近的值=" + index);
//            //debug end


            //===========================circle2
            //圆环
            mCircle2_Radius = touchRadius + 120;
            mCircle2_Width = 90;
            mCircle2_Paint = new Paint();
            mCircle2_Paint.setColor(Color.parseColor("#615532"));
            mCircle2_Paint.setStyle(Paint.Style.STROKE);
            mCircle2_Paint.setStrokeWidth(mCircle2_Width);
            mCircle2_Paint.setAntiAlias(true);
            //圆环边
            mCircle2_RadiusL = mCircle2_Radius+mCircle2_Width/2;
            mCircle2_PaintL = new Paint();
            mCircle2_PaintL.setColor(Color.parseColor("#ccb16d"));
            mCircle2_PaintL.setStyle(Paint.Style.STROKE);
            mCircle2_PaintL.setStrokeWidth(2);
            mCircle2_PaintL.setAntiAlias(true);
            //paint big
            int width_big2 = mCircle2_Width-40;
            mCircle2_Paint_Big = new Paint();
            mCircle2_Paint_Big.setColor(Color.parseColor("#ccb16d"));
            mCircle2_Paint_Big.setStyle(Paint.Style.FILL_AND_STROKE);
            mCircle2_Paint_Big.setStrokeWidth(width_big2);
            mCircle2_Paint_Big.setAntiAlias(true);
            //paint small
            int width_small2 = (mCircle2_Width-20)/2;
            mCircle2_Paint_Small = new Paint();
            mCircle2_Paint_Small.setColor(Color.parseColor("#000000"));
            mCircle2_Paint_Small.setStyle(Paint.Style.FILL_AND_STROKE);
            mCircle2_Paint_Small.setStrokeWidth(width_small2);
            mCircle2_Paint_Small.setAntiAlias(true);
            //rect big
            int raduis_big2 = mCircle2_Radius;
            int bigLeft2 = mCircleCenterX - raduis_big2;
            int bigRight2 = mCircleCenterX + raduis_big2;
            int bigTop2 = mCircleCenterY - raduis_big2;
            int bigBottom2 = mCircleCenterY + raduis_big2;
            mCircle2_Recf_Big = new RectF();
            mCircle2_Recf_Big.set(bigLeft2, bigTop2, bigRight2, bigBottom2);
            //rect small
            int raduis_small2 = (raduis_big2+width_big2/2) - width_small2/2;
            int smallLeft2 = mCircleCenterX - raduis_small2;
            int smallRight2 = mCircleCenterX + raduis_small2;
            int smallTop2 = mCircleCenterY - raduis_small2;
            int smallBottom2 = mCircleCenterY + raduis_small2;
            mCircle2_Recf_Small = new RectF();
            mCircle2_Recf_Small.set(smallLeft2, smallTop2, smallRight2, smallBottom2);
            //刻度
            mCircle2_TickMaxCount = 30;
            mCircle2_angleList = new int[mCircle2_TickMaxCount];
            mCircle2_SinglPoint = (int) ((float) 360 / (float) (mCircle2_TickMaxCount));
//            int startAngle2 = 0;
//            for(int i = 0; i < mCircle2_TickMaxCount; i++){
//                mCircle2_angleList[i] = startAngle2;
//                startAngle2 = startAngle2 + mCircle2_SinglPoint;
//            }

            //===========================circle3
            //圆环
            mCircle3_Radius = mCircle2_Radius + 150;
            mCircle3_Width = 90;
            mCircle3_Paint = new Paint();
            mCircle3_Paint.setColor(Color.parseColor("#242738"));
            mCircle3_Paint.setStyle(Paint.Style.STROKE);
            mCircle3_Paint.setStrokeWidth(mCircle2_Width);
            mCircle3_Paint.setAntiAlias(true);
            //圆环边
            mCircle3_RadiusL = mCircle3_Radius+mCircle3_Width/2;
            mCircle3_PaintL = new Paint();
            mCircle3_PaintL.setColor(Color.parseColor("#6c78c1"));
            mCircle3_PaintL.setStyle(Paint.Style.STROKE);
            mCircle3_PaintL.setStrokeWidth(2);
            mCircle3_PaintL.setAntiAlias(true);
            //paint big
            int width_big3 = mCircle3_Width-40;
            mCircle3_Paint_Big = new Paint();
            mCircle3_Paint_Big.setColor(Color.parseColor("#b0baf5"));
            mCircle3_Paint_Big.setStyle(Paint.Style.FILL_AND_STROKE);
            mCircle3_Paint_Big.setStrokeWidth(width_big3);
            mCircle3_Paint_Big.setAntiAlias(true);
            //paint small
            int width_small3 = (mCircle3_Width-20)/2;
            mCircle3_Paint_Small = new Paint();
            mCircle3_Paint_Small.setColor(Color.parseColor("#6c78c1"));
            mCircle3_Paint_Small.setStyle(Paint.Style.FILL_AND_STROKE);
            mCircle3_Paint_Small.setStrokeWidth(width_small3);
            mCircle3_Paint_Small.setAntiAlias(true);
            //rect big
            int raduis_big3 = mCircle3_Radius;
            int bigLeft3 = mCircleCenterX - raduis_big3;
            int bigRight3 = mCircleCenterX + raduis_big3;
            int bigTop3 = mCircleCenterY - raduis_big3;
            int bigBottom3 = mCircleCenterY + raduis_big3;
            mCircle3_Recf_Big = new RectF();
            mCircle3_Recf_Big.set(bigLeft3, bigTop3, bigRight3, bigBottom3);
            //rect small
            int raduis_small3 = (raduis_big3+width_big3/2) - width_small3/2;
            int smallLeft3 = mCircleCenterX - raduis_small3;
            int smallRight3 = mCircleCenterX + raduis_small3;
            int smallTop3 = mCircleCenterY - raduis_small3;
            int smallBottom3 = mCircleCenterY + raduis_small3;
            mCircle3_Recf_Small = new RectF();
            mCircle3_Recf_Small.set(smallLeft3, smallTop3, smallRight3, smallBottom3);
            //刻度
            mCircle3_TickMaxCount = 60;
            mCircle3_angleList = new int[mCircle3_TickMaxCount];
            mCircle3_SinglPoint = (int) ((float) 360 / (float) (mCircle3_TickMaxCount));
//            int startAngle3 = 0;
//            for(int i = 0; i < mCircle3_TickMaxCount; i++){
//                mCircle3_angleList[i] = startAngle3;
//                startAngle3 = startAngle3 + mCircle3_SinglPoint;
//            }

            calibrationValue1(mValueCircle1);
            calibrationValue2(mValueCircle2);
            calibrationValue3(mValueCircle3);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void calibrationValue1(int value){
        if(value<0 || value>=mTickMaxCount) return;
        try {
            int startAngle1 = 180;
            angleList[value] = startAngle1;
            for (int i = 0; i < value; i++) {
                startAngle1 = startAngle1 - mSinglPoint;
                angleList[value - 1 - i] = startAngle1;
            }
            int startAngle2 = 180;
            for (int i = value + 1; i < mTickMaxCount; i++) {
                startAngle2 = startAngle2 + mSinglPoint;
                angleList[i] = startAngle2;
            }

//            for(int m=0;m<angleList.length;m++){
//                Log.v("lock", "updateValue angleList=" + angleList[m]);
//            }

            mValueCircle1 = value;
            invalidate();
        }catch (Exception e){
            e.printStackTrace();
            Log.v("lock", "e="+e);
        }
    }
    public void calibrationValue2(int value){
        if(value<0 || value>=mCircle2_TickMaxCount) return;
        try {
            int startAngle1 = 180;
            mCircle2_angleList[value] = startAngle1;
            for (int i = 0; i < value; i++) {
                startAngle1 = startAngle1 - mCircle2_SinglPoint;
                mCircle2_angleList[value - 1 - i] = startAngle1;
            }
            int startAngle2 = 180;
            for (int i = value + 1; i < mCircle2_TickMaxCount; i++) {
                startAngle2 = startAngle2 + mCircle2_SinglPoint;
                mCircle2_angleList[i] = startAngle2;
            }

//            for(int m=0;m<angleList.length;m++){
//                Log.v("lock", "updateValue angleList=" + angleList[m]);
//            }

            mValueCircle2 = value;
            invalidate();
        }catch (Exception e){
            e.printStackTrace();
            Log.v("lock", "e="+e);
        }
    }
    public void calibrationValue3(int value){
        if(value<0 || value>=mCircle3_TickMaxCount) return;
        try {
            int startAngle1 = 180;
            mCircle3_angleList[value] = startAngle1;
            for (int i = 0; i < value; i++) {
                startAngle1 = startAngle1 - mCircle3_SinglPoint;
                mCircle3_angleList[value - 1 - i] = startAngle1;
            }
            int startAngle2 = 180;
            for (int i = value + 1; i < mCircle3_TickMaxCount; i++) {
                startAngle2 = startAngle2 + mCircle3_SinglPoint;
                mCircle3_angleList[i] = startAngle2;
            }

//            for(int m=0;m<angleList.length;m++){
//                Log.v("lock", "updateValue angleList=" + angleList[m]);
//            }

            mValueCircle3 = value;
            invalidate();
        }catch (Exception e){
            e.printStackTrace();
            Log.v("lock", "e="+e);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureMySelf(widthMeasureSpec,heightMeasureSpec);
    }
    private void measureMySelf(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.v("lock", "---onLayout--- changed=" + changed+", l="+l+", t="+t+", r="+r+", b="+b);
    }
    private volatile boolean isDraw = false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.v("lock", "---onDraw---");
        canvas.save();
        isDraw = true;
        try {
            canvas.drawColor(mRoundBackgroundColor);
            //canvas.drawCircle(mCircleCenterX,mCircleCenterY,touchRadius,mTouchPaint);

            //circle3
            canvas.drawCircle(mCircleCenterX,mCircleCenterY,mCircle3_Radius,mCircle3_Paint);
            canvas.drawCircle(mCircleCenterX,mCircleCenterY,mCircle3_RadiusL,mCircle3_PaintL);
            for (int i = 0; i < mCircle3_TickMaxCount; i++) {
                if( i%2==0){
                    canvas.drawArc(mCircle3_Recf_Big, mCircle3_angleList[i], mCircle3_Width_Big, false, mCircle3_Paint_Big);
                }else{
                    canvas.drawArc(mCircle3_Recf_Small, mCircle3_angleList[i], mCircle3_Width_Small, false, mCircle3_Paint_Small);
                }
            }
            //circle2
            canvas.drawCircle(mCircleCenterX,mCircleCenterY,mCircle2_Radius,mCircle2_Paint);
            canvas.drawCircle(mCircleCenterX,mCircleCenterY,mCircle2_RadiusL,mCircle2_PaintL);
            for (int i = 0; i < mCircle2_TickMaxCount; i++) {
                if( i%2==0){
                    canvas.drawArc(mCircle2_Recf_Big, mCircle2_angleList[i], mCircle2_Width_Big, false, mCircle2_Paint_Big);
                }else{
                    canvas.drawArc(mCircle2_Recf_Small, mCircle2_angleList[i], mCircle2_Width_Small, false, mCircle2_Paint_Small);
                }
            }
            //circle1
            for (int i = 0; i < mTickMaxCount; i++) {
                if( i%2==0){
                    canvas.drawArc(mRecfBig, angleList[i], mLineWidthBig, false, mCircleRingPaintBig); // 绘制间隔块
                }else{
                    canvas.drawArc(mRecfSmall, angleList[i], mLineWidthSmall, false, mCircleRingPaintSmall); // 绘制间隔块
                }
            }
            //normal
            float start = 179f;
            canvas.drawArc(mRecfNormal, start, mLineWidthNormal, false, mPaintNormal);

            //===========固定Triangle
//            int[] point = getTriangleFirstPoint();
//            //Path path = new Path();
//            mCircleTrianglePath.moveTo(point[0], point[1]);// 此点为多边形的起点
//            mCircleTrianglePath.lineTo(point[0]+15, point[1]-10);
//            mCircleTrianglePath.lineTo(point[0]+15, point[1]+10);
//            mCircleTrianglePath.close(); // 使这些点构成封闭的多边形
//            canvas.drawPath(mCircleTrianglePath, mCircleTrianglePaint);

            //===============绘制字体
//            int fontWidth = mCenterTextBounds.width();
//            int fontHeight = mCenterTextBounds.height();
//            int fontLeft = mCircleCenterX - (mCircleRingRadiusSmall - mTickStrokeSizeSmall / 2) / 2;
//            int fontTop = mCircleCenterY + (int) (fontHeight / 2);
//            canvas.drawText(mCenterText, fontLeft, fontTop, mCenterTextPaint);
        }catch (Exception e){
            e.printStackTrace();
            Log.v("lock", "e="+e);
        }

        isDraw = false;
        canvas.restore();
    }

    //---------------touch--------------------------------------------------------------------------
    private boolean touchCircle1=false,touchCircle2=false,touchCircle3=false;
    private int angleTrack1 = 0;
    private int angleTrack2 = 0;
    private int angleTrack3 = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        try {
            int distance = getTouchDistance((int) x, (int) y);
            int angle = getTouchAngle((int) x, (int) y);
//            if (distance > touchRadius) {  //circle1
//                //获取接近值索引
//                int index = getApproximate(180, angleList);
//                Log.v("lock", "touch out index=" + index);
//                //校准刻度
//                updateValue(index+1);
//                if (mOnTimeChangeListener != null) {
//                    mOnTimeChangeListener.onChange(mValue);
//                }
//                return false;
//            }

            if(distance<=touchRadius){  //circle1
                touchCircle1 = true;touchCircle2=false;touchCircle3=false;
                angleTrack2 = -1;
                angleTrack3 = -1;
//                //校准circle2
//                int index2 = getApproximate(180, mCircle2_angleList);
//                Log.v("lock", "touch ---2---out index2=" + index2);
//                updateValue(index2);
//                //校准circle3
//                int index3 = getApproximate(180, mCircle3_angleList);
//                Log.v("lock", "touch ---3---out index3=" + index3);
//                updateValue(index3);
            }else if(distance>touchRadius && distance<=mCircle2_RadiusL){  //circle2
                touchCircle1 = false;touchCircle2=true;touchCircle3=false;
                angleTrack1 = -1;
                angleTrack3 = -1;
//                //校准circle1
//                int index1 = getApproximate(180, angleList);
//                Log.v("lock", "touch ---1---out index1=" + index1);
//                updateValue(index1);
//                //校准circle3
//                int index3 = getApproximate(180, mCircle3_angleList);
//                Log.v("lock", "touch ---3---out index3=" + index3);
//                updateValue(index3);
            }else if(distance>mCircle2_RadiusL && distance<=mCircle3_RadiusL){  //circle3
                touchCircle1 = false;touchCircle2=false;touchCircle3=true;
                angleTrack1 = -1;
                angleTrack2 = -1;
//                //校准circle1
//                int index1 = getApproximate(180, angleList);
//                Log.v("lock", "touch ---1---out index1=" + index1);
//                updateValue(index1);
//                //校准circle2
//                int index2 = getApproximate(180, mCircle2_angleList);
//                Log.v("lock", "touch ---2---out index2=" + index2);
//                updateValue(index2);
            }else{   //touch out
                touchCircle1 = false;touchCircle2=false;touchCircle3=false;
                angleTrack1 = -1;
                angleTrack2 = -1;
                angleTrack3 = -1;
                //校准circle1
                int index1 = getApproximate(180, angleList);
                //Log.v("lock", "touch ---1---out index1=" + index1);
                calibrationValue1(index1);
                //校准circle2
                int index2 = getApproximate(180, mCircle2_angleList);
                //Log.v("lock", "touch ---2---out index2=" + index2);
                calibrationValue2(index2);
                //校准circle3
                int index3 = getApproximate(180, mCircle3_angleList);
                //Log.v("lock", "touch ---3---out index3=" + index3);
                calibrationValue3(index3);
                if (mOnTimeChangeListener != null) {
                    mOnTimeChangeListener.onChange(index1,index2,index3);
                }
            }


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //judgeQuadrantAndSetCurrentProgress(x, y, angle, true);
                    //angleTrack = angle;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //judgeQuadrantAndSetCurrentProgress(x, y, angle, false);
                    if(touchCircle1==true){  //circle1
                        if (angleTrack1 != angle) {
                            if (angle > angleTrack1 + touchPrecision) {
                                angleTrack1 = angle;
                                for (int i = 0; i < mTickMaxCount; i++) {
                                    angleList[i] = angleList[i] + touchRangle;
                                    if(angleList[i]>360){
                                        angleList[i] = angleList[i]-360;
                                    }
                                }
                                //获取接近值索引
                                int index = getApproximate(180, angleList);
                                //Log.v("lock", "touch ---1---move index=" + index);
                                if (mOnTimeChangeListener != null) {
                                    mOnTimeChangeListener.updateSingle(1,index);
                                }
                                if(isDraw==false)invalidate();
                            } else if (angle < angleTrack1 - touchPrecision) {
                                angleTrack1 = angle;
                                for (int i = 0; i < mTickMaxCount; i++) {
                                    angleList[i] = angleList[i] - touchRangle;
                                    if(angleList[i]<0){
                                        angleList[i] = 360+angleList[i];
                                    }
                                }
                                //获取接近值索引
                                int index = getApproximate(180, angleList);
                                //Log.v("lock", "touch ---1---move index=" + index);
                                if (mOnTimeChangeListener != null) {
                                    mOnTimeChangeListener.updateSingle(1,index);
                                }
                                if(isDraw==false)invalidate();
                            }
                        }
                    }else if(touchCircle2==true) {  //circle2
                        if (angleTrack2 != angle) {
                            if (angle > angleTrack2 + touchPrecision) {
                                angleTrack2 = angle;
                                for (int i = 0; i < mCircle2_TickMaxCount; i++) {
                                    mCircle2_angleList[i] = mCircle2_angleList[i] + touchRangle;
                                    if(mCircle2_angleList[i]>360){
                                        mCircle2_angleList[i] = mCircle2_angleList[i]-360;
                                    }
                                }
                                //获取接近值索引
                                int index = getApproximate(180, mCircle2_angleList);
                                //Log.v("lock", "touch ---2---move index=" + index);
                                if (mOnTimeChangeListener != null) {
                                    mOnTimeChangeListener.updateSingle(2,index);
                                }
                                if(isDraw==false)invalidate();
                            } else if (angle < angleTrack2 - touchPrecision) {
                                angleTrack2 = angle;
                                for (int i = 0; i < mCircle2_TickMaxCount; i++) {
                                    mCircle2_angleList[i] = mCircle2_angleList[i] - touchRangle;
                                    if(mCircle2_angleList[i]<0){
                                        mCircle2_angleList[i] = 360+mCircle2_angleList[i];
                                    }
                                }
                                //获取接近值索引
                                int index = getApproximate(180, mCircle2_angleList);
                                //Log.v("lock", "touch ---2---move index=" + index);
                                if (mOnTimeChangeListener != null) {
                                    mOnTimeChangeListener.updateSingle(2,index);
                                }
                                if(isDraw==false)invalidate();
                            }
                        }
                    }else if(touchCircle3==true) {  //circle3
                        if (angleTrack3 != angle) {
                            if (angle > angleTrack3 + touchPrecision) {
                                angleTrack3 = angle;
                                for (int i = 0; i < mCircle3_TickMaxCount; i++) {
                                    mCircle3_angleList[i] = mCircle3_angleList[i] + touchRangle;
                                    if(mCircle3_angleList[i]>360){
                                        mCircle3_angleList[i] = mCircle3_angleList[i]-360;
                                    }
                                }
                                //获取接近值索引
                                int index = getApproximate(180, mCircle3_angleList);
                                //Log.v("lock", "touch ---3---move index=" + index);
                                if (mOnTimeChangeListener != null) {
                                    mOnTimeChangeListener.updateSingle(3,index);
                                }
                                if(isDraw==false)invalidate();
                            } else if (angle < angleTrack3 - touchPrecision) {
                                angleTrack3 = angle;
                                for (int i = 0; i < mCircle3_TickMaxCount; i++) {
                                    mCircle3_angleList[i] = mCircle3_angleList[i] - touchRangle;
                                    if(mCircle3_angleList[i]<0){
                                        mCircle3_angleList[i] = 360+mCircle3_angleList[i];
                                    }
                                }
                                //获取接近值索引
                                int index = getApproximate(180, mCircle3_angleList);
                                if (mOnTimeChangeListener != null) {
                                    mOnTimeChangeListener.updateSingle(3,index);
                                }
                                //Log.v("lock", "touch ---3---move index=" + index);
                                if(isDraw==false)invalidate();
                            }
                        }
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    touchCircle1 = false;touchCircle2=false;touchCircle3=false;
                    angleTrack1 = -1;
                    angleTrack2 = -1;
                    angleTrack3 = -1;
                    //校准circle1
                    int index1 = getApproximate(180, angleList);
                    //Log.v("lock", "touch ---1---up index1=" + index1);
                    calibrationValue1(index1);
                    //校准circle2
                    int index2 = getApproximate(180, mCircle2_angleList);
                    //Log.v("lock", "touch ---2---up index2=" + index2);
                    calibrationValue2(index2);
                    //校准circle3
                    int index3 = getApproximate(180, mCircle3_angleList);
                    //Log.v("lock", "touch ---3---up index3=" + index3);
                    calibrationValue3(index3);

                    if (mOnTimeChangeListener != null) {
                        mOnTimeChangeListener.onChange(index1,index2,index3);
                    }
                    return true;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.v("lock", "e="+e);
        }
        return super.onTouchEvent(event);
    }

    //--------------------------------监听----------------------------------------------------------
    public interface OnTimeChangeListener {
        void onChange(int value1,int value2,int value3);
        void updateSingle(int index,int value);
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
    private int getApproximate(int x, int[] src) {
        if (src == null) {
            return -1;
        }
        if (src.length == 1) {
            return 0;
        }
        int minDifference = Math.abs(src[0] - x);
        int minIndex = 0;
        for (int i = 1; i < src.length; i++) {
            int temp = Math.abs(src[i] - x);
            if (temp < minDifference) {
                minIndex = i;
                minDifference = temp;
            }
        }
        return minIndex;
    }
    /**
     * 获取接近值的索引
     *
     * @param x
     * @param src
     * @return
     */
    private int getApproximateOrder(int x, int[] src) throws Exception{
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
