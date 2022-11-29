package com.dorohedoro.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.dorohedoro.domain.User;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.service.IWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements IWorkflowService {

    private final IUserService userService;
    private final IMeetingService meetingService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    
    @Override
    public String createMeetingProcess(String uuid, Long creatorId, String date, String start) {
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
        map.put("date", date);
        map.put("start", start);
        map.put("filing", false);
        map.put("type", "会议申请");
        map.put("createDate", DateUtil.today());
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
}
