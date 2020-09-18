package com.wiget.light;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wiget.LocaleUtils;

public class LightView extends View {
    private static final int BG_COLOR = 0xffd9e5f5;
    private static final int UN_SELECT_COLOR = 0xffa2aebc;
    private static final int SELECT_COLOR = 0xff407bc7;
    private static final int BG_TOP_COLOR = 0xffdbe7f5;
    private static final int BG_OOUTTER_COLOR = 0xffeef3f9;
    private Paint bgPaint;
    private Paint realPaint_left;
    private Paint realPaint_bottom;
    private Paint realPaint_right;
    private Paint topPaint;
    private Paint outerPaint;
    private Paint whitePaint;
    //饼状画笔
    private Paint mTextPaint;
    // 文字画笔
    private static final int DEFAULT_RADIUS = 200;
    private int mRadius = DEFAULT_RADIUS;
    // 外圆的半径
    private String centerTitle;
    // 中间标题    super(context);
    private int compareHeight = 0;
    private int compareWidth = 0;
    private float startAngle = (float) 39.5;
    private float sweepAngle = 101;
    private boolean buttonleftOn = true;
    private boolean buttonbottomOn = true;
    private boolean buttonrightOn = true;
    private int areaProgress = 4;

    public LightView(Context context) {
        // super(context);
        this(context, null, 0);
    }

    public LightView(Context context, @Nullable AttributeSet attrs) {
        // super(context, attrs);
        this(context, attrs, 0);
    }


    public LightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        bgPaint = new Paint();

        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(BG_COLOR);

        realPaint_left = new Paint();
        realPaint_left.setAntiAlias(true);
        realPaint_left.setStyle(Paint.Style.FILL);
        realPaint_left.setColor(SELECT_COLOR);

        realPaint_bottom = new Paint();
        realPaint_bottom.setAntiAlias(true);
        realPaint_bottom.setStyle(Paint.Style.FILL);
        realPaint_bottom.setColor(SELECT_COLOR);

        realPaint_right = new Paint();
        realPaint_right.setAntiAlias(true);
        realPaint_right.setStyle(Paint.Style.FILL);
        realPaint_right.setColor(SELECT_COLOR);

        topPaint = new Paint();
        topPaint.setAntiAlias(true);
        topPaint.setStyle(Paint.Style.FILL);
        topPaint.setColor(BG_TOP_COLOR);


        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(36);


