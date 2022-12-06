package com.dorohedoro.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.dorohedoro.domain.Role;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.RegisterUserDTO;
import com.dorohedoro.domain.dto.UpdateRoleDTO;
import com.dorohedoro.job.MailJob;
import com.dorohedoro.problem.ServerProblem;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.Enums;
import com.dorohedoro.util.R;
import com.dorohedoro.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "超级管理员模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/root")
public class RootController {

    private final IUserService userService;
    private final RedisUtil redisUtil;
    private final MailJob mailJob;

    @PostMapping("/register")
    @ApiOperation("注册员工")
    @RequiresPermissions(value = {"ROOT", "EMPLOYEE:INSERT"}, logical = Logical.OR)
    public R register(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        User user = BeanUtil.copyProperties(registerUserDTO, User.class);
        user.setRoles("[3]");
        user.setStatus(Enums.Status.AVAILABLE.getCode());
        user.setRoot(false);
        Long userId = userService.createUser(user);
        String code = RandomUtil.randomNumbers(6);
        log.debug("注册码:{}", code);
        redisUtil.set(code, userId);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("你的注册码");
        msg.setText(code);
        mailJob.send(msg);
        return R.ok();
    }

    @GetMapping("/getRoles")
    @ApiOperation("查询角色列表")
    @RequiresPermissions("ROOT")
    public R getRoles() {
        List<Role> roles = userService.getRoles();
        return R.ok(roles, null);
    }

    @GetMapping("/getModules")
    @ApiOperation("查询模块列表")
    @RequiresPermissions("ROOT")
    public R getModules() {
        List<Map> modules = userService.getModules();
        return R.ok(modules, null);
    }
    
    @PostMapping("/updateRole")
    @ApiOperation("更新角色包含的权限")
    @RequiresPermissions("ROOT")
    public R updateRole(@Valid @RequestBody UpdateRoleDTO updateRoleDTO) {
        if (!JSONUtil.isJsonArray(updateRoleDTO.getPermissions())) {
            throw new ServerProblem("字段[permissions]要求为JSON数组");
        }
        
        Role role = BeanUtil.copyProperties(updateRoleDTO, Role.class);
        userService.updateRole(role);
        return R.ok();
    }
}
