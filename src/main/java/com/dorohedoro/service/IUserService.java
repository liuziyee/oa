package com.dorohedoro.service;

import com.dorohedoro.domain.User;

import java.util.Optional;
import java.util.Set;

public interface IUserService {

    Long register(String registerCode, String code);

    Set<String> getPermissions(Long userId);

    Long login(String code);

    Optional<User> getUserDetail(Long userId);
}
