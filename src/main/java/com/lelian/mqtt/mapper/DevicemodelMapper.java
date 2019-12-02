package com.lelian.mqtt.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Mapper
public interface DevicemodelMapper {

    int insert(Map<String, Object> map);

    int getNextId();

    int checkConflict(Map<String, Object> map);

    int selectByName(Map<String, Object> param);

}