package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Workday;

public interface WorkdayMapper extends BaseMapper<Workday> {

    Long selectToday();
}
