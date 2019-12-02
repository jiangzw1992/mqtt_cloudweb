package com.lelian.mqtt.quartz;

import com.lelian.mqtt.service.ServiceApi;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义定时任务
 */
public class TokenScheduledJob implements Job {

    private Logger logger = LoggerFactory.getLogger(TokenScheduledJob.class);

    @Autowired
    ServiceApi serviceApi;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try{
            serviceApi.refreshAccessToken();
        }catch (Exception e){
            logger.error("定时刷新token失败",e);
        }
    }
}
