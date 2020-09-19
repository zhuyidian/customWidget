package com.tc.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;


public class FallingBallAnimation extends ImageView {
    private int mTop;
    //当前动画图片索引
    private int mCurImgIndex = 0;
    //动画图片总张数
    private static int mImgCount = 3;

    public FallingBallAnimation(Context context) {
        super(context);
        initView();
    }

    public FallingBallAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FallingBallAnimation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView(){
        setImageDrawable(getResources().getDrawable(R.drawable.fallingball_cicle));
    }

    public void setFallingPos(Point pos){
        layout(pos.x, pos.y, pos.x + getWidth(), pos.y + getHeight());
    }
}
