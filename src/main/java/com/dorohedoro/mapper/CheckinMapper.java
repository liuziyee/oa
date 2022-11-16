package com.dorohedoro.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Checkin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckinMapper extends BaseMapper<Checkin> {

    Long selectToday(@Param("userId") Long userId, @Param("start") DateTime start, @Param("end") DateTime end);
    // 查询签到天数
    int selectDays(Long userId);
    // 查询周签到记录
    List<Checkin> selectWeek(@Param("userId") Long userId, @Param("start") DateTime start, @Param("end") DateTime end);
}
