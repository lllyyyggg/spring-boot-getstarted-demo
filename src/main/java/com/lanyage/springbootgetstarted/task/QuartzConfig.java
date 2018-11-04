package com.lanyage.springbootgetstarted.task;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// spring boot 2.x 已集成Quartz，无需自己配置
//@Configuration
public class QuartzConfig {
    private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);
    @Bean(name = "helloJob")
    public JobDetail helloTaskDetail() {
        logger.info("[{}] is going to be created as a bean.","JobDetail");
        return JobBuilder.newJob(HelloTask.class).withIdentity("helloTask").storeDurably().build();
    }

    @Bean(name = "helloTrigger")
    public Trigger helloTaskTrigger() {
        logger.info("[{}] is going to be created as a bean.","Trigger");
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("*/5 * * * * ?");
        return TriggerBuilder.newTrigger().forJob(helloTaskDetail()).withIdentity("helloTaskTrigger")
                .withSchedule(cronScheduleBuilder).build();
    }
}