        outerPaint = new Paint();
        outerPaint.setAntiAlias(true);
        outerPaint.setStyle(Paint.Style.FILL);
        outerPaint.setColor(BG_OOUTTER_COLOR);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(38);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        bgPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(SELECT_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        initSize();

        //  canvas.drawLine(getWidth()/2,getHeight()/4,getWidth()/2,getHeight()*3/4,realPaint);
        RectF bgRecttop = new RectF(0, -getHeight() * 3 / 4, getHeight(), getWidth() * 3 / 4);


        canvas.drawArc(bgRecttop, startAngle, sweepAngle, true, outerPaint);

        canvas.drawArc(bgRecttop, 75, 30, true, topPaint);


        RectF bgRectF = new RectF(rectf_bg_left, rectf_bg_top, rectf_bg_right, rectf_bg_bottom);


        canvas.drawArc(bgRectF, -30, 60, true, bgPaint);
        canvas.drawArc(bgRectF, 30, 120, true, bgPaint);
        canvas.drawArc(bgRectF, 150, 60, true, bgPaint);

        RectF realRectF = new RectF(rectf_bg_left + compareWidth, rectf_bg_top + compareHeight, rectf_bg_right - compareWidth, rectf_bg_bottom - compareHeight);
        canvas.drawArc(realRectF, -30, 60, true, realPaint_right);
        canvas.drawArc(realRectF, 30, 120, true, realPaint_bottom);
        canvas.drawArc(realRectF, 150, 60, true, realPaint_left);

        if (LocaleUtils.isChinese(getContext())) {

            canvas.drawCircle(getWidth() / 4, getHeight() * 5 / 8 - DeviceUtil.dip2px(10), DeviceUtil.dip2px(10), whitePaint);
            int r = DeviceUtil.dip2px(10);
            canvas.drawCircle(getWidth() / 2, getHeight() * 7 / 8 - DeviceUtil.dip2px(10), r, whitePaint);
            canvas.drawCircle(getWidth() * 3 / 4, getHeight() * 5 / 8 - DeviceUtil.dip2px(10), DeviceUtil.dip2px(10), whitePaint);


        } else {
            canvas.drawCircle(getWidth() / 4, getHeight() * 5 / 8 - DeviceUtil.dip2px(10), DeviceUtil.dip2px(10), whitePaint);
            int r = DeviceUtil.dip2px(10);
            // canvas.drawCircle(getWidth() / 2, getHeight() * 7 / 8 - DeviceUtil.dip2px(10), r, whitePaint);
            //int centerLeft=
            int centerX = getWidth() / 2;
            int centerY = getHeight() * 7 / 8 - DeviceUtil.dip2px(10);

            canvas.drawRect(centerX - r, centerY - r, centerX + r, centerY + r, whitePaint);
            canvas.drawCircle((getWidth() / 2) - r, getHeight() * 7 / 8 - DeviceUtil.dip2px(10), r, whitePaint);
            canvas.drawCircle((getWidth() / 2) + r, getHeight() * 7 / 8 - DeviceUtil.dip2px(10), r, whitePaint);
            canvas.drawCircle(getWidth() * 3 / 4, getHeight() * 5 / 8 - DeviceUtil.dip2px(10), DeviceUtil.dip2px(10), whitePaint);
        }


        if (LocaleUtils.isChinese(getContext())) {
            canvas.drawText("右", getWidth() / 4, getHeight() * 5 / 8 - DeviceUtil.dip2px(6), mTextPaint);
            canvas.drawText("中", getWidth() / 2, getHeight() * 7 / 8 - DeviceUtil.dip2px(6), mTextPaint);
            canvas.drawText("左", getWidth() * 3 / 4, getHeight() * 5 / 8 - DeviceUtil.dip2px(6), mTextPaint);
        } else {
            canvas.drawText("R", getWidth() / 4, getHeight() * 5 / 8 - DeviceUtil.dip2px(7), mTextPaint);
            canvas.drawText("Center", getWidth() / 2, getHeight() * 7 / 8 - DeviceUtil.dip2px(7), mTextPaint);
            canvas.drawText("L", getWidth() * 3 / 4, getHeight() * 5 / 8 - DeviceUtil.dip2px(7), mTextPaint);
        }


        float cutWidthOn = whitePaint.measureText("ON") / 2;
        float cutWidthOff = whitePaint.measureText("OFF") / 2;
        if (buttonleftOn) {
            canvas.drawText("ON", getWidth() / 4 - cutWidthOn, getHeight() * 5 / 8 + DeviceUtil.dip2px(14), whitePaint);
        } else {
            canvas.drawText("OFF", getWidth() / 4 - cutWidthOn, getHeight() * 5 / 8 + DeviceUtil.dip2px(14), whitePaint);
        }

        if (buttonbottomOn) {
            canvas.drawText("ON", getWidth() / 2 - cutWidthOn, getHeight() * 7 / 8 + DeviceUtil.dip2px(14), whitePaint);
        } else {
            canvas.drawText("OFF", getWidth() / 2 - cutWidthOn, getHeight() * 7 / 8 + DeviceUtil.dip2px(14), whitePaint);
        }

        if (buttonrightOn) {
            canvas.drawText("ON", getWidth() * 3 / 4 - cutWidthOn, getHeight() * 5 / 8 + DeviceUtil.dip2px(14), whitePaint);
        } else {
            canvas.drawText("OFF", getWidth() * 3 / 4 - cutWidthOn, getHeight() * 5 / 8 + DeviceUtil.dip2px(14), whitePaint);
        }


    }

    private int rectf_bg_left = 0;
    private int rectf_bg_top = 0;
    private int rectf_bg_right = 0;
    private int rectf_bg_bottom = 0;
    private int centerXpoint = 0;
    private int centerYpoint = 0;
    private int radius = 0;

    private void initSize() {
        int height = getHeight();
        int widht = getWidth();

        rectf_bg_left = DeviceUtil.dip2px(20);
        rectf_bg_top = height / 4 + DeviceUtil.dip2px(15);
        rectf_bg_right = widht - DeviceUtil.dip2px(20);
        rectf_bg_bottom = height - DeviceUtil.dip2px(15);

        centerXpoint = widht / 2;
        centerYpoint = rectf_bg_top + (rectf_bg_bottom - rectf_bg_top) / 2;
        radius = centerXpoint - rectf_bg_left;
        //LogTools.print("原点：" + "(" + centerXpoint + "," + centerYpoint + ") radius:" + radius);


    }

