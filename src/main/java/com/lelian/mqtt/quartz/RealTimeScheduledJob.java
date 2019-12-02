package com.lelian.mqtt.quartz;

import com.lelian.mqtt.service.ServiceApi;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 实时获取数据任务
 */
public class RealTimeScheduledJob implements Job {

    private Logger logger = LoggerFactory.getLogger(RealTimeScheduledJob.class);

    @Autowired
    ServiceApi serviceApi;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try{
            serviceApi.getRealTimeData();
        }catch (Exception e){
            logger.error("实时获取数据任务",e);
        }
    }
}
