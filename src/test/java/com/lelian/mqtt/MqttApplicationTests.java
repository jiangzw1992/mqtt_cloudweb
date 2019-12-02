package com.lelian.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.lelian.mqtt.service.SLRemoteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MqttApplicationTests {

	@Autowired
	SLRemoteService slRemoteService;

	@Test
	public void ttt() throws IOException {
//		String result = "{\n" +
//				"    \"code\": 200,\n" +
//				"    \"data\": [\n" +
//				"        {\n" +
//				"            \"61.01.13.000001.0102.01.004.008.99:014\": [\n" +
//				"                {\n" +
//				"                    \"param\": \"tn\",\n" +
//				"                    \"time\": 1570630800000,\n" +
//				"                    \"value\": 19.7\n" +
//				"                }\n" +
//				"            ]\n" +
//				"        },\n" +
//				"        {\n" +
//				"            \"61.01.13.000001.0102.01.004.008.99:015\": [\n" +
//				"                {\n" +
//				"                    \"param\": \"tn\",\n" +
//				"                    \"time\": 1570630800000,\n" +
//				"                    \"value\": 19.4\n" +
//				"                }\n" +
//				"            ]\n" +
//				"        }\n" +
//				"    ],\n" +
//				"    \"msg\": \"成功\",\n" +
//				"    \"page\": 1,\n" +
//				"    \"totalCount\": 2,\n" +
//				"    \"totalPage\": 1\n" +
//				"}";
//		slRemoteService.initSLMap();
//		JSONObject jsonObject = JSONObject.parseObject(result);
//		slRemoteService.handle(jsonObject);
	}

}
