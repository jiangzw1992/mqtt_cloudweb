package com.lelian.mqtt.util;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ThreadPoolUtil {

    public static ExecutorService executorService;

    static {
        executorService = Executors.newFixedThreadPool(20);
    }

}
