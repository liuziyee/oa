package com.dorohedoro.service.impl;

import com.dorohedoro.service.IWorkflowService;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements IWorkflowService {

    private final RuntimeService runtimeService;
    
    @Override
    public String createProcessInstance(Map map) {
        return runtimeService.startProcessInstanceByKey("meeting", map).getProcessInstanceId();
    }
}
