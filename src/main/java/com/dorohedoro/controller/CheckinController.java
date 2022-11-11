package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import com.dorohedoro.service.ICheckinService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "签到模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/checkin")
public class CheckinController {

    private final ICheckinService checkinService;
    private final JwtUtil jwtUtil;

    @GetMapping("/check")
    @ApiOperation("检查当天是否可以签到")
    public R check(@RequestHeader("Authorization") String accessToken) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        return R.ok(checkinService.check(userId));
    }
}
