package com.dorohedoro.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.dorohedoro.config.Constants;
import com.dorohedoro.mapper.CheckinMapper;
import com.dorohedoro.mapper.HolidayMapper;
import com.dorohedoro.mapper.WorkdayMapper;
import com.dorohedoro.service.ICheckinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements ICheckinService {

    private final WorkdayMapper workdayMapper;
    private final HolidayMapper holidayMapper;
    private final CheckinMapper checkinMapper;

    @Override
    public String check(Long userId) {
        log.debug("检查当天是否为工作日");
        log.debug("是工作日 => 检查当前时间是否在签到区间内");
        log.debug("在签到区间内 => 查询当天是否有签到记录");
        
        log.debug("默认周一到周五为工作日,周末为节假日");
        String today = Constants.WORKDAY;
        if (DateUtil.date().isWeekend()) {
            today = Constants.HOLIDAY;
        }
        log.debug("查询当天是否为工作日或节假日");
        boolean isWorkday = workdayMapper.selectToday() == null ? false : true;
        boolean isHoliday = holidayMapper.selectToday() == null ? false : true;
        if (isWorkday) {
            today = Constants.WORKDAY;
        }
        if (isHoliday) {
            today = Constants.HOLIDAY;
        }

        if (today.equals(Constants.HOLIDAY)) {
            return "非考勤日";
        }

        DateTime now = DateUtil.date();
        DateTime attendanceStartTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceStartTime);
        DateTime attendanceEndTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceEndTime);
        if (now.isBefore(attendanceStartTime)) {
            return "没到上班考勤开始时间";
        }
        if (now.isAfter(attendanceEndTime)) {
            return "超出上班考勤结束时间";
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("start", attendanceStartTime);
        map.put("end", attendanceEndTime);
        return checkinMapper.selectToday(map) == null ? "可以签到" : "已签到";
    }
}
