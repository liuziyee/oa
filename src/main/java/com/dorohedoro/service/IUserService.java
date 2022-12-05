package com.dorohedoro.service;

import com.dorohedoro.domain.Dept;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.RegisterDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface IUserService {

    Long register(RegisterDTO registerDTO);

    Set<String> getPermissions(Long userId);

    Long login(String code);

    Optional<User> getDetail(Long userId);

    List<User> getDetails(List<Long> userIds);

    List<Dept> getDeptMembers(String keyword);

    Long getDMId(Long meetingCreatorId);

    Long getGMId();

    Long createUser(User user);

    List<Role> getRoles();

    List<Map> getModules();
}
