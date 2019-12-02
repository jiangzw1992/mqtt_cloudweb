package com.lelian.mqtt.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtil {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	public static String post(String urlStr,String paramJSONString) {
        HttpURLConnection httpConn = null;
        URL url = null;
        String messageIn="";
        try {
            url = new URL(urlStr);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream(),"UTF-8"));
            out.println(paramJSONString);
            out.flush();
            BufferedReader bin = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream(),"utf-8"));
            StringBuffer buff = new StringBuffer();
            String line;
            while ((line = bin.readLine()) != null) {
                buff.append(line);
            }
            messageIn = buff.toString();
            out.close();
            bin.close();
            httpConn.disconnect();
        } catch (ConnectException ce) {
        	logger.error(ce.getMessage());
        } catch (IOException ie) {
            logger.error(ie.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return messageIn;
    }

    public static String get(String urlStr) {
        HttpURLConnection httpConn = null;
        URL url = null;
        String messageIn="";
        try {
            url = new URL(urlStr);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpConn.setRequestProperty("x-access-token",Constant.accessToken);
            BufferedReader bin = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream(),"utf-8"));
            StringBuffer buff = new StringBuffer();
            String line;
            while ((line = bin.readLine()) != null) {
                buff.append(line);
            }
            messageIn = buff.toString();
            bin.close();
            httpConn.disconnect();
        } catch (ConnectException ce) {
        	logger.error(ce.getMessage());
        } catch (IOException ie) {
            logger.error(ie.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return messageIn;
    }

    public static String get(String urlStr, Map<String,String> headers) {
        HttpURLConnection httpConn = null;
        URL url = null;
        String messageIn="";
        try {
            url = new URL(urlStr);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Accept-Charset", "utf-8");
            httpConn.setRequestProperty("Content-Type", "application/json");
            for(String key : headers.keySet()){
                httpConn.setRequestProperty(key, headers.get(key));
            }
            BufferedReader bin = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream(),"utf-8"));
            StringBuffer buff = new StringBuffer();
            String line;
            while ((line = bin.readLine()) != null) {
                buff.append(line);
            }
            messageIn = buff.toString();
            bin.close();
            httpConn.disconnect();
        } catch (ConnectException ce) {
            logger.error(ce.getMessage());
        } catch (IOException ie) {
            logger.error(ie.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return messageIn;
    }

}
