package com.bean;

import android.graphics.Rect;

public class DrawBean {
    private boolean isDraw;
    private Rect rect;

    public DrawBean(boolean isDraw, Rect rect) {
        this.isDraw = isDraw;
        this.rect = rect;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void setDraw(boolean draw) {
        isDraw = draw;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    @Override
    public String toString() {
        return "DrawBean{" +
                "isDraw=" + isDraw +
                ", rect=" + rect +
                '}';
    }
}
