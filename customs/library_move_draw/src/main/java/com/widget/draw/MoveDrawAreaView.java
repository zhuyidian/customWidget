package com.widget.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bean.DrawBean;
import com.bean.LineSortBean;
import com.tc.draw.R;
import com.utils.LineSortUtils;
import com.utils.RxOnFinishListenner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;




public class MoveDrawAreaView extends View {


    private Paint paint;
    private Paint linePaint;
    private int xNumber = 22;
    private int yNumber = 18;
    private int rectWidth = 0;
    private int rectHeight = 0;
    private List<DrawBean> drawBeanList = new ArrayList<>();
    private List<DrawBean>  drawBeanList_Cache = new ArrayList<>();
    private List<DrawBean>  cleanBeanList_Cache = new ArrayList<>();
    private Type type = Type.Normal;

    public MoveDrawAreaView(Context context) {
        super(context);
        init();
    }

    public MoveDrawAreaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //初始化同行同列position
        LineSortUtils.l().sort(xNumber, yNumber);
        rectWidth = getWidth() / xNumber;
        rectHeight = getHeight() / yNumber;
        paint = new Paint();
        linePaint = new Paint();
        paint.setColor(getResources().getColor(R.color.select));
        linePaint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        linePaint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        linePaint.setStrokeWidth(1);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    //public Rect(int left, int top, int right, int bottom)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawXYLine(canvas);
        if (rectWidth == 0) {
            rectWidth = getWidth() / xNumber;
            rectHeight = getHeight() / yNumber;
            for (int iY = 0; iY < yNumber; iY++) {
                for (int iX = 0; iX < xNumber; iX++) {
                    drawBeanList.add(iY * xNumber + iX, new DrawBean(false, new Rect(iX * rectWidth, iY * rectHeight, (iX + 1) * rectWidth, (iY + 1) * rectHeight)));
                    drawBeanList_Cache.add(iY * xNumber + iX, new DrawBean(false, new Rect(iX * rectWidth, iY * rectHeight, (iX + 1) * rectWidth, (iY + 1) * rectHeight)));
                    cleanBeanList_Cache.add(iY * xNumber + iX, new DrawBean(true, new Rect(iX * rectWidth, iY * rectHeight, (iX + 1) * rectWidth, (iY + 1) * rectHeight)));

                }
            }
            drawXYLine(canvas);
        }
    }

    private void drawXYLine(Canvas canvas) {
        canvas.drawLine(0, 1, 0, getHeight() - 1, paint);
        canvas.drawLine(getWidth() - 1, 1, getWidth() - 1, getHeight() - 1, linePaint);
        canvas.drawLine(1, 1, getWidth() - 1, 1, paint);
        canvas.drawLine(1, getHeight() - 1, getWidth() - 1, getHeight() - 1, linePaint);
        for (int i = 0; i < xNumber; i++) {
            canvas.drawLine(i * rectWidth, 0, i * rectWidth, getHeight(), linePaint);
        }
        for (int i = 0; i < yNumber; i++) {
            canvas.drawLine(0, i * rectHeight, getWidth(), i * rectHeight, linePaint);

        }
        for (int i = 0; i < drawBeanList.size(); i++) {
            DrawBean bean = drawBeanList.get(i);
          //  LogTools.print("drawCommitArea:DRAW:" + (i));
            if (bean.isDraw()) {
                canvas.drawRect(bean.getRect(), paint);
            }
        }
    }

    int xDown =0;
    int yDown = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                  xDown = (int) event.getX();
                  yDown = (int) event.getY();
                int poX = (int) xDown / rectWidth;
                int poY = (int) yDown / rectHeight;
                dealWithPoint(poX, poY, type);
                break;

            case MotionEvent.ACTION_MOVE:
                int xDownMove = (int) event.getX();
                int yDownMove = (int) event.getY();
                int poXMove = (int) xDownMove / rectWidth;
                int poYMove = (int) yDownMove / rectHeight;
                dealWithPoint(poXMove, poYMove, type);
                break;

            case MotionEvent.ACTION_UP:
                if (type.equals(Type.Darw)){
                    for (int i = 0; i < 5; i++) {
                        wiseDealDraw(i);
                    }
                    invalidate();
                }else {
                    int xUp=(int) event.getX();
                    int yUp = (int) event.getY();
                    wiseDealClean(1,(Math.abs(yUp-yDown)>Math.abs(xUp-xDown)));
                    invalidate();
                }
               if (onChangeListenner!=null){
                   onChangeListenner.onFinish(isChange());
               }
                break;
        }
        return true;

    }

    private void wiseDealDraw(int fixedTime) {
       // LogTools.print("wiseDeal==1");
        //1，获取每行超过2个的行，并补充中间空白
        List<LineSortBean> horizontalList = LineSortUtils.l().getHorizontalList();
        for (int i = 0; i < horizontalList.size(); i++) {
            List<Integer> list = horizontalList.get(i).getList();
            int drawNum = 0;
            List<Integer> drawList = new LinkedList<>();
            for (int j = 0; j < list.size(); j++) {
                if (drawBeanList_Cache.get(list.get(j)).isDraw()) {
                 //   LogTools.print("wiseDeal==2");
                    drawList.add(list.get(j));
                    drawNum++;
                }
            }

            if (drawNum >= 2) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j) >= drawList.get(0) && list.get(j) <= drawList.get(drawList.size() - 1)) {
                      //  LogTools.print("wiseDeal==3");
                        drawBeanList.get(list.get(j)).setDraw(true);
                      //  LogTools.print("darwview:自动补全："+j);
                    }
                }
            }
        }
        //2，获取每列超过啊2个的列，并补充中间空白

        List<LineSortBean> verticalList = LineSortUtils.l().getVerticalList();
        for (int i = 0; i < verticalList.size(); i++) {
            List<Integer> list = verticalList.get(i).getList();
            int drawNum = 0;
            List<Integer> drawList = new LinkedList<>();
            for (int j = 0; j < list.size(); j++) {
                if (drawBeanList_Cache.get(list.get(j)).isDraw()) {
                   // LogTools.print("wiseDeal==4");
                    drawList.add(list.get(j));
                    drawNum++;
                }
            }

            if (drawNum >= 2) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j) >= drawList.get(0) && list.get(j) <= drawList.get(drawList.size() - 1)) {
                     //   LogTools.print("wiseDeal==5");
                        drawBeanList.get(list.get(j)).setDraw(true);
                    }
                }
            }
        }
        //3，步骤1和步骤2重复几次
        if (fixedTime==4){
            for (int i = 0; i < drawBeanList_Cache.size(); i++) {
                drawBeanList_Cache.get(i).setDraw(false);
            }
        }
    }
    private void wiseDealClean(int fixedTime,boolean isVertical) {
        if (isVertical){
            //2，获取每列超过啊2个的列，并补充中间空白
            List<LineSortBean> verticalList = LineSortUtils.l().getVerticalList();
            for (int i = 0; i < verticalList.size(); i++) {
                List<Integer> list = verticalList.get(i).getList();
                int drawNum = 0;
                List<Integer> drawList = new LinkedList<>();
                for (int j = 0; j < list.size(); j++) {
                    if (!cleanBeanList_Cache.get(list.get(j)).isDraw()) {
                        //LogTools.print("wiseDeal==4");
                        drawList.add(list.get(j));
                        drawNum++;
                    }
                }

                if (drawNum >= 2) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j) >= drawList.get(0) && list.get(j) <= drawList.get(drawList.size() - 1)) {
                            //LogTools.print("wiseDeal==5");
                            drawBeanList.get(list.get(j)).setDraw(false);
                        }
                    }
                }
            }
        }else {
            //1，获取每行超过2个的行，并补充中间空白
            List<LineSortBean> horizontalList = LineSortUtils.l().getHorizontalList();
            for (int i = 0; i < horizontalList.size(); i++) {
                List<Integer> list = horizontalList.get(i).getList();
                int drawNum = 0;
                List<Integer> drawList = new LinkedList<>();
                for (int j = 0; j < list.size(); j++) {
                    if (!cleanBeanList_Cache.get(list.get(j)).isDraw()) {
                      //  LogTools.print("wiseDeal==2");
                        drawList.add(list.get(j));
                        drawNum++;
                    }
                }

                if (drawNum >= 2) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j) >= drawList.get(0) && list.get(j) <= drawList.get(drawList.size() - 1)) {
                        //    LogTools.print("wiseDeal==3");
                            drawBeanList.get(list.get(j)).setDraw(false);
                          //  LogTools.print("darwview:自动补全："+j);
                        }
                    }
                }
            }
        }
        //3，步骤1和步骤2重复几次
            for (int i = 0; i < cleanBeanList_Cache.size(); i++) {
                cleanBeanList_Cache.get(i).setDraw(true);
            }

    }
    private void dealWithPoint(int poX, int poY, Type type) {
        if (type.equals(Type.Normal)) {
            return;
        }
        if ((poY * xNumber + poX) >= drawBeanList.size() || poX * rectWidth >= getWidth()) {
            return;
        }
        if (type.equals(Type.Darw) && ((poY * xNumber + poX) >= 0)) {
            drawBeanList.get(poY * xNumber + poX).setDraw(true);
            drawBeanList_Cache.get(poY * xNumber + poX).setDraw(true);
        } else if (type.equals(Type.Clean) && (poY * xNumber + poX >= 0)) {
            drawBeanList.get(poY * xNumber + poX).setDraw(false);
            cleanBeanList_Cache.get(poY * xNumber + poX).setDraw(false);
        }
        invalidate();
    }

    public enum Type {
        Darw,
        Clean,
        Normal
    }

    public void setWidthNumX(int xNumber) {
        this.xNumber = xNumber;
    }

    public void setHeighNumY(int yNumber) {
        this.yNumber = yNumber;
    }

    public void redraw() {
        rectWidth = 0;
        rectHeight = 0;
        drawBeanList.clear();
        drawBeanList_Cache.clear();
        cleanBeanList_Cache.clear();
        invalidate();
    }

    public void getDrawData(RxOnFinishListenner<List<String>> call) {
        StringBuilder builder = new StringBuilder();
        result = new ArrayList<>();
        for (int i = 0; i < drawBeanList.size(); i++) {
            if (drawBeanList.get(i).isDraw()) {
                builder.append("0");
            } else {
                builder.append("1");
            }
            if (i % 22 == 21) {
                String reverseString = new StringBuffer(builder.toString()).toString();
                int num10 = Integer.valueOf(reverseString, 2);
                // String num16= Integer.toHexString(num10);
               // LogTools.print("移动侦测区域 提交：" + "indext:" + (int) (i / 22) + ",二进制:" + reverseString + "---十进制:" + num10);
                result.add("" + num10);
                builder = new StringBuilder();
            }
        }
        call.onFinish(result);
    }
    public  List<String>  getDrawData( ) {
        StringBuilder builder = new StringBuilder();
        result = new ArrayList<>();
        for (int i = 0; i < drawBeanList.size(); i++) {
            if (drawBeanList.get(i).isDraw()) {
                builder.append("0");
            } else {
                builder.append("1");
            }
            if (i % 22 == 21) {
                String reverseString = new StringBuffer(builder.toString()).toString();
                int num10 = Integer.valueOf(reverseString, 2);
                // String num16= Integer.toHexString(num10);
              //  LogTools.print("移动侦测区域 提交：" + "indext:" + (int) (i / 22) + ",二进制:" + reverseString + "---十进制:" + num10);
                result.add("" + num10);
                builder = new StringBuilder();
            }
        }
        return result;
    }
    private List<Integer>listTemp=new ArrayList<>();
    public void drawArea(List<Integer> list) {
        listTemp.clear();
        listTemp.addAll(list);
       // LogTools.print("drawArea:" + list.size());
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String line = Integer.toBinaryString(list.get(i));
            if (line.length() < 22) {
                int add = (22 - line.length());
                StringBuilder builder = new StringBuilder();
                for (int addi = 0; addi < add; addi++) {
                    builder.append("0");
                }
                line = builder.toString() + line;
            }
           // LogTools.print("移动侦测区域 获取：" + "indext:" + i + ",二进制:" + line + "---十进制:" + list.get(i));
            String reverseString = new StringBuffer(line).toString();
            lineList.add(i, reverseString);
        }

        drawCommitArea(lineList);
    }

    private List<String> result = null;

    private void drawCommitArea(List<String> areaList) {
        if (areaList.size() != yNumber) return;
        for (int i = 0; i < yNumber; i++) {
            String singleLine = areaList.get(i);
            for (int m = 0; m < singleLine.length(); m++) {
               // LogTools.print("drawCommitArea:" + (i * xNumber + m));
                if ((singleLine.charAt(m) + "").equals("1")) {
                    if ((i * xNumber + m)<drawBeanList.size()){
                        drawBeanList.get(i * xNumber + m).setDraw(false);
                    }

                } else if ((singleLine.charAt(m) + "").equals("0")) {
                    if ((i * xNumber + m)<drawBeanList.size()){
                        drawBeanList.get(i * xNumber + m).setDraw(true);
                    }
                }
            }
          //  LogTools.print("drawBeanList1:" );
            postInvalidate();
        }
    }

    public boolean isChange(){

         List<String> stringList=getDrawData();
         StringBuilder sb=new StringBuilder();
        for (int i = 0; i < stringList.size(); i++) {
            sb.append(stringList.get(i)+"-");
        }
        StringBuilder sb2=new StringBuilder();
        for (int i = 0; i < listTemp.size(); i++) {
            sb2.append(listTemp.get(i)+"-");
        }
       // LogTools.print("drawBeanList1:"+sb.toString());
      //  LogTools.print("drawBeanList2:"+sb2.toString());
        if (sb.toString().equals(sb2.toString())){
            return false;
        }
        return true;
    }

    private RxOnFinishListenner<Boolean> onChangeListenner;

    public void setOnChangeListenner(RxOnFinishListenner<Boolean> onChangeListenner) {
        this.onChangeListenner = onChangeListenner;
    }
}
