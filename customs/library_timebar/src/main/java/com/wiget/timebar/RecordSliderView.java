/*
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.wiget.timebar;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.tc.timebar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/*
 * 编写：wenlong on 2017/6/20 14:15
 * 企业QQ： 2853239883
 * 钉钉：13430330686
 */
public class RecordSliderView extends View {
    /**
     * 指示在当前刻度标准中，多少像素对应于1秒
     */
    private float pixelsPerSecond = 0;

    /**
     * Timebar Action 监听
     */
    private OnBarMoveListener mOnBarMoveListener;
    private OnBarScaledListener mOnBarScaledListener;

    /**
     * 屏幕宽高
     */
    private int screenWidth, screenHeight;

    /**
     * 背景画笔
     */
    Paint timebarPaint = new Paint();

    /**
     * 时间文本画笔
     */
    TextPaint keyTickTextPaint = new TextPaint();

    /**
     * 时间之间的间隔长度 dp
     */
    private final int TICK_TEXT_TO_TICK_MARGIN_IN_DP = 2;

    /**
     * 整个View 的高度 in dp
     */
    private final int VIEW_HEIGHT_IN_DP = 56;

    /**
     * 高度 dp
     */
    private final int COLORED_RECORDBAR_HEIGHT_IN_DP = 21;

    /**
     * 根据时间间隔而定义的时间文本字体大小  sp
     */
    private final int KEY_TICK_TEXT_SIZE_IN_SP = 10;

    /**
     * 时间间隔的最大高度 dp
     */
    private final int BIG_TICK_HEIGHT_IN_DP = 9;

    /**
     * 时间间隔的最小高度 dp
     */
    private final int SMALL_TICK_HEIGHT_IN_DP = 6;

    /**
     * 大时间间隔宽度的一半 dp
     */
    private final int BIG_TICK_HALF_WIDTH_IN_DP = 1;

    /**
     * 小时间间隔宽度的一半 dp
     */
    private final int SMALL_TICK_HALF_WIDTH_IN_DP = 1;

    /**
     * 刻度和时间文本的间隔 dp
     */
    private final int COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN_IN_DP = 5;

    /**
     * 白色游标
     */
    private final int MIDDLE_CURSOR_BITMAP_SCALED_WIDTH_IN_DP = 13;


    private final int BIG_TICK_HALF_WIDTH = DeviceUtil.dip2px(BIG_TICK_HALF_WIDTH_IN_DP);
    private final int BIG_TICK_HEIGHT = DeviceUtil.dip2px(BIG_TICK_HEIGHT_IN_DP);
    private final int SMALL_TICK_HALF_WIDTH = DeviceUtil.dip2px(SMALL_TICK_HALF_WIDTH_IN_DP);
    private final int SMALL_TICK_HEIGHT = DeviceUtil.dip2px(SMALL_TICK_HEIGHT_IN_DP);
    private final int KEY_TICK_TEXT_SIZE = DeviceUtil.dip2px(KEY_TICK_TEXT_SIZE_IN_SP);
    private final int TICK_TEXT_TO_TICK_MARGIN = DeviceUtil.dip2px(TICK_TEXT_TO_TICK_MARGIN_IN_DP);
    private final int VIEW_HEIGHT = DeviceUtil.dip2px(VIEW_HEIGHT_IN_DP);
    private final int COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN = DeviceUtil.dip2px(COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN_IN_DP);
    private final int COLORED_RECORDBAR_HEIGHT = DeviceUtil.dip2px(COLORED_RECORDBAR_HEIGHT_IN_DP);

    /**
     * 白色光标位于中间显示当前时间的btimap
     */
    private Bitmap middle_cursor_bitmap_original_size = BitmapFactory.decodeResource(getResources(),
            R.drawable.ic_video_middle);
    private Bitmap middle_cursor_bitmap = Bitmap.createScaledBitmap(middle_cursor_bitmap_original_size,
            DeviceUtil.dip2px(MIDDLE_CURSOR_BITMAP_SCALED_WIDTH_IN_DP),
            VIEW_HEIGHT,
            false);

    /**
     * 设置游标是否可见
     */
    private boolean middleCursorVisible = true;

