package com.dorohedoro.service.impl;

import com.dorohedoro.domain.Meeting;
import com.dorohedoro.mapper.MeetingMapper;
import com.dorohedoro.service.IMeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements IMeetingService {

    private final MeetingMapper meetingMapper;
    
    @Override
    public void createMeeting(Meeting meeting) {
        meetingMapper.insert(meeting);
    }
}
