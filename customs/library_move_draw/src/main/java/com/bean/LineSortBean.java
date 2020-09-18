package com.bean;

import java.util.List;

public class LineSortBean {
    private List<Integer> list;

    public LineSortBean(List<Integer> list) {
        this.list = list;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "LineSortBean{" +
                "list=" + list +
                '}';
    }
}
