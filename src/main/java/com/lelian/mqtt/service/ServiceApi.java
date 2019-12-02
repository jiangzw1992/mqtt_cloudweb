package com.lelian.mqtt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lelian.mqtt.pojo.DevicePojo;
import com.lelian.mqtt.util.Constant;
import com.lelian.mqtt.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceApi {

    private static Logger logger = LoggerFactory.getLogger(ServiceApi.class.getName());

    //token接口
    private static final String TOKEN_API = "/api/token";

    //接口
    private static final String REALTIME_API = "/api/realTime";

    @Value("${api.appKey}")
    private String appKey;

    @Value("${api.appSecret}")
    private String appSecret;

    @Value("${api.host}")
    private String HOST;

    @Autowired
    SLRemoteService slRemoteService;

    /**
     * 定时刷新访问token
     */
    public void refreshAccessToken(){
        logger.info("刷新token开始...");
        String link = HOST+TOKEN_API;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appKey",appKey);
        jsonObject.put("appSecret",appSecret);
        String result = HttpUtil.post(link,jsonObject.toJSONString());
        if(StringUtils.isNotBlank(result)){
            JSONObject resultObject = JSON.parseObject(result);
            JSONObject dataObject = resultObject.getJSONObject("data");
            if(dataObject != null){
                String token = dataObject.getString("token");
                if(StringUtils.isNotBlank(token)){
                    Constant.accessToken = token;
                }
            }
        }
    }

    /**
     * 获取实时数据
     */
    public void getRealTimeData() throws IOException, InterruptedException {
        logger.info("获取温度数据...");
        for(String agreement : SLRemoteService.SLMap.keySet()){
            DevicePojo devicePojo = SLRemoteService.SLMap.get(agreement);
            String deviceId = devicePojo.getDeviceId();
            String apiKey = devicePojo.getApiKey();
            String deviceTemp = getDeviceTemp(deviceId,apiKey);
            slRemoteService.handle(devicePojo,deviceTemp);
        }

    }

    public String getDeviceTemp(String deviceId,String apiKey){
        String url2 = String.format("http://api.heclouds.com/devices/%s/datastreams", deviceId);
        Map<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);
        String response = HttpUtil.get(url2, headers);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(response)) {
            JSONObject jsonObject1 = JSONObject.parseObject(response);
            if (jsonObject1.getJSONArray("data") != null && !jsonObject1.getJSONArray("data").isEmpty()) {
                for (Object object : jsonObject1.getJSONArray("data")) {
                    if (object instanceof JSONObject) {
                        JSONObject infoObject = (JSONObject) object;
                        if ("Temp".equals(infoObject.getString("id"))) {
                            return infoObject.getString("current_value");
                        }
                    }
                }
            }
        }
        return "0";
    }


}
