package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.domain.dto.PageDTO;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "会议模块")
@RestController
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final IMeetingService meetingService;
    private final JwtUtil jwtUtil;

    @PostMapping("/getMeetings")
    @ApiOperation("查询用户参加的会议")
    public R getMeetings(@Valid @RequestBody PageDTO pageDTO, @RequestHeader("Authorization") String accessToken) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        Page<Meeting> page = new Page<>(pageDTO.getPage(), pageDTO.getSize());
        List<Map> meetings = meetingService.getMeetings(page, userId);
        return R.ok(meetings, null);
    }
}
