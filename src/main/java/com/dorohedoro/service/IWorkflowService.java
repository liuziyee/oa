package com.dorohedoro.service;

public interface IWorkflowService {
    
    String createMeetingProcess(String uuid, Long creatorId, String date, String start);

    void deleteProcess(String instanceId, String type, String uuid);
}
