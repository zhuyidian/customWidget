package com.tc.animation.Evaluator;

import android.animation.TypeEvaluator;
import android.graphics.Point;


public class FallingBallEvaluator implements TypeEvaluator<Point> {
    private Point point = new Point();
    public Point evaluate(float fraction, Point startValue, Point endValue) {
        if(point==null) return null;
        point.x = (int)(startValue.x + fraction *(endValue.x - startValue.x));

        if (fraction*2<=1){
            point.y = (int)(startValue.y + fraction*2*(endValue.y - startValue.y));
        }else {
            point.y = endValue.y;
        }
        return point;
    }
}
