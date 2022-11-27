package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.Dept;

import java.util.List;

public interface DeptMapper extends BaseMapper<Dept> {

    List<Dept> selectMembers(String username);
}
