package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectAll();
}
