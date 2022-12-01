package com.dorohedoro.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.dorohedoro.config.Constants;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.GetTasksDTO;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.service.IWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
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
    
    @Override
    public String createMeetingProcess(Long meetingId) {
        // 封装流程变量
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
        map.put("uuid", uuid);
        map.put("openid", user.getOpenId());
        map.put("date", meeting.getDate());
        map.put("start", meeting.getStart());
        map.put("online", meeting.getType() == Constants.ONLINE);
        map.put("creator", user.getName());
        map.put("creatorAvatarUrl", user.getAvatarUrl());
        map.put("filing", false);
        map.put("type", "会议申请");
        map.put("createDate", DateUtil.today());
        DateTime start = DateUtil.parse(meeting.getStart() + ":00");
        DateTime end = DateUtil.parse(meeting.getEnd() + ":00");
        map.put("hour", DateUtil.between(start, end, DateUnit.HOUR));

        return runtimeService.startProcessInstanceByKey("meeting", map).getProcessInstanceId();
    }

    @Override
    public void deleteProcess(String instanceId, String type, String uuid) {
        if (runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).count() > 0L) {
            runtimeService.deleteProcessInstance(instanceId, "");
        }
        if (historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(instanceId).count() > 0L) {
            historyService.deleteHistoricProcessInstance(instanceId);
        }
        if (type.equals("会议申请")) {}
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
                    .processVariableValueEquals("type", type)
                    .orderByTaskCreateTime().desc();
            List<Task> tasks = query.listPage(skip, size);
            return tasks.stream().map(task -> task.getProcessVariables()).collect(toList());
        }
        if (status.equals("已审批")) {
            HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId)
                    .includeProcessVariables().includeTaskLocalVariables()
                    .processVariableValueEquals("type", type).finished()
                    .orderByHistoricTaskInstanceStartTime().desc();
            List<HistoricTaskInstance> tasks = query.listPage(skip, size);
            return tasks.stream().map(task -> task.getProcessVariables()).collect(toList());
        }
        if (status.equals("已结束")) {}
        return null;
    }
}
