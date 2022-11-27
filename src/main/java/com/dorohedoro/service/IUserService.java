package com.dorohedoro.service;

import com.dorohedoro.domain.Dept;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.RegisterDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IUserService {

    Long register(RegisterDTO registerDTO);

    Set<String> getPermissions(Long userId);

    Long login(String code);

    Optional<User> getDetail(Long userId);

    List<Dept> getDeptMembers(String keyword);
}
