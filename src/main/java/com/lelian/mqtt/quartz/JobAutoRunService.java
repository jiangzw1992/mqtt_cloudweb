package com.lelian.mqtt.quartz;

import com.lelian.mqtt.service.SLRemoteService;
import com.lelian.mqtt.service.ServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class JobAutoRunService implements ApplicationRunner {

    @Autowired
    public SchedulerManager schedulerManager;

    @Autowired
    ServiceApi serviceApi;

    @Autowired
    SLRemoteService slRemoteService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        slRemoteService.initSLMap();
        serviceApi.getRealTimeData();
        //每10个小时刷新一下token
        schedulerManager.startJob("0 0 0/20 * * ? *","tokenJob","tokenJobGroup", TokenScheduledJob.class);
        //每天凌晨20秒的时候开始运行
        schedulerManager.startJob("0 */30 * * * ? *","realTimeJob","realTimeJobGroup", RealTimeScheduledJob.class);
    }


}
