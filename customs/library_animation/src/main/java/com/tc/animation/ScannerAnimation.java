package com.tc.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class ScannerAnimation extends RelativeLayout {
    private Context mContext;
    private ImageView circle1,circle2,circle3,circle4;
    private Animation animation1,animation2,animation3,animation4;

    public ScannerAnimation(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.scan_layout, this, true);

        initView();
        initListenner();
    }
    public ScannerAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.scan_layout, this, true);

        initView();
        initListenner();
    }
    private void initView() {
        circle1 = (ImageView)findViewById(R.id.circle1);
        circle2 = (ImageView)findViewById(R.id.circle2);
        circle3 = (ImageView)findViewById(R.id.circle3);
        circle4 = (ImageView)findViewById(R.id.circle4);

        animation1 = AnimationUtils.loadAnimation(mContext,R.anim.scale_alpha_anim);
        animation2 = AnimationUtils.loadAnimation(mContext,R.anim.scale_alpha_anim);
        animation3 = AnimationUtils.loadAnimation(mContext,R.anim.scale_alpha_anim);
        animation4 = AnimationUtils.loadAnimation(mContext,R.anim.scale_alpha_anim);
    }
    private void initListenner() {
        findViewById(R.id.start_can).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                circle1.startAnimation(animation1);

                animation2.setStartOffset(600);
                circle2.startAnimation(animation2);

                animation3.setStartOffset(1200);
                circle3.startAnimation(animation3);

                animation4.setStartOffset(1800);
                circle4.startAnimation(animation4);
            }
        });
    }
}
