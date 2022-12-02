package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.mapper.MeetingMapper;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.util.Enums;
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
    public Long createMeeting(Meeting meeting) {
        meetingMapper.insert(meeting);
        return meeting.getId();
    }
    
    @Override
    public List<Map> getMeetings(Page<Meeting> page, Long userId) {
        page = meetingMapper.selectPage(page, userId);
        JSONArray array = null;
        Map<String, Object> map;
        List<Map> maps = new ArrayList<>();
        String pre = null;
        for (Meeting meeting : page.getRecords()) {
            String date = meeting.getDate();
            if (!date.equals(pre)) {
                pre = date;
                array = new JSONArray();
                map = new HashMap<>();
                map.put("date", date);
                map.put("meetings", array);
                maps.add(map);
            }
            array.add(meeting);
        }
        return maps;

        //Map<String, List<Meeting>> map = page.getRecords().stream().collect(groupingBy(Meeting::getDate));
        //return map.keySet().stream().map(date -> {
        //    map.get(date).sort(comparing(o -> DateUtil.parse(o.getStart())));
        //    return Map.of("date", date, "meetings", map.get(date));
        //}).sorted(comparing(o -> DateUtil.parse((String)o.get("date")))).collect(toList());
    }

    @Override
    public boolean isMembersInSameDept(String uuid) {
        return meetingMapper.isMembersInSameDept(uuid);
    }

    @Override
    public void setInstanceId(String uuid, String instanceId) {
        Meeting meeting = new Meeting();
        meeting.setInstanceId(instanceId);
        meetingMapper.update(meeting, Wrappers.<Meeting>lambdaQuery().eq(Meeting::getUuid, uuid));
    }

    @Override
    public Meeting getMeeting(Long meetingId) {
        return meetingMapper.selectById(meetingId);
    }

    @Override
    public void updateMeeting(Meeting meeting) {
        meetingMapper.update(meeting, Wrappers.<Meeting>lambdaQuery().eq(Meeting::getId, meeting.getId()));
    }

    @Override
    public void deleteMeeting(Long meetingId) {
        meetingMapper.delete(Wrappers.<Meeting>lambdaQuery().eq(Meeting::getId, meetingId)
                .eq(Meeting::getStatus, Enums.MeetingStatus.UNSTART.getCode()));
    }

    @Override
    public List<String> getMonth(Long userId, String month) {
        return meetingMapper.selectByMonth(userId, month);
    }
}
