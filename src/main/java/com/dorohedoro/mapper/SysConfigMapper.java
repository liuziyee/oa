package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.SysConfig;

import java.util.List;

public interface SysConfigMapper extends BaseMapper<SysConfig> {

    List<SysConfig> selectAll();
}
