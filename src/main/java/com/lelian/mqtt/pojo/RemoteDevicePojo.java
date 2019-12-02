package com.lelian.mqtt.pojo;

import java.util.List;

public class RemoteDevicePojo {
    /**
     * device_id 默认=1
     */
    private String d;

    /**
     * 数据采集的时间戳
     */
    private String t;

    /**
     * 数据项
     */
    private List<List<Object>> c;

    public RemoteDevicePojo(){}

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public List<List<Object>> getC() {
        return c;
    }

    public void setC(List<List<Object>> c) {
        this.c = c;
    }
}
