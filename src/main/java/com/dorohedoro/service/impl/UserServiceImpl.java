package com.dorohedoro.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.domain.Dept;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.RegisterDTO;
import com.dorohedoro.job.MessageJob;
import com.dorohedoro.mapper.DeptMapper;
import com.dorohedoro.mapper.ModuleMapper;
import com.dorohedoro.mapper.RoleMapper;
import com.dorohedoro.mapper.UserMapper;
import com.dorohedoro.mongo.entity.Message;
import com.dorohedoro.problem.ServerProblem;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.RedisUtil;
import com.dorohedoro.util.WeChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final WeChatUtil weChatUtil;
    private final RedisUtil redisUtil;
    private final MessageJob messageJob;
    private final RoleMapper roleMapper;
    private final ModuleMapper moduleMapper;
    
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
        
        if (redisUtil.hasKey(registerCode)) {
            log.debug("绑定openid到员工账号");
            Long userId = Convert.toLong(redisUtil.get(registerCode));
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
            user.setOpenId(openId);
            user.setNickname(nickName);
            user.setAvatarUrl(avatarUrl);
            userMapper.update(user, Wrappers.<User>lambdaQuery().eq(User::getId, user.getId()));
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

    @Override
    public List<User> getDetails(List<Long> userIds) {
        return userMapper.selectBatchIds(userIds);
    }

    @Override
    public List<Dept> getDeptMembers(String keyword) {
        return deptMapper.selectMembers(keyword).stream().peek(dept -> dept.setTotal(dept.getMembers().size()))
                .collect(toList());
    }

    @Override
    public Long getDMId(Long meetingCreatorId) {
        return userMapper.selectDMId(meetingCreatorId);
    }

    @Override
    public Long getGMId() {
        return userMapper.selectGMId();
    }

    @Override
    public Long createUser(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public List<Role> getRoles() {
        return roleMapper.selectAll();
    }

    @Override
    public List<Map> getModules() {
        Long pre = null;
        JSONArray array = null;
        Map<String, Object> module;
        List<Map> maps = new ArrayList<>();
        for (Map map : moduleMapper.selectAll()) {
            Long moduleId = Convert.toLong(map.get("moduleId"));
            if (!moduleId.equals(pre)) {
                pre = moduleId;
                array = new JSONArray();
                module = new HashMap();
                module.put("moduleId", moduleId);
                module.put("module", map.get("module"));
                module.put("permissions", array);
                maps.add(module);
            }
            array.add(map);
        }
        return maps;
    }

    @Override
    public void updateRole(Role role) {
        roleMapper.update(role, Wrappers.<Role>lambdaQuery().eq(Role::getId, role.getId()));
    }
}