    /**
     * 图缓存5标度准则
     */
    private Map<Integer, TimebarTickCriterion> timebarTickCriterionMap = new HashMap<>();

    /**
     * 比例等级规格数
     */
    private int timebarTickCriterionCount = 3;

    /**
     * 根据timebarTickCriterionMap的key 标明现在用的是哪个比例的时间刻度
     */
    private int currentTimebarTickCriterionIndex=1;

    /**
     * 存储报警时间段的数据 .
     */
    private List<RecordAlarmTimeSegment> recordDataExistTimeClipsList = new ArrayList<>();

    /**
     * 记录数据中的数据存在时间片断列表被分成不同的组并在这个地图中缓存
     * 同一组的同一节段
     */
    private Map<Long, List<RecordAlarmTimeSegment>> recordDataExistTimeClipsListMap = new HashMap<>();


    private ScaleGestureDetector scaleGestureDetector;

    /**
     * 指示此视图的宽度是否已初始化
     */
    private boolean notInited = true;


    private long currentTimeInMillisecond;


    private long mostLeftTimeInMillisecond;


    private long mostRightTimeInMillisecond;


    private long screenLeftTimeInMillisecond;


    private long screenRightTimeInMillisecond;


    private boolean justScaledByPressingButton = false;


    public final static int SECONDS_PER_DAY = 24 * 60 * 60;


    private long WHOLE_TIMEBAR_TOTAL_SECONDS;


    private int AFTER_SCALE_TEMP_WIDTH=0;

    public RecordSliderView(Context context) {
        super(context);
        init(null);
    }

    public RecordSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public RecordSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public List<RecordAlarmTimeSegment> getRecordDataExistTimeClipsList() {
        return recordDataExistTimeClipsList;
    }

    public void setRecordDataExistTimeClipsList(List<RecordAlarmTimeSegment> recordDataExistTimeClipsList) {
        this.recordDataExistTimeClipsList = recordDataExistTimeClipsList;
        arrangeRecordDataExistTimeClipsIntoMap(recordDataExistTimeClipsList);
    }


    public long getMostLeftTimeInMillisecond() {
        return mostLeftTimeInMillisecond;
    }


    public long getMostRightTimeInMillisecond() {
        return mostRightTimeInMillisecond;
    }

    public long getScreenLeftTimeInMillisecond() {
        screenLeftTimeInMillisecond = (long) (getCurrentTimeInMillisecond() - (long) ((float) screenWidth * 1000f / 2f / pixelsPerSecond));

        return screenLeftTimeInMillisecond;
    }

    public long getScreenRightTimeInMillisecond() {
        screenRightTimeInMillisecond = (long) (getCurrentTimeInMillisecond() + (long) (screenWidth * 1000f / 2f / pixelsPerSecond));
        return screenRightTimeInMillisecond;
    }

    private void arrangeRecordDataExistTimeClipsIntoMap(List<RecordAlarmTimeSegment> clipsList) {
        recordDataExistTimeClipsListMap = new HashMap<>();

        if (clipsList != null) {
            for (RecordAlarmTimeSegment clipItem : clipsList) {
                for (Long dateZeroOClockItem : clipItem.getCoverDateZeroOClockList()) {
                    List<RecordAlarmTimeSegment> list = null;
                    if ((list = recordDataExistTimeClipsListMap.get(dateZeroOClockItem)) == null) {
                        list = new ArrayList<>();
                        recordDataExistTimeClipsListMap.put(dateZeroOClockItem, list);
                    }
                    list.add(clipItem);
                }

            }
        }

        invalidate();
    }


    public void initTimebarLengthAndPosition(long mostLeftTime, long mostRightTime, long currentTime) {
        this.mostLeftTimeInMillisecond = mostLeftTime;
        this.mostRightTimeInMillisecond = mostRightTime;
        this.currentTimeInMillisecond = currentTime;
        WHOLE_TIMEBAR_TOTAL_SECONDS = (mostRightTime - mostLeftTime) / 1000;
        initTimebarTickCriterionMap();
        resetToStandardWidth();
    }

    public int getCurrentTimebarTickCriterionIndex() {
        return currentTimebarTickCriterionIndex;
    }

