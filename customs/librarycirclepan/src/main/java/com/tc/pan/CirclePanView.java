package com.tc.pan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class CirclePanView extends ViewGroup {
	private static final String TAG = "CircleTouchView";
	private Context mContext;
	//------------整个布局--------------
	private int mWidth, mHight;
	//手机屏幕高度的一半
	private int mScreenHalfWidth;
	//---------------set----------
	private boolean explicit = false;
	private float touchSensitivity = 0.0f;
	//------------中心图标--------------
	//触摸图标1
	private ImageView mAlphaView;
	//触摸图标2
	private ImageView mCenterView;
	private AlphaAnimation mAlphaAnimation;
	//中心图标宽 & 高
	private int mAlphaViewWidth, mAlphaViewHeight;
	private int mCenterViewWidth, mCenterViewHeight;
	//中心图标顶部到屏幕顶部的距离 & 中心图标底部部到屏幕顶部的距离
	private int mCenterViewTop, mCenterViewBottom;
	private int mAlphaViewTop, mAlphaViewBottom;
	//中心图标左边到屏幕左边的距离 & 中心图标右边到屏幕左边的距离
	private int mCenterViewLeft, mCenterViewRight;
	private int mAlphaViewLeft, mAlphaViewRight;
	private Rect mCenterViewRect;
	private boolean mTracking = false;
	//------------区域--------------
	private Rect mBgViewRect;
	//整个区域中心坐标
	private int circleCenterX,circleCenterY;
	private ImageView mBgView,mBgViewUp,mBgViewDown,mBgViewLeft,mBgViewRight;
	//背景图标宽 & 高
	private int mBgViewWidth, mBgViewHeight;
	//背景图标顶部到屏幕顶部的距离 & 背景图标底部部到屏幕顶部的距离
	private int mBgViewTop, mBgViewBottom;
	//背景图标左边到屏幕左边的距离 & 背景图标右边到屏幕左边的距离
	private int mBgLeft, mBgRight;
	private float[] touchPosition = new float[2];
	private int newWidth=0,newHeight=0;
	private boolean updateOnLayout = false;
	//---------------direction------------
	private CHOICE_DIRECTION directionChoice = CHOICE_DIRECTION.CIRCLE;
	private enum CHOICE_DIRECTION{
		UP,DOWN,LEFT,RIGHT,CIRCLE
	}
	//---------------callback------------
	private CircleOnClickListener mCircleOnClickListener;
	//监听接口
	public interface CircleOnClickListener{
		void OnChoiceDirectionCallBack(boolean up,boolean down,boolean left,boolean right);
	}
	
	public CirclePanView(Context context) {
		super(context);
		mContext = context;
		initViews(context);
		initValue();
		onAnimationStart();
	}

	public CirclePanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initViews(context);
		initValue();
		onAnimationStart();
	}

	/**
	 * 显示中心图标动画
	 */
	@Override
	protected void onAnimationStart() {
		// TODO Auto-generated method stub
		super.onAnimationStart();
		if(explicit) {
			mAlphaView.setVisibility(View.VISIBLE);
			//动画会不停的触发onDraw
			if (mAlphaAnimation == null) {
				mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
				mAlphaAnimation.setDuration(1000);
			}
			mAlphaAnimation.setRepeatCount(Animation.INFINITE);
			mAlphaView.startAnimation(mAlphaAnimation);
		}
	}

	/**
	 * 停止显示动画
	 */
	@Override
	protected void onAnimationEnd() {
		// TODO Auto-generated method stub
		super.onAnimationEnd();
		if (mAlphaAnimation != null) {
			mAlphaAnimation = null;
		}
		mAlphaView.setAnimation(null);
		mAlphaView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.i(TAG,"onMeasure widthMeasureSpec="+widthMeasureSpec+", heightMeasureSpec="+heightMeasureSpec+", Width="+getMeasuredWidth()+", Height="+getMeasuredHeight());
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		//Log.i(TAG,"onLayout changed="+changed+", l="+l+", t="+t+", r="+r+", b="+b);
		//l=0, t=0, r=1080, b=1835
		if (changed) {
			mWidth = r;
			mHight = b;
			//mHalfWidth >> 1为向右位移1位，相当于mHalfWidth / 2。采用位移的原因是计算效率比较高。
			mScreenHalfWidth = mWidth >> 1;
			Log.i(TAG,"onLayout mWidth="+mWidth+", mHight="+mHight);

			getViewMeasure();

			//背景图标顶部到屏幕顶部的距离
			mBgViewTop = (mHight >> 1) - (mBgViewHeight >> 1);
			//背景图标底部部到屏幕顶部的距离
			mBgViewBottom = (mHight >> 1) + (mBgViewHeight >> 1);
			//背景图标左边到屏幕左边的距离
			mBgLeft = (mWidth >> 1) - (mBgViewWidth >> 1);
			//背景图标右边到屏幕左边的距离
			mBgRight = (mWidth >> 1) + (mBgViewWidth >> 1);
			//Log.i(TAG,"onLayout mBgViewTop="+mBgViewTop+", mBgViewBottom="+mBgViewBottom+", mBgLeft="+mBgLeft+", mBgRight="+mBgRight);

			circleCenterX = mBgLeft+(mBgViewWidth >> 1);
			circleCenterY = mBgViewTop+(mBgViewHeight >> 1);
			//Log.i(TAG,"onLayout circleCenterX="+circleCenterX+", circleCenterY="+circleCenterY);

			//中心图标顶部到屏幕顶部的距离
			mCenterViewTop = (mHight >> 1) - (mCenterViewHeight >> 1);
			//中心图标底部部到屏幕顶部的距离
			mCenterViewBottom = (mHight >> 1) + (mCenterViewHeight >> 1);
			//显示动画的图标顶部到屏幕顶部的距离
			mAlphaViewTop = (mHight >> 1) - (mAlphaViewHeight >> 1);
			//显示动画的图标底部到屏幕顶部的距离
			mAlphaViewBottom = (mHight >> 1) + (mAlphaViewHeight >> 1);
			//Log.i(TAG,"onLayout mCenterViewTop="+mCenterViewTop+", mCenterViewBottom="+mCenterViewBottom+", mAlphaViewTop="+mAlphaViewTop+", mAlphaViewBottom="+mAlphaViewBottom);

			//中心图标左边到屏幕左边的距离
			mCenterViewLeft = (mWidth >> 1) - (mCenterViewWidth >> 1);
			//中心图标右边到屏幕左边的距离
			mCenterViewRight = (mWidth >> 1) + (mCenterViewWidth >> 1);
			//显示动画的左边到屏幕左边的距离
			mAlphaViewLeft = (mWidth >> 1) - (mAlphaViewWidth >> 1);
			//显示动画的右边到屏幕左边的距离
			mAlphaViewRight = (mWidth >> 1) + (mAlphaViewWidth >> 1);
			//Log.i(TAG,"onLayout mCenterViewLeft="+mCenterViewLeft+", mCenterViewRight="+mCenterViewRight+", mAlphaViewLeft="+mAlphaViewLeft+", mAlphaViewRight="+mAlphaViewRight);

			setChildViewLayout();

			//创建整个圆背景区域
			mBgViewRect = new Rect(mBgLeft, mBgViewTop, mBgRight, mBgViewBottom);
			//创建中心图标所在矩形区域
			mCenterViewRect = new Rect((mWidth >> 1) - (mAlphaViewWidth >> 1), mAlphaViewTop, (mWidth >> 1) + (mAlphaViewWidth >> 1), mAlphaViewBottom);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.i(TAG,"onDraw");
		super.onDraw(canvas);
	}

//	@Override
//	protected void dispatchDraw(Canvas canvas) {
//		super.dispatchDraw(canvas);
//		Log.i(TAG,"dispatchDraw");
//		//绘制子view
//
//		//
//	}

	/**
	 * 用户手机点下屏幕时首先会先调用执行该函数，然后再执行onTouchEvent
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				//Log.i(TAG,"onInterceptTouchEvent ACTION_DOWN");
				//手指点在中心图标范围区域内
				if ( mCenterViewRect.contains((int) x, (int) y) && explicit ) {
					mTracking = true;
					onAnimationEnd();
					return true;
				}

				//手指点在整个圆内
				if( mBgViewRect.contains((int) x, (int) y) && explicit ){
					//手指点在中心图标范围区域内
					if ( mCenterViewRect.contains((int) x, (int) y) ) {
						mTracking = true;
						onAnimationEnd();
					}else{
						mTracking = true;
						onAnimationEnd();
					}
					return true;
				}
				break;
			default:
				break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/* mTracking为true时，说明中心图标被点击移动
		 * 即只有在中心图标被点击移动的情况下，onTouchEvent
		 * 事件才会触发。
		 */
		if (mTracking) {
			final int action = event.getAction();
			final float nx = event.getX();
			final float ny = event.getY();

			switch (action) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					//中心图标移动
					handleMoveView(nx, ny);
					break;
				case MotionEvent.ACTION_UP:
					mTracking = false;
					resetMoveView();
					break;
				case MotionEvent.ACTION_CANCEL:
					mTracking = false;
					resetMoveView();
					break;
			}
		}
		return mTracking || super.onTouchEvent(event);
	}

	private void initValue(){
		explicit = true;
	}

	/**
	 * 获取图标，将获取的图标添加入MainView，设置图标的可见性
	 */
	private void initViews(Context context) {
		//设置group背景
		//setBackgroundResource(R.drawable.ic_launcher);
		//bg
		mBgView = new ImageView(context);
		mBgView.setImageResource(R.drawable.circle_bg);
		setViewsLayout(mBgView);
		mBgView.setVisibility(View.VISIBLE);
		mBgViewUp = new ImageView(context);
		mBgViewUp.setImageResource(R.drawable.circle_up);
		setViewsLayout(mBgViewUp);
		mBgViewUp.setVisibility(View.INVISIBLE);
		mBgViewDown = new ImageView(context);
		mBgViewDown.setImageResource(R.drawable.circle_down);
		setViewsLayout(mBgViewDown);
		mBgViewDown.setVisibility(View.INVISIBLE);
		mBgViewLeft = new ImageView(context);
		mBgViewLeft.setImageResource(R.drawable.circle_let);
		setViewsLayout(mBgViewLeft);
		mBgViewLeft.setVisibility(View.INVISIBLE);
		mBgViewRight = new ImageView(context);
		mBgViewRight.setImageResource(R.drawable.circle_right);
		setViewsLayout(mBgViewRight);
		mBgViewRight.setVisibility(View.INVISIBLE);

		//touch
		mAlphaView = new ImageView(context);
		mAlphaView.setImageResource(R.drawable.circle_touch_down);
		setViewsLayout(mAlphaView);
		mAlphaView.setVisibility(View.INVISIBLE);
		mCenterView = new ImageView(context);
		mCenterView.setImageResource(R.drawable.circle_touch_down);
		setViewsLayout(mCenterView);
		mCenterView.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置获取图标的参数，并添加到MainView
	 */
	private void setViewsLayout(ImageView image) {
		image.setScaleType(ScaleType.CENTER);
		image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(image);
	}

	/**
	 * 获取中心图片和显示动画图片的宽、高
	 */
	private void getViewMeasure() {
		mBgView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		//显示动画图标的宽
		mBgViewWidth = mBgView.getMeasuredWidth();
		//显示动画图标的高
		mBgViewHeight = mBgView.getMeasuredHeight();
		//Log.i(TAG,"getViewMeasure mBgViewWidth="+mBgViewWidth+", mBgViewHeight="+mBgViewHeight);

		mAlphaView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		//显示动画图标的宽
		mAlphaViewWidth = mAlphaView.getMeasuredWidth();
		//显示动画图标的高
		mAlphaViewHeight = mAlphaView.getMeasuredHeight();
		//Log.i(TAG,"getViewMeasure mAlphaViewWidth="+mAlphaViewWidth+", mAlphaViewHeight="+mAlphaViewHeight);

		mCenterView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		//中心图标的宽
		mCenterViewWidth = mCenterView.getMeasuredWidth();
		//中心图标的高
		mCenterViewHeight = mCenterView.getMeasuredHeight();
		//Log.i(TAG,"getViewMeasure mCenterViewWidth="+mCenterViewWidth+", mCenterViewHeight="+mCenterViewHeight);
	}

	/**
	 * 设置中心图标和显示动画图片在MainView中的布局
	 */
	private void setChildViewLayout() {
		mBgView.layout(mBgLeft, mBgViewTop, mBgRight, mBgViewBottom);
		mBgViewUp.layout(mBgLeft, mBgViewTop, mBgRight, mBgViewBottom);
		mBgViewDown.layout(mBgLeft, mBgViewTop, mBgRight, mBgViewBottom);
		mBgViewLeft.layout(mBgLeft, mBgViewTop, mBgRight, mBgViewBottom);
		mBgViewRight.layout(mBgLeft, mBgViewTop, mBgRight, mBgViewBottom);
		mAlphaView.layout(mAlphaViewLeft, mAlphaViewTop, mAlphaViewRight, mAlphaViewBottom);
		mCenterView.layout(mCenterViewLeft, mCenterViewTop, mCenterViewRight, mCenterViewBottom);
	}

	/**
	 * 实现图标在固定圆圈内移动的方法
	 计算方式1：以大圆圆心为中心点
	 大圆圆心（x0,y0） 大圆半径r0
	 手指触控的点相当于小圆的圆心(x1,y1) 小圆半径r1
	 小圆圆心X坐标到大圆圆心X坐标的距离x10
	 小圆圆心Y坐标到大圆圆心Y坐标的距离y10
	 需要指定的半径r2
	 得到相应的圆心坐标（x2,y2）
	 cos角度 = x10/r1 = x2/r2   可以得到x2
	 sin角度 = y10/r1 = y2/r2   可以得到y2
	 计算方式2： 以大圆圆心为中心点
	 手指触控的点相当于小圆的圆心(x1,y1) 小圆半径r1
	 需要指定的半径r2
	 得到相应的圆心坐标（x2,y2）
	 r2/r1 = x2/x1    可以得到x2
	 r2/r1 = y2/y1    可以得到y2
	 */
	private void handleMoveView(float x, float y) {
		int mHalfCenterViewWidth = mCenterViewWidth >> 1;
			
		//Radius为中心图标移动的限定的圆范围区域半径(可根据自己的需要设置大小)
		//int Radius = mCenterViewWidth + mHalfCenterViewWidth;
		int Radius = (mBgViewWidth >> 1);
		//int Radius = 100;
		//Log.i(TAG,"handleMoveView move Radius="+Radius);

		/* 若用户手指移动的点与中心点的距离长度大于Radius，则中心图标坐标位置限定在移动区域范围圆弧上。
		 * 一般是用户拖动中心图标，手指移动到限定圆范围区域外。
		 */
		touchPosition[0] = x;
		touchPosition[1] = y;
		//if ( Math.sqrt(dist2(x - mScreenHalfWidth, y - (mCenterView.getTop() + mCenterViewWidth / 2))) > (Radius-mHalfCenterViewWidth) ){
		if ( Math.sqrt(dist2(dynamicCalcTouchCircleLenX(x), dynamicCalcTouchCircleLenY(y))) > (Radius-mHalfCenterViewWidth) ){
			//Log.i(TAG,"handleMoveView is over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

//			//原理为x1 / x = r1 / r ，此时的x，y为以Radius为半径的圆圈圆弧上的坐标
//			xy1[0] = (float) ( ((Radius-mHalfCenterViewWidth) / (Math.sqrt(dist2(x - mScreenHalfWidth, y - (mCenterView.getTop() + mHalfCenterViewWidth))))) * (x - mScreenHalfWidth) + mScreenHalfWidth);
//
//			xy1[1] = (float) (((Radius-mHalfCenterViewWidth) / (Math.sqrt(dist2(x - mScreenHalfWidth, y - (mCenterView.getTop() + mHalfCenterViewWidth)
//			)))) * (y - (mCenterView.getTop() + mHalfCenterViewWidth)) + mCenterView.getTop() + mHalfCenterViewWidth);
			dynamicCalcCircleX(Radius-mHalfCenterViewWidth,x,y);
		}

		//Log.i(TAG,"handleMoveView x="+x+", left="+((int)x - mCenterView.getWidth()/2)+", y="+y+", top="+((int)y - mCenterView.getHeight()/2));
		/* 图形的坐标是以左上角为基准的，
		 * 所以，为了使手指所在的坐标和图标的中心位置一致，
		 * 中心坐标要减去宽度和高度的一半。
		 */
//		mCenterView.setX( (int)x - mCenterView.getWidth()/2 );
//		mCenterView.setY( (int)y - mCenterView.getHeight()/2 );
		mCenterView.setX( (int)touchPosition[0] - (mCenterView.getWidth()>>1) );
		mCenterView.setY( (int)touchPosition[1] - (mCenterView.getHeight()>>1) );

        //int dd = (int) (Math.atan((xy1[1]-circleCenterY) / (xy1[0]-circleCenterX)) / Math.PI * 180);
		//double tt = Math.toDegrees( Math.atan((touchPosition[1]-circleCenterY) / (touchPosition[0]-circleCenterX)) );
		touchChoice(x,y,Math.toDegrees( Math.atan((touchPosition[1]-circleCenterY) / (touchPosition[0]-circleCenterX)) ));

	    invalidate();
	}

	/**
	 * 重置中心图标，回到原位置
	 */
	private void resetMoveView() {
//		mCenterView.setX(mWidth / 2 - mCenterViewWidth /2);
//		mCenterView.setY((mCenterView.getTop() + mCenterViewHeight / 2) - mCenterViewHeight / 2);
		mCenterView.setX(mCenterViewLeft);
		mCenterView.setY(mCenterViewTop);
		choiceDirection(CHOICE_DIRECTION.CIRCLE);
		onAnimationStart();
		invalidate();
	}

	private void touchChoice(float x, float y,double angle){
		if( Math.sqrt(dist2(dynamicCalcTouchCircleLenX(x), dynamicCalcTouchCircleLenY(y))) >= ((mBgViewWidth >> 1)-(mCenterViewWidth >> 1)-touchSensitivity) ){
			if( x>circleCenterX && y>circleCenterY ){  //第四象限
				if(angle>=0.0&&angle<=45.0){
					choiceDirection(CHOICE_DIRECTION.RIGHT);
				}else if(angle>45.0&&angle<=90.0){
					choiceDirection(CHOICE_DIRECTION.DOWN);
				}
			}else if( x>circleCenterX && y<circleCenterY ){   //第一象限
				if(angle>=-45.0&&angle<=0.0){
					choiceDirection(CHOICE_DIRECTION.RIGHT);
				}else if(angle>=-90.0&&angle<-45.0){
					choiceDirection(CHOICE_DIRECTION.UP);
				}
			}else if( x<circleCenterX && y<circleCenterY ) {   //第二象限
				if(angle>=45.0&&angle<=90.0){
					choiceDirection(CHOICE_DIRECTION.UP);
				}else if(angle>=0.0&&angle<45.0){
					choiceDirection(CHOICE_DIRECTION.LEFT);
				}
			}else if( x<circleCenterX && y>circleCenterY ) {   //第三象限
				if(angle>=-45.0&&angle<=0.0){
					choiceDirection(CHOICE_DIRECTION.LEFT);
				}else if(angle>=-90.0&&angle<-45.0){
					choiceDirection(CHOICE_DIRECTION.DOWN);
				}
			}
		}
	}

	private void choiceDirection(CHOICE_DIRECTION choice){
		if(mTracking){
			if( choice == CHOICE_DIRECTION.UP && directionChoice!=CHOICE_DIRECTION.UP ){
				//Log.i(TAG,"choiceDirection touch choice up");
				directionChoice = CHOICE_DIRECTION.UP;
				mBgViewUp.setVisibility(View.VISIBLE);
				mBgViewDown.setVisibility(View.INVISIBLE);
				mBgViewLeft.setVisibility(View.INVISIBLE);
				mBgViewRight.setVisibility(View.INVISIBLE);
				if(mCircleOnClickListener!=null){
					mCircleOnClickListener.OnChoiceDirectionCallBack(true,false,false,false);
				}
			}else if( choice == CHOICE_DIRECTION.DOWN && directionChoice!=CHOICE_DIRECTION.DOWN ){
				//Log.i(TAG,"choiceDirection touch choice down");
				directionChoice = CHOICE_DIRECTION.DOWN;
				mBgViewUp.setVisibility(View.INVISIBLE);
				mBgViewDown.setVisibility(View.VISIBLE);
				mBgViewLeft.setVisibility(View.INVISIBLE);
				mBgViewRight.setVisibility(View.INVISIBLE);
				if(mCircleOnClickListener!=null){
					mCircleOnClickListener.OnChoiceDirectionCallBack(false,true,false,false);
				}
			}else if( choice == CHOICE_DIRECTION.LEFT && directionChoice!=CHOICE_DIRECTION.LEFT ){
				//Log.i(TAG,"choiceDirection touch choice left");
				directionChoice = CHOICE_DIRECTION.LEFT;
				mBgViewUp.setVisibility(View.INVISIBLE);
				mBgViewDown.setVisibility(View.INVISIBLE);
				mBgViewLeft.setVisibility(View.VISIBLE);
				mBgViewRight.setVisibility(View.INVISIBLE);
				if(mCircleOnClickListener!=null){
					mCircleOnClickListener.OnChoiceDirectionCallBack(false,false,true,false);
				}
			}else if( choice == CHOICE_DIRECTION.RIGHT && directionChoice!=CHOICE_DIRECTION.RIGHT ){
				//Log.i(TAG,"choiceDirection touch choice right");
				directionChoice = CHOICE_DIRECTION.RIGHT;
				mBgViewUp.setVisibility(View.INVISIBLE);
				mBgViewDown.setVisibility(View.INVISIBLE);
				mBgViewLeft.setVisibility(View.INVISIBLE);
				mBgViewRight.setVisibility(View.VISIBLE);
				if(mCircleOnClickListener!=null){
					mCircleOnClickListener.OnChoiceDirectionCallBack(false,false,false,true);
				}
			}
		}else{
			if( choice == CHOICE_DIRECTION.CIRCLE ){
				//Log.i(TAG,"choiceDirection touch choice circle");
				directionChoice = CHOICE_DIRECTION.CIRCLE;
				mBgViewUp.setVisibility(View.INVISIBLE);
				mBgViewDown.setVisibility(View.INVISIBLE);
				mBgViewLeft.setVisibility(View.INVISIBLE);
				mBgViewRight.setVisibility(View.INVISIBLE);
				if(mCircleOnClickListener!=null){
					mCircleOnClickListener.OnChoiceDirectionCallBack(false,false,false,false);
				}
			}
		}
	}

	/**
	 * 计算触摸圆心Y坐标到小圆圆心Y坐标的距离
	 * @param touchY
	 * @return
	 */
	private float dynamicCalcTouchCircleLenY(float touchY){
		//return touchY - (mCenterView.getTop() + (mCenterViewHeight >> 1));
		return touchY - circleCenterY;
	}

	/**
	 * 计算触摸圆心X坐标到小圆圆心X坐标的距离
	 * @param touchX
	 * @return
	 */
	private float dynamicCalcTouchCircleLenX(float touchX){
		return touchX - circleCenterX;
	}

	/**
	 * 根据需要的半径得到X坐标
	 * @param r1
	 * @return
	 */
	private void dynamicCalcCircleX(int r1,float touchX,float touchY){
		//原理为x1 / x = r1 / r ，此时的x，y为以Radius为半径的圆圈圆弧上的坐标
		touchPosition[0] = (float) ( (r1/(Math.sqrt(dist2(dynamicCalcTouchCircleLenX(touchX), dynamicCalcTouchCircleLenY(touchY))))) * dynamicCalcTouchCircleLenX(touchX) + circleCenterX );
		touchPosition[1] = (float) ( (r1/(Math.sqrt(dist2(dynamicCalcTouchCircleLenX(touchX), dynamicCalcTouchCircleLenY(touchY))))) * dynamicCalcTouchCircleLenY(touchY) + circleCenterY );
	}
	
	/**
	平方和计算
	 */
	private float dist2(float dx, float dy) {
		return dx * dx + dy * dy;
	}

	/**
	 * 角度 转 弧度
	 *
	 * @param value
	 * @return
	 */
	private double angleToRadian(double value) {
		return value * Math.PI / 180;
	}

	/**
	 * 弧度 转 角度
	 *
	 * @param value
	 * @return
	 */
	private double radianToAngle(double value) {
		return value * 180 / Math.PI;
	}

	public void setBgExplicitMode(boolean explicit){
		if(mBgView==null || mCenterView==null) return;

		this.explicit = explicit;
		if(explicit){
			mBgView.setImageResource(R.drawable.circle_bg);
			mCenterView.setImageResource(R.drawable.circle_touch_down);
			onAnimationStart();
		}else{
			mBgView.setImageResource(R.drawable.circle_bg1);
			mCenterView.setImageResource(R.drawable.circle_touch_up);
			onAnimationEnd();
		}
		invalidate();
	}

	public void setTouchSensitivity(float sensitivity){
		if(mBgViewWidth<=0 || mCenterViewWidth<=0) return;

		if( sensitivity>=0 && sensitivity<=((mBgViewWidth >> 1)-(mCenterViewWidth >> 1)) ){
			this.touchSensitivity = sensitivity;
		}
	}

	public void setCircleOnClickListener(CircleOnClickListener listener){
		this.mCircleOnClickListener =  listener;
	}

	/*----------update---------------------------------------------------------------------------------------------*/
	/*
	public void requestOnLayout(int width,int height){
		int[] temp = areaBigCalculation(mContext,width,height);

		if(newWidth==temp[0] && newHeight==temp[1]) return;
        newWidth = temp[0];
        newHeight = temp[1];
		updateOnLayout = true;
		//LogTools.debug("common","newWidth="+newWidth+", newHeight="+newHeight);
		onAnimationEnd();
		deleteAllView();
		updateViews();
		setBgExplicitMode(explicit);
		onAnimationStart();
		invalidate();
        requestLayout();
	}

	public void requestOnLayoutPush(int width,int height){
		int[] temp = areaBigCalculationPush(mContext,width,height);

		if(newWidth==temp[0] && newHeight==temp[1]) return;
		newWidth = temp[0];
		newHeight = temp[1];
		updateOnLayout = true;
		//LogTools.debug("common","newWidth="+newWidth+", newHeight="+newHeight);
		onAnimationEnd();
		deleteAllView();
		updateViews();
		setBgExplicitMode(explicit);
		onAnimationStart();
		invalidate();
		requestLayout();
	}

	public void requestOnLayoutPlay(int width,int height){
		int[] temp = areaBigCalculationPlay(mContext,width,height);

		if(newWidth==temp[0] && newHeight==temp[1]) return;
		newWidth = temp[0];
		newHeight = temp[1];
		updateOnLayout = true;
		//LogTools.debug("common","newWidth="+newWidth+", newHeight="+newHeight);
		onAnimationEnd();
		deleteAllView();
		updateViewsPlay();
		setBgExplicitModePlay(explicit);
		onAnimationStart();
		invalidate();
		requestLayout();
	}

	private void deleteAllView(){
//		int size = getChildCount();
//		for( int i = 0; i < size; i++){
//			removeViewAt(i);
//		}
        removeAllViews();
	}

	private void updateViews() {
		//设置group背景
		//setBackgroundResource(R.drawable.ic_launcher);
		//bg
		mBgView = new ImageView(mContext);
		//mBgView.setImageResource(R.drawable.circle_bg);
		//mBgView.setImageBitmap(bitmapCreate(R.drawable.circle_bg,width,height));
		mBgView.setImageDrawable(zoomImage(mContext,R.drawable.circle_bg,newWidth,newHeight));
		setViewsLayout(mBgView);
		mBgView.setVisibility(View.VISIBLE);

		mBgViewUp = new ImageView(mContext);
		//mBgViewUp.setImageResource(R.drawable.circle_up);
		//mBgViewUp.setImageBitmap(bitmapCreate(R.drawable.circle_up,width,height));
		mBgViewUp.setImageDrawable(zoomImage(mContext,R.drawable.circle_up,newWidth,newHeight));
		setViewsLayout(mBgViewUp);
		mBgViewUp.setVisibility(View.INVISIBLE);

		mBgViewDown = new ImageView(mContext);
		//mBgViewDown.setImageResource(R.drawable.circle_down);
		//mBgViewDown.setImageBitmap(bitmapCreate(R.drawable.circle_down,width,height));
		mBgViewDown.setImageDrawable(zoomImage(mContext,R.drawable.circle_down,newWidth,newHeight));
		setViewsLayout(mBgViewDown);
		mBgViewDown.setVisibility(View.INVISIBLE);

		mBgViewLeft = new ImageView(mContext);
		//mBgViewLeft.setImageResource(R.drawable.circle_let);
		//mBgViewLeft.setImageBitmap(bitmapCreate(R.drawable.circle_let,width,height));
		mBgViewLeft.setImageDrawable(zoomImage(mContext,R.drawable.circle_let,newWidth,newHeight));
		setViewsLayout(mBgViewLeft);
		mBgViewLeft.setVisibility(View.INVISIBLE);

		mBgViewRight = new ImageView(mContext);
		//mBgViewRight.setImageResource(R.drawable.circle_right);
		//mBgViewRight.setImageBitmap(bitmapCreate(R.drawable.circle_right,width,height));
		mBgViewRight.setImageDrawable(zoomImage(mContext,R.drawable.circle_right,newWidth,newHeight));
		setViewsLayout(mBgViewRight);
		mBgViewRight.setVisibility(View.INVISIBLE);

		//touch
		mAlphaView = new ImageView(mContext);
		//mAlphaView.setImageResource(R.drawable.circle_touch_down);
		mAlphaView.setImageDrawable(zoomImage(mContext,R.drawable.circle_touch_down,(int)(newWidth/matBS),(int)(newHeight/matBS)));
		setViewsLayout(mAlphaView);
		mAlphaView.setVisibility(View.INVISIBLE);
		mCenterView = new ImageView(mContext);
		//mCenterView.setImageResource(R.drawable.circle_touch_down);
		mCenterView.setImageDrawable(zoomImage(mContext,R.drawable.circle_touch_down,(int)(newWidth/matBS),(int)(newHeight/matBS)));
		setViewsLayout(mCenterView);
		mCenterView.setVisibility(View.VISIBLE);
	}

	private void updateViewsPlay() {
		//设置group背景
		//setBackgroundResource(R.drawable.ic_launcher);
		//bg
		mBgView = new ImageView(mContext);
		//mBgView.setImageResource(R.drawable.circle_bg);
		//mBgView.setImageBitmap(bitmapCreate(R.drawable.circle_bg,width,height));
		mBgView.setImageDrawable(zoomImage(mContext,R.drawable.auto_play_circle_bg,newWidth,newHeight));
		setViewsLayout(mBgView);
		mBgView.setVisibility(View.VISIBLE);

		mBgViewUp = new ImageView(mContext);
		//mBgViewUp.setImageResource(R.drawable.circle_up);
		//mBgViewUp.setImageBitmap(bitmapCreate(R.drawable.circle_up,width,height));
		mBgViewUp.setImageDrawable(zoomImage(mContext,R.drawable.auto_play_circle_up,newWidth,newHeight));
		setViewsLayout(mBgViewUp);
		mBgViewUp.setVisibility(View.INVISIBLE);

		mBgViewDown = new ImageView(mContext);
		//mBgViewDown.setImageResource(R.drawable.circle_down);
		//mBgViewDown.setImageBitmap(bitmapCreate(R.drawable.circle_down,width,height));
		mBgViewDown.setImageDrawable(zoomImage(mContext,R.drawable.auto_play_circle_down,newWidth,newHeight));
		setViewsLayout(mBgViewDown);
		mBgViewDown.setVisibility(View.INVISIBLE);

		mBgViewLeft = new ImageView(mContext);
		//mBgViewLeft.setImageResource(R.drawable.circle_let);
		//mBgViewLeft.setImageBitmap(bitmapCreate(R.drawable.circle_let,width,height));
		mBgViewLeft.setImageDrawable(zoomImage(mContext,R.drawable.auto_play_circle_left,newWidth,newHeight));
		setViewsLayout(mBgViewLeft);
		mBgViewLeft.setVisibility(View.INVISIBLE);

		mBgViewRight = new ImageView(mContext);
		//mBgViewRight.setImageResource(R.drawable.circle_right);
		//mBgViewRight.setImageBitmap(bitmapCreate(R.drawable.circle_right,width,height));
		mBgViewRight.setImageDrawable(zoomImage(mContext,R.drawable.auto_play_circle_right,newWidth,newHeight));
		setViewsLayout(mBgViewRight);
		mBgViewRight.setVisibility(View.INVISIBLE);

		//touch
		mAlphaView = new ImageView(mContext);
		//mAlphaView.setImageResource(R.drawable.circle_touch_down);
		mAlphaView.setImageDrawable(zoomImage(mContext,R.drawable.auto_play_circle_touch,(int)(newWidth/matBS),(int)(newHeight/matBS)));
		setViewsLayout(mAlphaView);
		mAlphaView.setVisibility(View.INVISIBLE);
		mCenterView = new ImageView(mContext);
		//mCenterView.setImageResource(R.drawable.circle_touch_down);
		mCenterView.setImageDrawable(zoomImage(mContext,R.drawable.auto_play_circle_touch,(int)(newWidth/matBS),(int)(newHeight/matBS)));
		setViewsLayout(mCenterView);
		mCenterView.setVisibility(View.VISIBLE);
	}

	private void updateViewsLayout(ImageView image) {
		image.setScaleType(ScaleType.CENTER);
		image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(image);
//		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		layoutParams.height = height;// 设置图片的高度
//		layoutParams.width = width; // 设置图片的宽度
//		image.setLayoutParams(layoutParams);

//		LayoutParams params = image.getLayoutParams();
//		params.height=height;
//		params.width=width;
//		image.setLayoutParams(params);
	}

	private Bitmap bitmapCreate(int resId, int newWidth , int newHeight){
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
		// 获得图片的宽高   
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// 计算缩放比例   
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数   
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
 		return newbm;
	}

	public static Drawable zoomImage(Context mContext, int resId, int newWidth, int newHeight){
		Resources res = mContext.getResources();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 0;

		Bitmap oldBmp = BitmapFactory.decodeResource(res, resId,opts);
		if(oldBmp!=null){
			Bitmap newBmp = Bitmap.createScaledBitmap(oldBmp,newWidth, newHeight, true);
			Drawable drawable = new BitmapDrawable(res, newBmp);
			return drawable;
		}else{
			Drawable drawable = res.getDrawable(resId);
			return drawable;
		}

//		if(oldBmp != null && !oldBmp.isRecycled()){
//			oldBmp.recycle();
//			oldBmp = null;
//		}
//		if(newBmp != null && !newBmp.isRecycled()){
//			newBmp.recycle();
//			newBmp = null;
//		}

	}

	public static int[] areaBigCalculation(Context mContext,int width,int height){
		int[] temp = new int[2];

		//int upDownHeight = ScreenUtils.dipConvertPx(mContext, 2*bigUp);
		int leftRightWidth = width/2;

		int tempWidth = width-leftRightWidth;
		//int tempHeight =  height-upDownHeight;
		//if(tempWidth>=tempHeight){
		//	tempWidth = tempHeight;
		//}else{
		//	tempHeight = tempWidth;
		//}
		int tempHeight =  tempWidth;

		temp[0] = tempWidth;
		temp[1] = tempHeight;

		return temp;
	}

	public static int[] areaBigCalculationPush(Context mContext,int width,int height){
		int[] temp = new int[2];

		int upDownHeight = ScreenUtils.dipConvertPx(mContext, 2*bigUp);
		//int leftRightWidth = ScreenUtils.dipConvertPx(mContext, 2*80);
		int leftRightWidth = width/2;

		int tempWidth = width-leftRightWidth;
		int tempHeight =  height-upDownHeight;
		if(tempWidth>=tempHeight){
			tempWidth = tempHeight;
		}else{
			tempHeight = tempWidth;
		}

		temp[0] = tempWidth;
		temp[1] = tempHeight;

		return temp;
	}

	public static int[] areaBigCalculationPlay(Context mContext,int width,int height){
		int[] temp = new int[2];

		int upDownHeight = ScreenUtils.dipConvertPx(mContext, 2*bigUp);
		//int leftRightWidth = ScreenUtils.dipConvertPx(mContext, 2*80);
		int leftRightWidth = ScreenUtils.dipConvertPx(mContext, 2*smallLeft);

		int tempWidth = width-leftRightWidth;
		int tempHeight =  height-upDownHeight;
		if(tempWidth>=tempHeight){
			tempWidth = tempHeight;
		}else{
			tempHeight = tempWidth;
		}

		temp[0] = tempWidth;
		temp[1] = tempHeight;

		return temp;
	}

	public static int[] areaSmallCalculation(Context mContext,int width,int height){
		int[] temp = new int[2];

		//int upDownHeight = (height-ScreenUtils.dipConvertPx(mContext, 2*smallUpArea))/3;
		int leftRightWidth = width/4-ScreenUtils.dipConvertPx(mContext, 2*smallLeft);
		//if(leftRightWidth>=upDownHeight){
		//	leftRightWidth = upDownHeight;
		//}else{
		//	upDownHeight = leftRightWidth;
		//}
		int upDownHeight = leftRightWidth;

		temp[0] = leftRightWidth;
		temp[1] = upDownHeight;

		return temp;
	}

	public static int[] areaSmallCalculationPush(Context mContext,int width,int height){
		int[] temp = new int[2];

		int upDownHeight = (height-ScreenUtils.dipConvertPx(mContext, 2*smallUpArea))/3;
		int leftRightWidth = width/4-ScreenUtils.dipConvertPx(mContext, 2*smallLeft);
		if(leftRightWidth>=upDownHeight){
			leftRightWidth = upDownHeight;
		}else{
			upDownHeight = leftRightWidth;
		}

		temp[0] = leftRightWidth;
		temp[1] = upDownHeight;

		return temp;
	}

	public static int areaHeightCalculation(Context mContext,int width,int height){
		int[] tempBig = areaBigCalculation(mContext,width,height);
		int[] tempSmall = areaSmallCalculation(mContext,width,height);
		int tempHeight = tempBig[1] + tempSmall[1] + ScreenUtils.dipConvertPx(mContext, 2*smallUpArea);
		return tempHeight;
	}
	*/
}
