package com.dorohedoro.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.domain.User;
import com.dorohedoro.mapper.UserMapper;
import com.dorohedoro.problem.BizProblem;
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
                throw new BizProblem("超级管理员账号已存在");
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
            Long userId = redisUtil.<Long>get(registerCode);
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
            user.setOpenId(openId);
            userMapper.insert(user);
            return userId;
        } else {
            throw new BizProblem("注册码无效或已过期");
        }
    }

    @Override
    public Set<String> getPermissions(Long userId) {
        return userMapper.selectPermissions(userId);
    }

    @Override
    public Long login(String code) {
        log.debug("根据openid查询员工表,有记录,说明微信账号已经和员工账号绑定并注册,没有记录,说明微信账号没有注册");
        String openId = weChatUtil.getOpenId(code);
        Long userId = userMapper.selectByOpenId(openId).orElseThrow(() -> new BizProblem("账号不存在"));
        // TODO 消息队列
        return userId;
    }
}
