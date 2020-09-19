/*
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.wiget.timebar;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordAlarmTimeSegment implements Serializable {

    private static long mostLeftDayZeroTime = Long.MAX_VALUE;


    private static long mostRightDayZeroTime = -1;


    private long startTimeInMillisecond;

    private long endTimeInMillisecond;


    private long tempStartTimeMillisecond;

    /**
     * {“2015-11-26 00:00:00”、“2015-11-27 00:00:00”、“2015-11-28 00:00:00”、“2015-11-29 00:00:00”}
     */
    private List<Long> coverDateZeroOClockList = new ArrayList<>();

    public RecordAlarmTimeSegment(long startTimeInMillisecond, long endTimeInMillisecond) {
        this.startTimeInMillisecond = startTimeInMillisecond;
        this.endTimeInMillisecond = endTimeInMillisecond;

        if (startTimeInMillisecond < mostLeftDayZeroTime) {
            this.mostLeftDayZeroTime = startTimeInMillisecond;
        }

        if (endTimeInMillisecond > mostRightDayZeroTime) {
            this.mostRightDayZeroTime = endTimeInMillisecond;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat zeroTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startTimeDateString = dateFormat.format(startTimeInMillisecond);
        String startTimeZeroTimeString = startTimeDateString + " 00:00:00";

        String endTimeDateString = dateFormat.format(endTimeInMillisecond);
        String endTimeZeroTimeString = endTimeDateString + " 00:00:00";

        try {
            Date startTimeZeroDate = zeroTimeFormat.parse(startTimeZeroTimeString);
            Date endTimeZeroDate = zeroTimeFormat.parse(endTimeZeroTimeString);

            long loopZeroDateInMilliseconds = startTimeZeroDate.getTime();
            while (loopZeroDateInMilliseconds <= endTimeZeroDate.getTime()) {
                coverDateZeroOClockList.add(loopZeroDateInMilliseconds);
                loopZeroDateInMilliseconds = loopZeroDateInMilliseconds + RecordSliderView.SECONDS_PER_DAY * 1000;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public long getStartTimeInMillisecond() {
        return startTimeInMillisecond;
    }

    public long getEndTimeInMillisecond() {
        return endTimeInMillisecond;
    }

    public List<Long> getCoverDateZeroOClockList() {
        return coverDateZeroOClockList;
    }

    public void setStartTimeInMillisecond(long startTimeInMillisecond) {
        this.startTimeInMillisecond = startTimeInMillisecond;
    }

    public void setEndTimeInMillisecond(long endTimeInMillisecond) {
        this.endTimeInMillisecond = endTimeInMillisecond;
    }

    public long getTempStartTimeMillisecond() {
        return tempStartTimeMillisecond;
    }

    public void setTempStartTimeMillisecond(long tempStartTimeMillisecond) {
        this.tempStartTimeMillisecond = tempStartTimeMillisecond;
    }

    @Override
    public String toString() {
        return "RecordAlarmTimeSegment{" +
                "startTimeInMillisecond=" + startTimeInMillisecond +
                ", endTimeInMillisecond=" + endTimeInMillisecond +
                ", coverDateZeroOClockList=" + coverDateZeroOClockList +
                '}';
    }
}