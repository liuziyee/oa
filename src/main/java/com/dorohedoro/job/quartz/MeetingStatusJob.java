package com.dorohedoro.job.quartz;

import com.dorohedoro.service.IMeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingStatusJob extends QuartzJobBean {

    private final IMeetingService meetingService;

    @Override
    protected void executeInternal(JobExecutionContext ctx) {
        Map map = ctx.getJobDetail().getJobDataMap();
        String uuid = map.get("uuid").toString();
        int status = (int) map.get("status");
        meetingService.setStatus(uuid, status);
        log.debug("会议{}状态置为{}", uuid, status);
    }
}
