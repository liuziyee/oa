package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.mapper.MeetingMapper;
import com.dorohedoro.service.IMeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements IMeetingService {

    private final MeetingMapper meetingMapper;
    
    @Override
    public void createMeeting(Meeting meeting) {
        meetingMapper.insert(meeting);
    }

    @Override
    public List<Map> getMeetings(Page<Meeting> page, Long userId) {
        page = meetingMapper.selectPage(page, userId);
        JSONArray array = null;
        Map<String, Object> map;
        List<Map> res = new ArrayList<>();
        String pre = null;
        for (Meeting meeting : page.getRecords()) {
            String date = meeting.getDate();
            if (!date.equals(pre)) {
                pre = date;
                array = new JSONArray();
                map = new HashMap<>();
                map.put("date", date);
                map.put("meetings", array);
                res.add(map);
            }
            array.add(meeting);
        }
        return res;
    }
}
