package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Meeting;
import org.apache.ibatis.annotations.Param;

public interface MeetingMapper extends BaseMapper<Meeting> {

    // 查询用户参加的会议
    Page<Meeting> selectPage(@Param("page")Page<Meeting> page, @Param("userId") Long userId);

    // 参会者是否在同一部门
    boolean isMembersInSameDept(String uuid);

    Meeting selectById(Long id);
}
