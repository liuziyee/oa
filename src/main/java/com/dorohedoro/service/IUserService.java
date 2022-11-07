package com.dorohedoro.service;

import java.util.Set;

public interface IUserService {

    Long register(String registerCode, String code);

    Set<String> getPermissions(Long userid);
}
