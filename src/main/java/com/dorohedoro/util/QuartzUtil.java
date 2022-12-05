package com.dorohedoro.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzUtil {

    private final Scheduler scheduler;
    
    public void addJob(JobDetail jobDetail, String jobName, String groupName, Date jobStart) {
        try {
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, groupName)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .startAt(jobStart).build();
            scheduler.scheduleJob(jobDetail, trigger);
            log.debug("已创建" + jobName + "定时器");
        } catch (Throwable e) {
            log.error("定时器创建失败");
        }
    }

    public void deleteJob(String jobName, String groupName) {
        TriggerKey key = TriggerKey.triggerKey(jobName, groupName);
        try {
            scheduler.resumeTrigger(key);
            scheduler.unscheduleJob(key);
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
            log.debug("已删除" + jobName + "定时器");
        } catch (SchedulerException e) {
            log.error("定时器删除失败");
        }
    }
}
