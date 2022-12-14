package com.dorohedoro.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Checkin;
import com.dorohedoro.domain.dto.CheckinDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckinMapper extends BaseMapper<Checkin> {

    Long selectByCreateTime(@Param("userId") Long userId, @Param("start") DateTime startTime, @Param("end") DateTime endTime);
    
    int selectDays(Long userId);
    
    List<Checkin> selectByDate(@Param("userId") Long userId, @Param("start") DateTime startDate, @Param("end") DateTime endDate);

    CheckinDTO selectToday(Long userId);
}
