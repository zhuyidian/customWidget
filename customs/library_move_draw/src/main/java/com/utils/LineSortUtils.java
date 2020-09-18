package com.utils;

import com.bean.LineSortBean;

import java.util.LinkedList;
import java.util.List;



public class LineSortUtils {
    private List<LineSortBean>verticalList=new LinkedList<>();
    private List<LineSortBean>horizontalList=new LinkedList<>();
    private static class H{
        private static LineSortUtils utils=new LineSortUtils();
    }

    public static LineSortUtils l(){
        return H.utils;
    }

    public  void sort(int xNum, int yNum) {
        verticalList.clear();
        horizontalList.clear();
        for (int i = 0; i < yNum; i++) {
            List<Integer>list=new LinkedList<>();
            for (int j = 0; j < xNum; j++) {
                list.add(xNum*i+j);
            }
            horizontalList.add(new LineSortBean(list));
        }

        for (int i = 0; i < xNum; i++) {
            List<Integer>list=new LinkedList<>();
            for (int k = 0; k < yNum; k++) {
                list.add(xNum*k+i);
            }
            verticalList.add(new LineSortBean(list));
        }

        for (int i = 0; i < verticalList.size(); i++) {
            System.out.println(verticalList.get(i).toString());
        }

        for (int i = 0; i < horizontalList.size(); i++) {
            System.out.println(horizontalList.get(i).toString());
        }
    }
    public static void main(String [] arr){
        LineSortUtils.l().sort(22,18);
    }

    public List<LineSortBean> getVerticalList() {
        return verticalList;
    }

    public List<LineSortBean> getHorizontalList() {
        return horizontalList;
    }
}
