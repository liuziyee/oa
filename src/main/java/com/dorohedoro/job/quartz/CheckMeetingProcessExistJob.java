package com.dorohedoro.job.quartz;

import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.service.IWorkflowService;
import com.dorohedoro.util.Enums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckMeetingProcessExistJob extends QuartzJobBean {

    private final RuntimeService runtimeService;
    private final IMeetingService meetingService;
    private final IWorkflowService workflowService;

    @Override
    protected void executeInternal(JobExecutionContext ctx) {
        Map map = ctx.getJobDetail().getJobDataMap();
        String uuid = map.get("uuid").toString();
        String instanceId = map.get("instanceId").toString();

        ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        if (process != null) {
            log.debug("在会议的开始时间,流程实例存在 => 删掉流程实例,会议状态置为审批未通过");
            map.put("processStatus", "未结束");
            workflowService.deleteProcess(instanceId, "会议", uuid);
            meetingService.setStatus(uuid, Enums.MeetingStatus.FAILED.getCode());
            log.debug("会议{}已失效", uuid);
        }
    }
}
