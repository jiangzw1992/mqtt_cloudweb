package com.lelian.mqtt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lelian.mqtt.pojo.DevicePojo;
import com.lelian.mqtt.pojo.RemoteDevicePojo;
import com.lelian.mqtt.pojo.RemoteGatewayPojo;
import com.lelian.mqtt.util.HttpUtil;
import com.lelian.mqtt.util.ThreadPoolUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SLRemoteService {

    @Autowired
    ServiceApi serviceApi;

    @Autowired
    DataService dataService;

    private static Logger logger = LoggerFactory.getLogger(SLRemoteService.class.getName());

    /**
     * 协议-设备
     */
    public static Map<String,DevicePojo> SLMap = new HashMap<>();

    /**
     * MQTT 相关参数
     */
    private static  final String Broker = "tcp://112.126.98.10:1883";
    private static  final int Qos = 2;
    private static  final boolean isRetained = false;
    private static  final boolean isCleanSession = true;

    /**
     *  记录设备上传数据的最新时间, key: 设备序列号，value：最新时间(毫秒)
     */
    private static Map<String, Long> DeviceTimeMap = new ConcurrentHashMap<>(1024);

    /*
     * 用于保存每个设备对应的mqtt连接，key：设备序列号，value：mqtt连接
     */
    private static Map<String, MqttClient> mqttClientMap = new HashMap<>(1024);

    /**
     *  初始化SLMap
     */
    public void initSLMap(){
        try {
            String jsonString = getConfigContent();
            JSONObject devicesObject = JSONObject.parseObject(jsonString);
            if(devicesObject != null){
                JSONArray deviceArray = devicesObject.getJSONArray("devices");
                for(int i=0;i<deviceArray.size();i++){
                    String agreement = deviceArray.getString(i);
                    //网关序列号
                    String serialNumber = agreement.split(":")[0];
                    int deviceNumber = 1024 * Integer.parseInt(serialNumber) + 1;
                    String sn = agreement.split(":")[1];
                    DevicePojo devicePojo = new DevicePojo();
                    devicePojo.setDeviceId(agreement);
                    devicePojo.setSn(sn);
                    devicePojo.setSerialNumber(serialNumber);
                    getDeviceId(devicePojo);
                    SLMap.put(agreement, devicePojo);
                    //初始化数据库数据
                    dataService.addAgent(Integer.parseInt(serialNumber));
                    Integer modelid = dataService.addModel();
                    if(modelid != null){
                        dataService.addDevice(Integer.parseInt(serialNumber),deviceNumber,modelid);
                    }
                    dataService.addDataitem(deviceNumber);
                }
            }
        } catch (Exception e) {
            logger.error("initSLMap error",e);
        }
    }

    public static void getDeviceId(DevicePojo devicePojo){
        String url = String.format("http://sz-fountainhead.cn:6012/service.aspx?sn=%s", devicePojo.getSn());
        String result = HttpUtil.get(url);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String deviceId = jsonObject.getString("device_id");
            String apiKey = jsonObject.getString("api_key");
            devicePojo.setDeviceId(deviceId);
            devicePojo.setApiKey(apiKey);
        }
    }

    private static String getConfigContent() throws IOException {
        InputStream inputStream = SLRemoteService.class.getResourceAsStream("/config.json");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    /**
     * MQTT发送数据
     */
    private static class MqttOperationHandler extends Thread {

        private RemoteGatewayPojo gatewayPojo;

        public MqttOperationHandler(RemoteGatewayPojo gatewayPojo) {
            this.gatewayPojo = gatewayPojo;
        }

        @Override
        public void run() {
            List<RemoteDevicePojo> remoteDevicePojos = this.gatewayPojo.getY();
            Iterator<RemoteDevicePojo> deviceItr = remoteDevicePojos.iterator();
            while (deviceItr.hasNext()) {
                RemoteDevicePojo remoteDevicePojo = deviceItr.next();
                // 移除gateway中没有数据项的设备
                List<List<Object>> items = remoteDevicePojo.getC();
                if ((items == null) || (items.size() == 0)) {
                    deviceItr.remove();
                    continue;
                }
            }

            if(remoteDevicePojos.size() == 0) {
                return;
            }

            RemoteGatewayPojo remoteGatewayPojo = this.gatewayPojo;

            // 序列号
            String serialNumber = remoteGatewayPojo.getZ();
            //  要发送给mqtt服务器的json数据
            String msgJson = null;
            try {
                JSONObject pojoObject = (JSONObject)JSON.toJSON(remoteGatewayPojo);
                msgJson = "[31]" + pojoObject.toJSONString();
                logger.info("正在发送数据 : "+msgJson);
                MqttMessage mqttMessage = new MqttMessage(msgJson.getBytes());
                mqttMessage.setQos(Qos);
                mqttMessage.setRetained(isRetained);
                String topic = String.format("/at/%s/[31]", serialNumber);

                MqttClient mqttClient = SLRemoteService.mqttClientMap.get(serialNumber);
                if((mqttClient == null) || (!mqttClient.isConnected())) {
                    String clientId = "sl_" + serialNumber;
                    mqttClient = new MqttClient(Broker, clientId, new MemoryPersistence());
                    MqttConnectOptions connOpts = new MqttConnectOptions();
                    connOpts.setCleanSession(isCleanSession);
                    //connOpts.setMaxInflight(150);
                    mqttClient.connect(connOpts);
                    SLRemoteService.mqttClientMap.put(serialNumber, mqttClient);
                }

                mqttClient.publish(topic, mqttMessage);
                DeviceTimeMap.put(serialNumber, System.currentTimeMillis());
                logger.info("发送数据结束..");
            }catch(Exception e) {
                logger.error("mqtt error:", e);
            }

        }
    }

    public void handle(DevicePojo devicePojo,String deviceTemp) throws IOException {
        RemoteGatewayPojo remoteGatewayPojo = parseJson(devicePojo,deviceTemp);
        ThreadPoolUtil.executorService.execute(new MqttOperationHandler(remoteGatewayPojo));
    }

    /**
     * 解析从客户端传来的json数据, 并且返回指定格式的数组
     * @param
     * @return
     */
    private static RemoteGatewayPojo parseJson(DevicePojo devicePojo,String deviceTemp) throws IOException {
        String serialNumber = devicePojo.getSerialNumber();

        // 创建RemoteGatewayPojo
        RemoteGatewayPojo remoteGatewayPojo = new RemoteGatewayPojo();

        remoteGatewayPojo.setZ(serialNumber);
        List<RemoteDevicePojo> devicePojos = new ArrayList<>();
        remoteGatewayPojo.setY(devicePojos);

        RemoteDevicePojo remoteDevicePojo = new RemoteDevicePojo();
        devicePojos.add(remoteDevicePojo);
        remoteDevicePojo.setD("1");
        remoteDevicePojo.setT((System.currentTimeMillis() + ""));
        List<List<Object>> remoteItems = new ArrayList<>();
        // 创建 List数组保存数据
        List<Object> remoteItem  = new ArrayList<>();
        remoteItems.add(remoteItem);
        remoteItem.add(1);
        remoteItem.add(deviceTemp);
        remoteItem.add("g");
        remoteDevicePojo.setC(remoteItems);
        return remoteGatewayPojo;
    }

}
