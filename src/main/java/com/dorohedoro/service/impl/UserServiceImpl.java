package com.dorohedoro.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.domain.User;
import com.dorohedoro.mapper.UserMapper;
import com.dorohedoro.problem.BusinessProblem;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.WeChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final WeChatUtil weChatUtil;
    private final RedisTemplate redisTemplate;
    
    @Override
    public Long register(String registerCode, String code) {
        String openId = weChatUtil.getOpenId(code);
        if (registerCode.equals("000000")) {
            log.debug("注册超级管理员账号");
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
        
        if (redisTemplate.hasKey(registerCode)) {
            log.debug("绑定openid到员工账号");
            Long userid = (Long) redisTemplate.opsForValue().get(registerCode);
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userid));
            user.setOpenId(openId);
            userMapper.insert(user);
            return userid;
        } else {
            throw new BusinessProblem("注册码无效或已过期");
        }
    }
}
