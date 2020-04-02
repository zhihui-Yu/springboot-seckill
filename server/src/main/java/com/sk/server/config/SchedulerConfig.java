package com.sk.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * @Author yzh
 * @Date 2020/4/2 21:38
 * @Version 1.0
 */
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        //设置定时任务线程池数量
        scheduledTaskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
    }
}
