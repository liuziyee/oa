package com.dorohedoro.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.domain.User;
import com.dorohedoro.mapper.UserMapper;
import com.dorohedoro.problem.BusinessProblem;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.RedisUtil;
import com.dorohedoro.util.WeChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final WeChatUtil weChatUtil;
    private final RedisUtil redisUtil;
    
    @Override
    public Long register(String registerCode, String code) {
        String openId = weChatUtil.getOpenId(code);
        if (registerCode.equals("000000")) {
            log.debug("注册超级管理员");
            if (userMapper.isRootExist()) {
                throw new BusinessProblem("超级管理员账号已存在");
            }
            log.debug("创建超级管理员账号,绑定openid");
            User root = new User();
            root.setOpenId(openId);
            root.setRoles("[0]");
            root.setRoot(true);
            root.setStatus(1);
            userMapper.insert(root);
            return root.getId();
        }

        log.debug("注册员工");
        if (redisUtil.hasKey(registerCode)) {
            log.debug("绑定openid到员工账号");
            Long userid = redisUtil.<Long>get(registerCode);
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userid));
            user.setOpenId(openId);
            userMapper.insert(user);
            return userid;
        } else {
            throw new BusinessProblem("注册码无效或已过期");
        }
    }

    @Override
    public Set<String> getPermissions(Long userid) {
        return userMapper.selectPermissions(userid);
    }
}