    public void setCurrentTimebarTickCriterionIndex(int currentTimebarTickCriterionIndex) {
        this.currentTimebarTickCriterionIndex = currentTimebarTickCriterionIndex;
    }

    private void init(AttributeSet attrs) {
        //获取屏幕宽高
        screenWidth = DeviceUtil.getScreenResolution(getContext())[0];
        screenHeight = DeviceUtil.getScreenResolution(getContext())[1];

        //  默认用第4中刻度显示刻度
        // 默认游标设置在3小时前
        // 默认右边的时间结束点设置为当前时间
        currentTimeInMillisecond = System.currentTimeMillis() - 3 * 3600 * 1000;
        mostRightTimeInMillisecond = currentTimeInMillisecond + 3 * 3600 * 1000;

        // 默认整体的时间宽度是7天的时间
        mostLeftTimeInMillisecond = mostRightTimeInMillisecond - 7 * 24 * 3600 * 1000;
        WHOLE_TIMEBAR_TOTAL_SECONDS = (mostRightTimeInMillisecond - mostLeftTimeInMillisecond) / 1000;

        pixelsPerSecond = (float) (getWidth() - screenWidth) / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;

        initTimebarTickCriterionMap();
        setCurrentTimebarTickCriterionIndex(1);

        keyTickTextPaint.setTextSize(KEY_TICK_TEXT_SIZE);
        keyTickTextPaint.setColor(getContext().getResources().getColor(R.color.colorWhite));

        /*GestureDetector.SimpleOnGestureListener gestureDetectorListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int top = getTop();

                int left = (int) (getLeft() - (int) (e2.getX() - e1.getX()));
                int right = left + getWidth();


                if (left >= 0) {
                    left = 0;
                    right = getWidth();
                }

                if (right < screenWidth) {
                    right = screenWidth;
                    left = right - getWidth();
                }

                layout(left, top, right, top + getHeight());

                int deltaX = (0 - left);  //绉诲姩鐨勬椂闂村搴�

                int timeBarLength = getWidth() - screenWidth;
                currentTimeInMillisecond = mostLeftTimeInMillisecond + deltaX * WHOLE_TIMEBAR_TOTAL_SECONDS * 1000 / timeBarLength;
                invalidate();

                if (mOnBarMoveListener != null) {
                    mOnBarMoveListener.onBarMove(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
                }

                return true;
            }
        };
        */

        ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleTimebarByFactor(detector.getScaleFactor(), false);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                justScaledByPressingButton = true;
            }


        };
        scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleGestureListener);
        //gestureDetector = new GestureDetector(getContext(), gestureDetectorListener);
    }


    public void scaleTimebarByFactor(float scaleFactor, boolean scaleByClickButton) {
        int newWidth = (int) ((getWidth() - screenWidth) * scaleFactor);

        if (newWidth > timebarTickCriterionMap.get(0).getViewLength()) {
            setCurrentTimebarTickCriterionIndex(0);
            newWidth = timebarTickCriterionMap.get(0).getViewLength();

        } else if (newWidth < timebarTickCriterionMap.get(0).getViewLength()
                && newWidth >= getAverageWidthForTwoCriterion(0, 1)) {
            setCurrentTimebarTickCriterionIndex(0);

        } else if (newWidth < getAverageWidthForTwoCriterion(0, 1)
                && newWidth >= getAverageWidthForTwoCriterion(1, 2)) {
            setCurrentTimebarTickCriterionIndex(1);

        } else if (newWidth < getAverageWidthForTwoCriterion(1, 2)
                && newWidth >= timebarTickCriterionMap.get(2).getViewLength()) {
            setCurrentTimebarTickCriterionIndex(2);

        } else if (newWidth < timebarTickCriterionMap.get(2).getViewLength()) {
            setCurrentTimebarTickCriterionIndex(2);
            newWidth = timebarTickCriterionMap.get(2).getViewLength();

        }


//        else if (newWidth < getAverageWidthForTwoCriterion(1, 2)
//                && newWidth >= getAverageWidthForTwoCriterion(2, 3)) {
//            setCurrentTimebarTickCriterionIndex(2);
//
//        }
//        else if (newWidth < getAverageWidthForTwoCriterion(2, 3)
//                && newWidth >= timebarTickCriterionMap.get(3).getViewLength()) {
//            setCurrentTimebarTickCriterionIndex(3);
//
//        }else if (newWidth < timebarTickCriterionMap.get(3).getViewLength()) {
//            setCurrentTimebarTickCriterionIndex(3);
//            newWidth = timebarTickCriterionMap.get(3).getViewLength();
//
//        }

//        else if (newWidth < getAverageWidthForTwoCriterion(3, 4)
//                && newWidth >= timebarTickCriterionMap.get(4).getViewLength()) {
//            setCurrentTimebarTickCriterionIndex(4);
//
//        } else if (newWidth < timebarTickCriterionMap.get(4).getViewLength()) {
//            setCurrentTimebarTickCriterionIndex(4);
//            newWidth = timebarTickCriterionMap.get(4).getViewLength();
//
//        }

        if (scaleByClickButton) {
            justScaledByPressingButton = true;
        }


        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = newWidth;
        AFTER_SCALE_TEMP_WIDTH=newWidth;
        setLayoutParams(params);

    }

    private float getAverageWidthForTwoCriterion(int criterion1Index, int criterion2Index) {
        int width1 = timebarTickCriterionMap.get(criterion1Index).getViewLength();
        int width2 = timebarTickCriterionMap.get(criterion2Index).getViewLength();
        return (width1 + width2) / 2;
    }


    private void initTimebarTickCriterionMap() {
        TimebarTickCriterion t0 = new TimebarTickCriterion();
        t0.setTotalSecondsInOneScreen(10 * 60);
        t0.setKeyTickInSecond(1 * 60);
        t0.setMinTickInSecond(6);
        t0.setDataPattern("HH:mm");
        t0.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t0.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(0, t0);

        TimebarTickCriterion t1 = new TimebarTickCriterion();
        t1.setTotalSecondsInOneScreen(60 * 60);
        t1.setKeyTickInSecond(10 * 60);
        t1.setMinTickInSecond(60);
        t1.setDataPattern("HH:mm");
        t1.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t1.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(1, t1);

        TimebarTickCriterion t2 = new TimebarTickCriterion();
        t2.setTotalSecondsInOneScreen(6 * 60 * 60);
        t2.setKeyTickInSecond(60 * 60);
        t2.setMinTickInSecond(5 * 60);
        t2.setDataPattern("HH:mm");
        t2.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t2.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(2, t2);

        TimebarTickCriterion t3 = new TimebarTickCriterion();
        t3.setTotalSecondsInOneScreen(18 * 60 * 60);
        t3.setKeyTickInSecond(3 * 60 * 60);
        t3.setMinTickInSecond(15 * 60);
        t3.setDataPattern("MM-dd HH:mm");
        t3.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t3.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(3, t3);

//        TimebarTickCriterion t4 = new TimebarTickCriterion();
//        t4.setTotalSecondsInOneScreen(6 * 24 * 60 * 60);
//        t4.setKeyTickInSecond(24 * 60 * 60);
//        t4.setMinTickInSecond(2 * 60 * 60);
//        t4.setDataPattern("MM.dd");
//        // t4.dataPattern = "MM.dd HH:mm:ss";
//        t4.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t4.getTotalSecondsInOneScreen()));
        //     timebarTickCriterionMap.put(4, t4);
        timebarTickCriterionCount = timebarTickCriterionMap.size();
    }

    private void resetToStandardWidth() {
        //  setCurrentTimebarTickCriterionIndex(1);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getViewLength();
        setLayoutParams(params);
    }


    public long getCurrentTimeInMillisecond() {
        return currentTimeInMillisecond;
    }
    /**
     * 将当前时间光标设置为特定时间点
     * 你应该调用这个方法每分钟更新显示当前时间。
     */
    public void setCurrentTimeInMillisecond(long currentTimeInMillisecond) {
        this.currentTimeInMillisecond = currentTimeInMillisecond;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), VIEW_HEIGHT);

        if (justScaledByPressingButton && mOnBarScaledListener != null) {
            justScaledByPressingButton = false;
            mOnBarScaledListener.onBarScaleFinish(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int measureMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSize = MeasureSpec.getSize(widthMeasureSpec);
        int result = getSuggestedMinimumWidth();
        switch (measureMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = measureSize + screenWidth;
                pixelsPerSecond = measureSize / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;
                if (mOnBarScaledListener != null) {
                    mOnBarScaledListener.onBarScaled(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
                }
                break;
            default:
                break;
        }

        return result;
    }


    private String getTimeStringFromLong(long value) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getDataPattern());
        return timeFormat.format(value);
    }


    public void setMiddleCursorVisible(boolean middleCursorVisible) {
        this.middleCursorVisible = middleCursorVisible;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (notInited) {
            notInited = false;
            resetToStandardWidth();
            return;
        }

        pixelsPerSecond = (float) (getWidth() - screenWidth) / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;

        //获得手机的本地时区与UTC之间的毫秒数
        Calendar cal = Calendar.getInstance();
        int zoneOffsetInSeconds = cal.get(Calendar.ZONE_OFFSET) / 1000;
        long forStartUTC = (long) (currentTimeInMillisecond / 1000 - screenWidth / pixelsPerSecond / 2 - timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond());
        long forEndUTC = (long) (currentTimeInMillisecond / 1000 + screenWidth / pixelsPerSecond / 2 + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond());

        long forStartLocalTimezone = forStartUTC + zoneOffsetInSeconds;
        long forEndLocalTimezone = forEndUTC + zoneOffsetInSeconds;

        long firstTickToSeeInSecondUTC = -1;
        for (long i = forStartLocalTimezone; i <= forEndLocalTimezone; i++) {
            if (i % timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() == 0) {
                firstTickToSeeInSecondUTC = i - zoneOffsetInSeconds;
                break;
            }
        }
























        //画出灰色的背景
        long startDrawTimeInSeconds = firstTickToSeeInSecondUTC + (-20) * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond();
        float startX = pixelsPerSecond * (startDrawTimeInSeconds - mostLeftTimeInMillisecond / 1000) + screenWidth / 2f;
        RectF cloudRecordTimeClipsBarBackgroundRectF = new RectF(startX,
                getHeight() - BIG_TICK_HEIGHT - TICK_TEXT_TO_TICK_MARGIN - KEY_TICK_TEXT_SIZE - COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN - COLORED_RECORDBAR_HEIGHT,
                (startX + screenWidth + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() * 40 * pixelsPerSecond),
                getHeight() - BIG_TICK_HEIGHT - TICK_TEXT_TO_TICK_MARGIN - KEY_TICK_TEXT_SIZE - COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN
        );
        timebarPaint.setColor(getContext().getResources().getColor(R.color.colorGrayTransparent));//colorGrayTransparent
        timebarPaint.setStyle(Paint.Style.FILL);
        //取消了上半边灰色背景
        //    canvas.drawRect(cloudRecordTimeClipsBarBackgroundRectF, timebarPaint);


        if (recordDataExistTimeClipsList != null && recordDataExistTimeClipsList.size() > 0) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat zeroTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDrawTimeDateString = dateFormat.format(startDrawTimeInSeconds * 1000);
            String zeroTimeString = startDrawTimeDateString + " 00:00:00";

            long screenLastSecondToSee = (long) (startDrawTimeInSeconds + screenWidth / pixelsPerSecond + 30 * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()) * 1000L;

            Date startDate;
            try {
                startDate = zeroTimeFormat.parse(zeroTimeString);
                List<RecordAlarmTimeSegment> startList = recordDataExistTimeClipsListMap.get(startDate.getTime());
                if (startList == null) {
                    int afterFindDays = 1;
                    long findTimeInMilliseconds = startDate.getTime();
                    long newFindStartMilliseconds = findTimeInMilliseconds;
                    while (startList == null && newFindStartMilliseconds < screenLastSecondToSee) {
                        newFindStartMilliseconds = findTimeInMilliseconds + (long) SECONDS_PER_DAY * 1000L * (long) afterFindDays;
                        startList = recordDataExistTimeClipsListMap.get(newFindStartMilliseconds);
                        afterFindDays++;
                    }
                }

                if (startList != null && startList.size() > 0) {
                    int thisDateFirstClipStartIndex = recordDataExistTimeClipsList.indexOf(startList.get(0));

                   /*
                    int firstClipToDrawIndex = -1;
                    for (int i = thisDateFirstClipStartIndex; i < recordDataExistTimeClipsList.size(); i++) {
                        if (recordDataExistTimeClipsList.get(i).endTimeInMillisecond < startDrawTimeInSeconds * 1000) {
                            continue;
                        } else {
                            firstClipToDrawIndex = i;
                            break;
                        }
                    }*/


                    //The first record segment should be drawn is found. Now let's start to draw colorful record segments!
                    long endDrawTimeInSeconds = (long) (startDrawTimeInSeconds
                            + screenWidth / pixelsPerSecond
                            + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() * 30);

                    timebarPaint.setColor(getContext().getResources().getColor(R.color.colorRecordGreen));
                    timebarPaint.setStyle(Paint.Style.FILL);

                    for (int i = thisDateFirstClipStartIndex; i < recordDataExistTimeClipsList.size(); i++) {
                        float leftX = pixelsPerSecond * (recordDataExistTimeClipsList.get(i).getStartTimeInMillisecond() - mostLeftTimeInMillisecond) / 1000 + screenWidth / 2f;
                        float rightX = pixelsPerSecond * (recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond() - mostLeftTimeInMillisecond) / 1000 + screenWidth / 2f;
                        RectF rectF = new RectF(leftX,
                                0 //getHeight() - BIG_TICK_HEIGHT - TICK_TEXT_TO_TICK_MARGIN - KEY_TICK_TEXT_SIZE - COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN - COLORED_RECORDBAR_HEIGHT
                                ,
                                rightX,
                                getHeight()// - BIG_TICK_HEIGHT - TICK_TEXT_TO_TICK_MARGIN - KEY_TICK_TEXT_SIZE - COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN
                        );
                        canvas.drawRect(rectF, timebarPaint);
                        if (recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond() >= endDrawTimeInSeconds * 1000) {
                            break;
                        }
                    }
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
















        /**
         * 画timebar背景，大时间点，小的时间小，和对应时间点的文字
         */
        int totalTickToDrawInOneScreen = (int) (screenWidth / pixelsPerSecond / timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()) + 2;
        float keytextY = getHeight() - BIG_TICK_HEIGHT - TICK_TEXT_TO_TICK_MARGIN;
        for (int i = -20; i <= totalTickToDrawInOneScreen + 10; i++) {
            long drawTickTimeInSecondUTC = firstTickToSeeInSecondUTC + i * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond();
            long drawTickTimeInSecondLocalTimezone = drawTickTimeInSecondUTC + zoneOffsetInSeconds;

            if (drawTickTimeInSecondLocalTimezone % timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getKeyTickInSecond() == 0) {
                //画大的时间刻度
                timebarPaint.setColor(getContext().getResources().getColor(R.color.colorgrey));//color_33ffffff
                timebarPaint.setStyle(Paint.Style.FILL);
                float startX2 = pixelsPerSecond * (drawTickTimeInSecondUTC - mostLeftTimeInMillisecond / 1000) + screenWidth / 2f;
                RectF largeTickRect = new RectF(startX2 - BIG_TICK_HALF_WIDTH / 2,
                        DeviceUtil.dip2px(10)// getHeight() - BIG_TICK_HEIGHT-50
                        , (startX2 + BIG_TICK_HALF_WIDTH / 2),
                        getHeight()-DeviceUtil.dip2px(12));
                canvas.drawRect(largeTickRect, timebarPaint);
////////////////////////////////////////////
                //时间文字
                String keytext = getTimeStringFromLong(drawTickTimeInSecondUTC * 1000);
                float keyTextWidth = keyTickTextPaint.measureText(keytext);
                float keytextX = startX2 - keyTextWidth / 2;


                canvas.drawText(keytext,
                        keytextX,
                        keytextY+DeviceUtil.dip2px(10),
                        keyTickTextPaint);
            } else if (drawTickTimeInSecondLocalTimezone % timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() == 0) {
                //画小的时间刻度
                timebarPaint.setColor(getContext().getResources().getColor(R.color.colorgrey));//color_33ffffff
                timebarPaint.setStyle(Paint.Style.FILL);
                float startX1 = pixelsPerSecond * (drawTickTimeInSecondUTC - mostLeftTimeInMillisecond / 1000) + screenWidth / 2f;
                RectF smallTickRect = new RectF(startX1 - SMALL_TICK_HALF_WIDTH / 2,
                        DeviceUtil.dip2px(15)//getHeight() - BIG_TICK_HEIGHT-35
                        , (startX1 + SMALL_TICK_HALF_WIDTH / 2),
                        getHeight()-DeviceUtil.dip2px(12));
                canvas.drawRect(smallTickRect, timebarPaint);
            }
        }









        /**
         * 在屏幕中间绘制指示当前时间的白光标位图
         */
        if (middleCursorVisible) {
            timebarPaint.setColor(getContext().getResources().getColor(R.color.colorRecordGreen));
            timebarPaint.setStyle(Paint.Style.FILL);
            canvas.drawBitmap(middle_cursor_bitmap,
                    (currentTimeInMillisecond / 1000L - mostLeftTimeInMillisecond / 1000L) * pixelsPerSecond + screenWidth / 2f - middle_cursor_bitmap.getWidth() / 2,
                    0,
                    timebarPaint);
        }


        /**
         * 根据 currenttimeinmillisecond 可变布局视图，绘画在适当的位置
         */
        layout((int) (0 - (currentTimeInMillisecond - mostLeftTimeInMillisecond) / 1000 * pixelsPerSecond),
                getTop(),
                getWidth() - (int) ((currentTimeInMillisecond - mostLeftTimeInMillisecond) / 1000 * pixelsPerSecond),
                getTop() + getHeight());
















    }


    float lastX, lastY;


    private int mode = NONE;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleGestureDetector.onTouchEvent(event);

        if (scaleGestureDetector.isInProgress()) {
            return true;
        }


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {

                } else if (mode == DRAG) {
                    int dx = (int) (event.getRawX() - lastX);
                    int dy = (int) (event.getRawY() - lastY);

                    if (dx == 0) {
                        return true;
                    }

                    int top = getTop();
                    int left = getLeft() + dx;
                    int right = left + getWidth();

                    if (left >= 0) {
                        left = 0;
                        right = getWidth();
                    }

                    if (right < screenWidth) {
                        right = screenWidth;
                        left = right - getWidth();
                    }

                    layout(left, top, right, top + getHeight());
                    invalidate();

                    lastX = event.getRawX();
                    lastY = event.getRawY();

                    //pixels to move
                    int deltaX = (0 - left);
                    int timeBarLength = getWidth() - screenWidth;
                    currentTimeInMillisecond = mostLeftTimeInMillisecond + deltaX * WHOLE_TIMEBAR_TOTAL_SECONDS * 1000 / timeBarLength;

                    if (mOnBarMoveListener != null) {
                        mOnBarMoveListener.onBarMove(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                if (mode == DRAG) {
                    int deltaX_up = (0 - getLeft());
                    int timeBarLength_up = getWidth() - screenWidth;
                    currentTimeInMillisecond = mostLeftTimeInMillisecond + deltaX_up * WHOLE_TIMEBAR_TOTAL_SECONDS * 1000 / timeBarLength_up;

                    if (mOnBarMoveListener != null) {
                        mOnBarMoveListener.OnBarMoveFinish(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
                    }

                }
                mode = NONE;
                break;
        }


        return true;
    }


    public void scaleByPressingButton(boolean zoomIn) {

        int currentCriterionViewLength = timebarTickCriterionMap.get(getCurrentTimebarTickCriterionIndex()).getViewLength();//褰撳墠鎵�鍦ㄥ埢搴︽爣鍑嗙殑榛樿闀垮害锛堜笉鍚袱绔┖鍑虹殑screenWidth锛�


        int currentViewLength = getWidth() - screenWidth;

        if (currentViewLength == currentCriterionViewLength) {
            if (zoomIn) {//zoom in
                int newCriteriaIndex = getCurrentTimebarTickCriterionIndex() - 1;
                if (newCriteriaIndex < 0) {
                    return;
                } else {
                    setCurrentTimebarTickCriterionIndex(newCriteriaIndex);
                    int newWidth = timebarTickCriterionMap.get(newCriteriaIndex).getViewLength();
                    justScaledByPressingButton = true;

                    ViewGroup.LayoutParams params = getLayoutParams();
                    params.width = newWidth;
                    setLayoutParams(params);
                }
            } else {//zoom out

                int newCriteriaIndex = getCurrentTimebarTickCriterionIndex() + 1;
                if (newCriteriaIndex >= timebarTickCriterionCount) {
                    return;
                } else {
                    setCurrentTimebarTickCriterionIndex(newCriteriaIndex);
                    int newWidth = timebarTickCriterionMap.get(newCriteriaIndex).getViewLength();
                    justScaledByPressingButton = true;

                    ViewGroup.LayoutParams params = getLayoutParams();
                    params.width = newWidth;
                    setLayoutParams(params);
                }
            }
        } else {
            if (currentViewLength > currentCriterionViewLength) {//currentViewLength > currentCriterionViewLength

                if (zoomIn) {
                    int newCriteriaIndex = getCurrentTimebarTickCriterionIndex() - 1;
                    if (newCriteriaIndex < 0) {
                        return;
                    } else {
                        setCurrentTimebarTickCriterionIndex(newCriteriaIndex);
                        int newWidth = timebarTickCriterionMap.get(newCriteriaIndex).getViewLength();
                        justScaledByPressingButton = true;

                        ViewGroup.LayoutParams params = getLayoutParams();
                        params.width = newWidth;
                        setLayoutParams(params);
                    }
                } else {
                    int newWidth = timebarTickCriterionMap.get(getCurrentTimebarTickCriterionIndex()).getViewLength();
                    justScaledByPressingButton = true;

                    ViewGroup.LayoutParams params = getLayoutParams();
                    params.width = newWidth;
                    setLayoutParams(params);
                }

            } else {

                if (zoomIn) {
                    int newWidth = timebarTickCriterionMap.get(getCurrentTimebarTickCriterionIndex()).getViewLength();
                    justScaledByPressingButton = true;

                    ViewGroup.LayoutParams params = getLayoutParams();
                    params.width = newWidth;
                    setLayoutParams(params);


                } else {
                    int newCriteriaIndex = getCurrentTimebarTickCriterionIndex() + 1;
                    if (newCriteriaIndex >= timebarTickCriterionCount) {
                        return;
                    } else {
                        setCurrentTimebarTickCriterionIndex(newCriteriaIndex);
                        int newWidth = timebarTickCriterionMap.get(newCriteriaIndex).getViewLength();
                        justScaledByPressingButton = true;

                        ViewGroup.LayoutParams params = getLayoutParams();
                        params.width = newWidth;
                        setLayoutParams(params);
                    }
                }

            }
        }


    }


    public interface OnBarMoveListener {

        void onBarMove(long screenLeftTime, long screenRightTime, long currentTime);

        void OnBarMoveFinish(long screenLeftTime, long screenRightTime, long currentTime);
    }

    public void setOnBarMoveListener(OnBarMoveListener onBarMoveListener) {
        mOnBarMoveListener = onBarMoveListener;
    }


    public interface OnBarScaledListener {

        void onBarScaled(long screenLeftTime, long screenRightTime, long currentTime);

        void onBarScaleFinish(long screenLeftTime, long screenRightTime, long currentTime);
    }

    public void setOnBarScaledListener(OnBarScaledListener onBarScaledListener) {
        mOnBarScaledListener = onBarScaledListener;
    }



    public void setScaleWidth(){
        if (AFTER_SCALE_TEMP_WIDTH!=0){
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = AFTER_SCALE_TEMP_WIDTH;
            setLayoutParams(params);
            postInvalidate();
        }
    }
    public int getScaleWidth(){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = AFTER_SCALE_TEMP_WIDTH;
        return  params.width;
    }
    public void print(){
        //LogTools.barPrint((getMostRightTimeInMillisecond()-getMostLeftTimeInMillisecond()),(getScreenRightTimeInMillisecond()-getScreenLeftTimeInMillisecond()), currentTimebarTickCriterionIndex,getScaleWidth());
    }
}

