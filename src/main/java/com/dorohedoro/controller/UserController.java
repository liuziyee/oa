package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.dorohedoro.domain.Dept;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.GetDeptMembersDTO;
import com.dorohedoro.domain.dto.GetDetailsDTO;
import com.dorohedoro.domain.dto.LoginDTO;
import com.dorohedoro.domain.dto.RegisterDTO;
import com.dorohedoro.problem.ServerProblem;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import com.dorohedoro.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @ApiOperation("注册")
    @PostMapping("/register")
    public R register(@Valid @RequestBody RegisterDTO registerDTO) {
        Long userId = userService.register(registerDTO);

        log.debug("注册成功 => 查询权限列表,生成访问令牌并缓存到Redis");
        Set<String> permissions = userService.getPermissions(userId);
        String accessToken = jwtUtil.generate(userId);
        redisUtil.set(accessToken, userId);

        return R.<Set<String>>builder().code(HttpStatus.OK.value())
                .accessToken(accessToken).data(permissions).build();
    }
    
    @ApiOperation("登录")
    @PostMapping("/login")
    public R login(@Valid @RequestBody LoginDTO loginDTO) {
        Long userId = userService.login(loginDTO.getCode());

        Set<String> permissions = userService.getPermissions(userId);
        String accessToken = jwtUtil.generate(userId);
        redisUtil.set(accessToken, userId);
        log.debug("访问令牌: {}", accessToken);

        return R.<Set<String>>builder().code(HttpStatus.OK.value())
                .accessToken(accessToken).data(permissions).build();
    }

    @GetMapping("/getDetail")
    @ApiOperation("查询用户信息")
    public R getDetail(@RequestHeader("Authorization") String accessToken) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        User user = userService.getDetail(userId).orElse(null);
        return R.ok(user, null);
    }

    @PostMapping("/getDeptMembers")
    @ApiOperation("查询部门员工")
    @RequiresPermissions(value = {"ROOT", "EMPLOYEE:SELECT"}, logical = Logical.OR)
    public R getDeptMembers(@RequestBody GetDeptMembersDTO getDeptMembersDTO) {
        List<Dept> depts = userService.getDeptMembers(getDeptMembersDTO.getUsername());
        return R.ok(depts, null);
    }
    
    @PostMapping("/getDetails")
    @ApiOperation("批量查询用户信息")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT", "MEETING:UPDATE", "WORKFLOW:APPROVAL"}, logical = Logical.OR)
    public R getDetails(@Valid @RequestBody GetDetailsDTO getDetailsDTO) {
        if (!JSONUtil.isJsonArray(getDetailsDTO.getIds())) {
            throw new ServerProblem("字段[ids]要求为JSON数组");
        }

        List<Long> userIds = JSON.parseArray(getDetailsDTO.getIds(), Long.class);
        List<User> users = userService.getDetails(userIds);
        return R.ok(users, null);
    }
}
