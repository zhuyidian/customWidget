package com.tc.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;


public class LoadingAnimation extends ImageView {
    private int mTop;
    //当前动画图片索引
    private int mCurImgIndex = 0;
    //动画图片总张数
    private static int mImgCount = 3;

    public LoadingAnimation(Context context) {
        super(context);
        init();
    }

    public LoadingAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingAnimation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mTop = top;
    }

    private void init() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100, 0);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer dx = (Integer) animation.getAnimatedValue();
                setTop(mTop - dx);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
                setImageDrawable(getResources().getDrawable(R.drawable.load_first));
            }

            public void onAnimationRepeat(Animator animation) {
                mCurImgIndex++;
                switch (mCurImgIndex % mImgCount) {
                    case 0:
                        setImageDrawable(getResources().getDrawable(R.drawable.load_first));
                        break;
                    case 1:
                        setImageDrawable(getResources().getDrawable(R.drawable.load_second));
                        break;
                    case 2:
                        setImageDrawable(getResources().getDrawable(R.drawable.load_three));
                        break;
                }
            }

            public void onAnimationEnd(Animator animation) {

            }

            public void onAnimationCancel(Animator animation) {

            }
        });


        valueAnimator.start();
    }
}
