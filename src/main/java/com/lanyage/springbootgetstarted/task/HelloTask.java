package com.lanyage.springbootgetstarted.task;

import org.quartz.JobExecutionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;

//@Configuration
//@DisallowConcurrentExecution  //禁止并发执行
public class HelloTask extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        System.out.println("任务开始");
        System.out.println("Hello World" + System.currentTimeMillis());
        System.out.println("任务结束");
    }
}
