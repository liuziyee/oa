package com.dorohedoro.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Workday;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkdayMapper extends BaseMapper<Workday> {

    Long selectToday();

    List<String> selectByDate(@Param("start") DateTime startDate, @Param("end") DateTime endDate);
}
