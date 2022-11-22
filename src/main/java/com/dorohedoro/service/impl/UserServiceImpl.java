package com.dorohedoro.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.RegisterDTO;
import com.dorohedoro.job.MessageJob;
import com.dorohedoro.mapper.UserMapper;
import com.dorohedoro.mongo.entity.Message;
import com.dorohedoro.problem.ServerProblem;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.RedisUtil;
import com.dorohedoro.util.WeChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final WeChatUtil weChatUtil;
    private final RedisUtil redisUtil;
    private final MessageJob messageJob;
    
    @Override
    public Long register(RegisterDTO registerDTO) {
        String code = registerDTO.getCode();
        String registerCode = registerDTO.getRegisterCode();
        String nickName = registerDTO.getNickName();
        String avatarUrl = registerDTO.getAvatarUrl();
        
        String openId = weChatUtil.getOpenId(code);
        if (registerCode.equals("000000")) {
            log.debug("注册超级管理员");
            if (userMapper.isRootExist()) {
                throw new ServerProblem("超级管理员账号已存在");
            }
            log.debug("创建超级管理员账号,绑定openid");
            User root = new User();
            root.setOpenId(openId);
            root.setRoles("[0]");
            root.setRoot(true);
            root.setStatus(1);
            root.setNickname(nickName);
            root.setAvatarUrl(avatarUrl);
            userMapper.insert(root);
            Long userId = root.getId();

            Message message = new Message();
            message.setSenderId(0L);
            message.setSenderName("通知");
            message.setCreateTime(DateUtil.date());
            message.setMsg("你已注册为超级管理员,请及时更新你的个人信息");
            messageJob.send(userId.toString(), message);
            return userId;
        }
        
        log.debug("注册员工");
        if (redisUtil.hasKey(registerCode)) {
            log.debug("绑定openid到员工账号");
            Long userId = redisUtil.<Long>get(registerCode);
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
            user.setOpenId(openId);
            user.setNickname(nickName);
            user.setAvatarUrl(avatarUrl);
            userMapper.insert(user);
            return userId;
        } else {
            throw new ServerProblem("注册码无效或已过期");
        }
    }

    @Override
    public Set<String> getPermissions(Long userId) {
        return userMapper.selectPermissions(userId);
    }

    @Override
    public Long login(String code) {
        log.debug("根据openid查询员工表,有记录,说明微信账号已经和员工(或超级管理员)账号绑定并注册,没有记录,说明微信账号没有注册或已冻结");
        String openId = weChatUtil.getOpenId(code);
        Long userId = userMapper.selectByOpenId(openId).orElseThrow(() -> new ServerProblem("未注册或已冻结"));
        return userId;
    }

    @Override
    public Optional<User> getDetail(Long userId) {
        return userMapper.selectById(userId);
    }
}
