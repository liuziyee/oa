package com.dorohedoro.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.dorohedoro.config.Constants;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.ApprovalTaskDTO;
import com.dorohedoro.domain.dto.GetTasksDTO;
import com.dorohedoro.job.quartz.CheckMeetingProcessExistJob;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.service.IWorkflowService;
import com.dorohedoro.util.QuartzUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements IWorkflowService {

    private final IUserService userService;
    private final IMeetingService meetingService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final TaskService taskService;
    private final QuartzUtil quartzUtil;

    @Override
    public String createMeetingProcess(Long meetingId) {
        log.debug("封装流程变量,创建流程实例");
        log.debug("创建定时器,在会议的开始时间检查流程实例是否存在");
        Meeting meeting = meetingService.getMeeting(meetingId);
        Long creatorId = meeting.getCreatorId();
        String uuid = meeting.getUuid();
        User user = userService.getDetail(creatorId).orElseThrow();
        String[] roles = user.getRoles().split(",");
        Map<String, Object> map = new HashMap<>();
        if (ArrayUtil.contains(roles, "总经理")) {
            map.put("identity", "总经理");
            map.put("result", "同意");
        } else {
            map.put("identity", "员工");
            map.put("managerId", userService.getDMId(creatorId));
            map.put("gmId", userService.getGMId());
            map.put("sameDept", meetingService.isMembersInSameDept(uuid));
        }
        map.put("id", meetingId);
        map.put("uuid", uuid);
        map.put("openid", user.getOpenId());
        map.put("date", meeting.getDate());
        map.put("start", meeting.getStart());
        map.put("online", meeting.getType() == Constants.ONLINE);
        map.put("creator", user.getName());
        map.put("creatorId", creatorId);
        map.put("creatorAvatarUrl", user.getAvatarUrl());
        map.put("filing", false);
        map.put("type", "会议申请");
        map.put("createDate", DateUtil.today());
        DateTime start = DateUtil.parse(meeting.getStart() + ":00");
        DateTime end = DateUtil.parse(meeting.getEnd() + ":00");
        map.put("hour", DateUtil.between(start, end, DateUnit.HOUR));
        String instanceId = runtimeService.startProcessInstanceByKey("meeting", map).getProcessInstanceId();
        
        JobDetail jobDetail = JobBuilder.newJob(CheckMeetingProcessExistJob.class).build();
        Map data = jobDetail.getJobDataMap();
        data.put("uuid", uuid);
        data.put("instanceId", instanceId);
        DateTime jobStart = DateUtil.parse(meeting.getDate() + " " + meeting.getStart());
        quartzUtil.addJob(jobDetail, uuid, "会议工作流任务组", jobStart);
        return instanceId;
    }

    @Override
    public void deleteProcess(String instanceId, String type, String uuid) {
        if (runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).count() > 0L) {
            runtimeService.deleteProcessInstance(instanceId, "");
        }
        if (historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(instanceId).count() > 0L) {
            historyService.deleteHistoricProcessInstance(instanceId);
        }
        if (type.equals("会议申请")) {
            quartzUtil.deleteJob(uuid, "会议工作流任务组");
            quartzUtil.deleteJob(uuid, "生成房间ID任务组");
            quartzUtil.deleteJob(uuid, "会议开始任务组");
            quartzUtil.deleteJob(uuid, "会议结束任务组");
        }
    }

    @Override
    public List<Map> getTasks(GetTasksDTO getTasksDTO) {
        String userId = getTasksDTO.getUserId().toString();
        int size = getTasksDTO.getSize();
        int skip = (getTasksDTO.getPage() - 1) * size;
        String status = getTasksDTO.getStatus();
        String type = getTasksDTO.getType();

        if (status.equals("待审批")) {
            TaskQuery query = taskService.createTaskQuery().taskAssignee(userId)
                    .includeProcessVariables().includeTaskLocalVariables()
                    .orderByTaskCreateTime().desc();
            if (!StrUtil.isBlank(type)) {
                query.processVariableValueEquals("type", type);
            }
            List<Task> tasks = query.listPage(skip, size);
            return tasks.stream().map(task -> {
                Map map = task.getProcessVariables();
                map.put("taskId", task.getId());
                return map;
            }).collect(toList());
        }
        if (status.equals("已审批")) {
            log.debug("这里要取得指派给该用户的任务和流程最后的任务");
            log.debug("流程已结束 => 取得最后一个历史任务");
            log.debug("流程未结束 => 审批结果为空");
            HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                    .includeProcessVariables().includeTaskLocalVariables()
                    .taskAssignee(userId).finished().orderByHistoricTaskInstanceStartTime().desc();
            if (!StrUtil.isBlank(type)) {
                query.processVariableValueEquals("type", type);
            }
            List<HistoricTaskInstance> tasks = query.listPage(skip, size);
            return tasks.stream().map(task -> {
                Map map = task.getProcessVariables(); // 取得流程变量
                String instanceId = task.getProcessInstanceId();
                ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                        .processInstanceId(instanceId).singleResult();
                map.put("taskId", task.getId());
                map.put("result_1", task.getTaskLocalVariables().get("result"));
                map.put("processStatus", instance == null ? "已结束" : "未结束");
                if (instance == null) {
                    HistoricTaskInstance lastTask = historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(instanceId)
                            .includeProcessVariables().includeTaskLocalVariables()
                            .orderByHistoricTaskInstanceStartTime().desc().list().get(0);
                    map.put("lastUserId", lastTask.getAssignee());
                    map.put("result_2", lastTask.getTaskLocalVariables().get("result"));
                }
                return map;
            }).collect(toList());
        }
        return null;
    }

    @Override
    public void approvalTask(ApprovalTaskDTO approvalTaskDTO) {
        String taskId = approvalTaskDTO.getTaskId();
        taskService.setVariableLocal(taskId, "result", approvalTaskDTO.getResult());
        taskService.complete(taskId);
    }
}
