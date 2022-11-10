package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.User;

import java.util.Optional;
import java.util.Set;

public interface UserMapper extends BaseMapper<User> {

    boolean isRootExist();

    Set<String> selectPermissions(Long userId);

    Optional<Long> selectByOpenId(String openId);

    Optional<User> selectById(Long userId);
}
