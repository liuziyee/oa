package com.dorohedoro.controller;

import com.dorohedoro.domain.dto.LoginDTO;
import com.dorohedoro.domain.dto.RegisterDTO;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import com.dorohedoro.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
        Long userId = userService.register(registerDTO.getRegisterCode(), registerDTO.getCode());

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
}
