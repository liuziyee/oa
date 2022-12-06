package com.dorohedoro.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.job.quartz.MeetingRoomJob;
import com.dorohedoro.job.quartz.MeetingStatusJob;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.util.Enums;
import com.dorohedoro.util.QuartzUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyService implements JavaDelegate {

    private final QuartzUtil quartzUtil;
    private final IMeetingService meetingService;
    
    @Override
    public void execute(DelegateExecution execution) {
        Map map = execution.getVariables();
        Long id = (Long) map.get("id");
        String uuid = map.get("uuid").toString();
        String result = map.get("result").toString();

        Meeting meeting = meetingService.getMeeting(id);
        String date = meeting.getDate();
        String start = meeting.getStart();
        String end = meeting.getEnd();
        log.debug("会议{}审批结果为{}", uuid, result);
        
        if (result.equals("同意")) {
            log.debug("审批结果为同意 => 会议状态置为未开始");
            log.debug("线上 => 创建定时器,开会前15分钟生成房间ID并缓存到Redis,过期时间为会议的结束时间");
            log.debug("删掉之前创建的定时器");
            log.debug("创建两个定时器,分别用来将会议状态置为进行中和已结束");
            meetingService.setStatus(uuid, Enums.MeetingStatus.UNSTART.getCode());
            boolean online = (boolean) map.get("online");
            if (online) {
                JobDetail jobDetail = JobBuilder.newJob(MeetingRoomJob.class).build();
                map = jobDetail.getJobDataMap();
                map.put("uuid", uuid);
                map.put("expire", DateUtil.parse(date + " " + end));
                DateTime jobStart = DateUtil.parse(date + " " + start)
                        .offset(DateField.MINUTE, -15);
                quartzUtil.addJob(jobDetail, uuid, "生成房间ID任务组", jobStart);
            }

            JobDetail jobDetail = JobBuilder.newJob(MeetingStatusJob.class).build();
            map = jobDetail.getJobDataMap();
            map.put("uuid", uuid);
            map.put("status", Enums.MeetingStatus.DOING.getCode());
            DateTime jobStart = DateUtil.parse(date + " " + start);
            quartzUtil.addJob(jobDetail, uuid, "会议开始任务组", jobStart);

            jobDetail = JobBuilder.newJob(MeetingStatusJob.class).build();
            map = jobDetail.getJobDataMap();
            map.put("uuid", uuid);
            map.put("status", Enums.MeetingStatus.FINISHED.getCode());
            jobStart = DateUtil.parse(date + " " + end);
            quartzUtil.addJob(jobDetail, uuid, "会议结束任务组", jobStart);
        } else {
            meetingService.setStatus(uuid, Enums.MeetingStatus.UNAPPROVED.getCode());
        }
        quartzUtil.deleteJob(uuid, "会议工作流任务组");
    }
}
