package com.dorohedoro.service;

import com.dorohedoro.domain.dto.ApprovalTaskDTO;
import com.dorohedoro.domain.dto.GetTasksDTO;

import java.util.List;
import java.util.Map;

public interface IWorkflowService {
    
    String createMeetingProcess(Long meetingId);

    void deleteProcess(String instanceId, String type, String uuid);

    List<Map> getTasks(GetTasksDTO getTasksDTO);

    void approvalTask(ApprovalTaskDTO approvalTaskDTO);
}
