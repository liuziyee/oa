package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.User;

import java.util.Set;

public interface UserMapper extends BaseMapper<User> {

    boolean isRootExist();

    Set<String> selectPermissions(Long userid);
}
