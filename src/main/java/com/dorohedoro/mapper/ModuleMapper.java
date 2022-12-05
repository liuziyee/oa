package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Module;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface ModuleMapper extends BaseMapper<Module> {

    @MapKey("permissionId")
    List<Map> selectAll();
}
