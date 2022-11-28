package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.User;

import java.util.Optional;
import java.util.Set;

public interface UserMapper extends BaseMapper<User> {

    // 超级管理员是否已创建
    boolean isRootExist();

    Set<String> selectPermissions(Long userId);

    Optional<Long> selectByOpenId(String openId);

    Optional<User> selectById(Long userId);
    
    // 查询会议发起人所在部门的部门经理ID
    Long selectDMId(Long meetingCreatorId);

    // 查询总经理ID
    Long selectGMId();
}
