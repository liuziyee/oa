package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Holiday;

public interface HolidayMapper extends BaseMapper<Holiday> {

    Long selectToday(); 
}
