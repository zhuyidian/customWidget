/*
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.wiget.timebar;
public class TimebarTickCriterion {
    /**
     * 整个时间轴的长度(不包括左右两边滑动不到的地方)
     */
    private int viewLength;

    /**
     *可见屏幕上可以显示多少秒
     */
    private int totalSecondsInOneScreen;

    /**
     * 大的时间刻度之间的间隔
     */
    private int keyTickInSecond;

    /**
     * 小的时间刻度的间隔
     */
    private int minTickInSecond;

    /**
     * 如何格式化时间字符串
     */
    private String dataPattern;

    public int getViewLength() {
        return viewLength;
    }

    public void setViewLength(int viewLength) {
        this.viewLength = viewLength;
    }

    public int getTotalSecondsInOneScreen() {
        return totalSecondsInOneScreen;
    }

    public void setTotalSecondsInOneScreen(int totalSecondsInOneScreen) {
        this.totalSecondsInOneScreen = totalSecondsInOneScreen;
    }

    public int getKeyTickInSecond() {
        return keyTickInSecond;
    }

    public void setKeyTickInSecond(int keyTickInSecond) {
        this.keyTickInSecond = keyTickInSecond;
    }

    public int getMinTickInSecond() {
        return minTickInSecond;
    }

    public void setMinTickInSecond(int minTickInSecond) {
        this.minTickInSecond = minTickInSecond;
    }

    public String getDataPattern() {
        return dataPattern;
    }

    public void setDataPattern(String dataPattern) {
        this.dataPattern = dataPattern;
    }
}