    public void reSize(int progress) {
        areaProgress = progress;
        int realprogress = 0;
        if (progress == 0) {
            realprogress = 60;
            startAngle = (float) 69.5;
            sweepAngle = 42;
        } else if (progress == 1) {
            realprogress = 40;
            startAngle = (float) 58;
            sweepAngle = 64;
        } else if (progress == 2) {
            realprogress = 30;
            startAngle = (float) 52.5;
            sweepAngle = 75;
        } else if (progress == 3) {
            realprogress = 20;
            startAngle = (float) 47.3;
            sweepAngle = (float) 85.5;
        } else if (progress == 4) {
            realprogress = 0;
            startAngle = (float) 39.5;
            sweepAngle = 101;
        }
        compareWidth = DeviceUtil.dip2px(realprogress * 4 / 3);
        compareHeight = DeviceUtil.dip2px(realprogress);
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int xDown = (int) event.getX();
                int yDown = (int) event.getY();
                //  justArea(xDown, yDown);
                justAreaInMath(xDown, yDown);
                break;

            case MotionEvent.ACTION_UP:
                int xDownw = (int) event.getX();
                int yDownw = (int) event.getY();
             //   LogTools.print("原点2：" + "(" + xDownw + "," + yDownw + ")");
                break;
        }
        return true;
    }

    private void justAreaInMath(int xDown, int yDown) {
        if (xDown > centerXpoint) {
            //右边区域
           // LogTools.print("new way===:右边区域...");
            onWitchButton(xDown, centerXpoint, yDown, false);
        } else if (xDown < centerXpoint) {
            //左边区域
          //  LogTools.print("new way===:左边区域...");
            onWitchButton(centerXpoint, xDown, yDown, true);
        }
    }

    public void onWitchButton(int xDown, int xDown2, int yDown, boolean isFelt) {
        //LogTools.print("new way===:xDown:" + xDown + "__xDown2:" + xDown2 + "__yDown:" + yDown + "__isFelt:" + isFelt);
        int xTemp = xDown - xDown2;
       // LogTools.print("new way===:xTemp:" + xTemp);
        int yTemp = Math.abs((int) ((xTemp / 0.866025403) * 0.5));
      //  LogTools.print("new way===:yTemp:" + yTemp);
        int topRang = centerYpoint - yTemp;
        int bottomRang = centerYpoint + yTemp;
       // LogTools.print("new way===:topRang:" + topRang + "______bottomRang:" + bottomRang);
        if (yDown > topRang && yDown < bottomRang) {
            //3
            if (isFelt && (isLeftAreaInside(xDown, yDown, radius))) {
               // LogTools.print("new way===:isFelt...");
                resetStatus(!buttonleftOn, buttonbottomOn, buttonrightOn);
            } else if (isRightAreaInside(xDown, yDown, radius)) {
               // LogTools.print("new way===:isRight...");
                resetStatus(buttonleftOn, buttonbottomOn, !buttonrightOn);
            }
        } else if ((yDown > bottomRang) && (isBottomAreaInside(xDown, yDown, (rectf_bg_bottom - centerYpoint)))) {
            //2
           // LogTools.print("new way===:buttonbottomOn...");
            resetStatus(buttonleftOn, !buttonbottomOn, buttonrightOn);
        }
    }

    public boolean isLeftAreaInside(int xDown, int yDown, int radiusTemp) {
        int total = getXforDistant(xDown) * getYforDistant(yDown);
       // LogTools.print("new way===:isLeftAreaInside..." + total + "___" + radiusTemp * radiusTemp);
        return (total < (radiusTemp * radiusTemp));
    }

    public boolean isRightAreaInside(int xDown, int yDown, int radiusTemp) {
        int total = getXforDistant(xDown) * getYforDistant(yDown);
       // LogTools.print("new way===:isRightAreaInside..." + total + "___" + radiusTemp * radiusTemp);
        return (total < (radiusTemp * radiusTemp));
    }

    public boolean isBottomAreaInside(int xDown, int yDown, int radiusTemp) {
        int total = getXforDistant(xDown) * getYforDistant(yDown);
       // LogTools.print("new way===:isBottomAreaInside..." + total + "___" + radiusTemp * radiusTemp);
        return (total < (radiusTemp * radiusTemp));
    }

    private int getXforDistant(int xDown) {
        if (xDown > centerXpoint) {
            return xDown - centerXpoint;
        } else {
            return centerXpoint - xDown;
        }
    }

    private int getYforDistant(int yDown) {
        if (yDown > centerYpoint) {
            return yDown - centerYpoint;
        } else {
            return centerXpoint - yDown;
        }
    }

    private void justArea(int xDown, int yDown) {
        int gaxwidth = DeviceUtil.dip2px(10);
        int xCenter1 = getWidth() / 4;
        int yCenter1 = getHeight() * 5 / 8 - gaxwidth;
        int xCenter2 = getWidth() / 2;
        int yCenter2 = getHeight() * 7 / 8 - gaxwidth;
        int xCenter3 = getWidth() * 3 / 4;
        int yCenter3 = getHeight() * 5 / 8 - gaxwidth;


        if (xDown > (xCenter1 - gaxwidth) && xDown < (xCenter1 + gaxwidth) && yDown > (yCenter1 - gaxwidth) && yDown < (yCenter1 + gaxwidth)) {
            resetStatus(!buttonleftOn, buttonbottomOn, buttonrightOn);
        } else if (xDown > (xCenter2 - gaxwidth) && xDown < (xCenter2 + gaxwidth) && yDown > (yCenter2 - gaxwidth) && yDown < (yCenter2 + gaxwidth)) {
            resetStatus(buttonleftOn, !buttonbottomOn, buttonrightOn);
        } else if (xDown > (xCenter3 - gaxwidth) && xDown < (xCenter3 + gaxwidth) && yDown > (yCenter3 - gaxwidth) && yDown < (yCenter3 + gaxwidth)) {
            resetStatus(buttonleftOn, buttonbottomOn, !buttonrightOn);
        }
    }
