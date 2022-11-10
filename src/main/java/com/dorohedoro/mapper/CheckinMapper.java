package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Checkin;

import java.util.Map;

public interface CheckinMapper extends BaseMapper<Checkin> {

    Long selectToday(Map map);
}
