package com.dorohedoro.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Meeting;

import java.util.List;
import java.util.Map;

public interface IMeetingService {

    void createMeeting(Meeting meeting);
    
    List<Map> getMeetings(Page<Meeting> page, Long userId);
}
