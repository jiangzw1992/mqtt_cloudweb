package com.lelian.mqtt.service;

import com.google.common.collect.Maps;
import com.lelian.mqtt.mapper.AgentMapper;
import com.lelian.mqtt.mapper.DataitemMapper;
import com.lelian.mqtt.mapper.DeviceMapper;
import com.lelian.mqtt.mapper.DevicemodelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataService {

    private static Logger logger = LoggerFactory.getLogger(DataService.class.getName());

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    DevicemodelMapper devicemodelMapper;

    @Autowired
    DataitemMapper dataitemMapper;

    @Autowired
    AgentMapper agentMapper;

    public void addDevice(int serialnumber,int deviceNumber,int modelid){
        try{
            Map<String,Object> paramMap = Maps.newHashMap();
            paramMap.put("deviceNumber",deviceNumber);
            paramMap.put("serialnumber",serialnumber);
            paramMap.put("modelid",modelid);
            paramMap.put("name","GE-T20G");
            paramMap.put("isactive",1);
            if(deviceMapper.checkConflict(paramMap) == 0){
                deviceMapper.insert(paramMap);
            }else{
                deviceMapper.updateByKey(paramMap);
                logger.info("addDevice 唯一键冲突 ! deviceNumber = "+deviceNumber);
            }
        }catch (Exception e){
            logger.error("addDevice error !",e);
        }
    }

    public void addAgent(int serialnumber){
        try{
            Map<String,Object> paramMap = Maps.newHashMap();
            paramMap.put("serialnumber",serialnumber);
            if(agentMapper.checkConflict(paramMap) == 0){
                Map<String,Object> param = Maps.newHashMap();
                param.put("serialnumber",serialnumber);
                agentMapper.insert(param);
            }else{
                logger.info("addAgent 唯一键冲突 ! serialnumber = "+serialnumber);
            }
        }catch (Exception e){
            logger.error("addAgent error !",e);
        }
    }

    public Integer addModel(){
        try{
            Map<String,Object> paramMap = Maps.newHashMap();
            paramMap.put("name","temp");
            if(devicemodelMapper.checkConflict(paramMap) == 0){
                int modelid = devicemodelMapper.getNextId();
                Map<String,Object> param = Maps.newHashMap();
                param.put("id",modelid);
                devicemodelMapper.insert(param);
                return modelid;
            }else{
                logger.info("addModel 唯一键冲突 ! name = temp");
                return devicemodelMapper.selectByName(paramMap);
            }
        }catch (Exception e){
            logger.error("addAgent error !",e);
        }
        return null;
    }

    public void addDataitem(int deviceNumber){
        try{
            Map<String,Object> paramMap = Maps.newHashMap();
            paramMap.put("deviceNumber",deviceNumber);
            if(dataitemMapper.checkConflict(paramMap) == 0){
                Map<String,Object> param = Maps.newHashMap();
                param.put("deviceNumber",deviceNumber);
                dataitemMapper.insert(param);
            }else{
                logger.info("addDataitem 唯一键冲突 ! deviceNumber = "+deviceNumber);
                dataitemMapper.updateByKey(paramMap);
            }
        }catch (Exception e){
            logger.error("addDataitem error !",e);
        }
    }

    public void updateDataitem(int deviceNumber,String value){
        try{
            Map<String,Object> param = Maps.newHashMap();
            param.put("deviceNumber",deviceNumber);
            param.put("value",value);
            dataitemMapper.updateAlias(param);
        }catch (Exception e){
            logger.error("updateDataitem error !",e);
        }
    }

}
