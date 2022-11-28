package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.config.Constants;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.CreateMeetingDTO;
import com.dorohedoro.domain.dto.PageDTO;
import com.dorohedoro.problem.ServerProblem;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.service.IWorkflowService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "会议模块")
@RestController
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final IMeetingService meetingService;
    private final IUserService userService;
    private final IWorkflowService workflowService;
    private final JwtUtil jwtUtil;

    @PostMapping("/getMeetings")
    @ApiOperation("查询用户参加的会议")
    public R getMeetings(@Valid @RequestBody PageDTO pageDTO, @RequestHeader("Authorization") String accessToken) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        Page<Meeting> page = new Page<>(pageDTO.getPage(), pageDTO.getSize());
        List<Map> meetings = meetingService.getMeetings(page, userId);
        return R.ok(meetings, null);
    }
    
    @PostMapping("/createMeeting")
    @ApiOperation("创建会议")
    public R createMeeting(@Valid @RequestBody CreateMeetingDTO createMeetingDTO, @RequestHeader("Authorization") String accessToken) {
        if (createMeetingDTO.getType() == Constants.OFFLINE && StrUtil.isBlank(createMeetingDTO.getPlace())) {
            throw new ServerProblem("线下会议地点不能为空");
        }
        DateTime startTime = DateUtil.parse(createMeetingDTO.getStart() + ":00");
        DateTime endTime = DateUtil.parse( createMeetingDTO.getEnd() + ":00");
        if (endTime.isBeforeOrEquals(startTime)) {
            throw new ServerProblem("开始时间要求早于结束时间");
        }
        if (!JSONUtil.isJsonArray(createMeetingDTO.getMembers())) {
            throw new ServerProblem("字段[members]要求为JSON数组");
        }

        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        String uuid = IdUtil.simpleUUID();
        Meeting meeting = new Meeting();
        BeanUtils.copyProperties(createMeetingDTO, meeting);
        meeting.setUuid(uuid);
        meeting.setCreatorId(userId);
        meeting.setStart(createMeetingDTO.getStart() + ":00");
        meeting.setEnd(createMeetingDTO.getEnd() + ":00");
        meeting.setStatus(1);
        meetingService.createMeeting(meeting); // 生成会议记录

        User user = userService.getDetail(userId).orElseThrow();
        String[] roles = user.getRoles().split(",");
        Map<String, Object> map = new HashMap<>();
        if (!ArrayUtil.contains(roles, "总经理")) {
            map.put("managerId", userService.getDMId(userId));
            map.put("gmId", userService.getGMId());
            map.put("sameDept", meetingService.isMembersInSameDept(uuid));
        }
        map.put("uuid", uuid);
        map.put("openid", user.getOpenId());
        map.put("date", meeting.getDate());
        map.put("start", meeting.getStart());
        String instanceId = workflowService.createProcessInstance(map); // 创建流程实例

        meetingService.setInstanceId(uuid, instanceId);
        
        return R.ok(null, "会议已创建");
    }
}
