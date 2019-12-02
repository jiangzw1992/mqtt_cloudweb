package com.lelian.mqtt.pojo;

import java.util.List;

public class RemoteGatewayPojo {
    /**
     * 网关序列号
     */
    private String z;

    private List<RemoteDevicePojo> y;

    public RemoteGatewayPojo(){}

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public List<RemoteDevicePojo> getY() {
        return y;
    }

    public void setY(List<RemoteDevicePojo> y) {
        this.y = y;
    }
}
