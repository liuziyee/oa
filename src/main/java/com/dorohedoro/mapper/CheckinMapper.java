package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Checkin;

import java.util.List;
import java.util.Map;

public interface CheckinMapper extends BaseMapper<Checkin> {

    Long selectToday(Map map);
    // 查询签到天数
    int selectDays(Long userId);
    // 查询一周签到记录
    List<Checkin> selectWeek(Map map);
}
