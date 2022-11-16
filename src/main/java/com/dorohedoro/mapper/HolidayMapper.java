package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Holiday;

import java.util.List;
import java.util.Map;

public interface HolidayMapper extends BaseMapper<Holiday> {

    Long selectToday();

    List<String> selectByDate(Map map);
}
