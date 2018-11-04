package com.lanyage.springbootgetstarted.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class ScheduleJobs {

    @Scheduled(fixedDelay = 1000 * 2)
    public void fixedDelayJob() {
        System.out.println("[Fixed Delay Job Executed]" + System.currentTimeMillis());
    }
}