//    public void initStatus(boolean leftOn, boolean bottomOn, boolean rightOn){
//        resetStatus(bottomOn,  rightOn,leftOn) ;
//    }

    public void setStatus(String pirArry) {
        if (pirArry.equals("0")) {
            resetStatus(false, false, false);
        } else if (pirArry.equals("1")) {
            resetStatus(false, true, false);
        } else if (pirArry.equals("2")) {
            resetStatus(true, false, false);
        } else if (pirArry.equals("3")) {
            resetStatus(true, true, false);
        } else if (pirArry.equals("4")) {
            resetStatus(false, false, true);
        } else if (pirArry.equals("5")) {
            resetStatus(false, true, true);
        } else if (pirArry.equals("6")) {
            resetStatus(true, false, true);
        } else if (pirArry.equals("7")) {
            resetStatus(true, true, true);
        }
    }

     private void resetStatus(boolean leftOn, boolean bottomOn, boolean rightOn) {
        buttonleftOn = leftOn;
        buttonbottomOn = bottomOn;
        buttonrightOn = rightOn;
        if (buttonleftOn) {
            realPaint_left.setColor(SELECT_COLOR);
        } else {
            realPaint_left.setColor(UN_SELECT_COLOR);
        }

        if (buttonbottomOn) {
            realPaint_bottom.setColor(SELECT_COLOR);
        } else {
            realPaint_bottom.setColor(UN_SELECT_COLOR);
        }

        if (buttonrightOn) {
            realPaint_right.setColor(SELECT_COLOR);
        } else {
            realPaint_right.setColor(UN_SELECT_COLOR);
        }
        postInvalidate();
    }

    private boolean isButtonleftOn() {
        return buttonleftOn;
    }

    private boolean isButtonbottomOn() {
        return buttonbottomOn;
    }

    private boolean isButtonrightOn() {
        return buttonrightOn;
    }

    public int getAreaProgress() {
        return areaProgress;
    }

    public String getPrray(){
        int c_1=isButtonleftOn()?2:0;
        int c_2=isButtonbottomOn()?1:0;
        int c_3=isButtonrightOn()?4:0;
        return (c_1+c_2+c_3)+"";
    }
}
