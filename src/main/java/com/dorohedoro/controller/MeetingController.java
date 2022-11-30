package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.config.Constants;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.domain.dto.CreateMeetingDTO;
import com.dorohedoro.domain.dto.PageDTO;
import com.dorohedoro.domain.dto.UpdateMeetingDTO;
import com.dorohedoro.problem.ServerProblem;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.service.IWorkflowService;
import com.dorohedoro.util.Enums;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@Valid
@Api(tags = "会议模块")
@RestController
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final IMeetingService meetingService;
    private final IWorkflowService workflowService;
    private final JwtUtil jwtUtil;

    @PostMapping("/getMeetings")
    @ApiOperation("查询用户参加的会议")
    public R getMeetings(@Valid @RequestBody PageDTO pageDTO, @RequestHeader("Authorization") String accessToken) {
        Long creatorId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        Page<Meeting> page = new Page<>(pageDTO.getPage(), pageDTO.getSize());
        List<Map> meetings = meetingService.getMeetings(page, creatorId);
        return R.ok(meetings, null);
    }

    @GetMapping("/getMeeting/{id}")
    @ApiOperation("查询会议信息")
    @RequiresPermissions(value = {"ROOT", "MEETING:SELECT"}, logical = Logical.OR)
    public R getMeeting(@NotNull @PathVariable Long id) {
        Meeting meeting = meetingService.getMeeting(id);
        return R.ok(meeting, null);
    }
    
    @PostMapping("/createMeeting")
    @ApiOperation("创建会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT"}, logical = Logical.OR)
    public R createMeeting(@Valid @RequestBody CreateMeetingDTO dto, @RequestHeader("Authorization") String accessToken) {
        log.debug("创建会议记录");
        log.debug("创建流程实例,绑定实例ID");
        check(dto.getType(), dto.getPlace(), dto.getStart(), dto.getEnd(), dto.getMembers());

        Long creatorId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        String uuid = IdUtil.simpleUUID();
        Meeting meeting = new Meeting();
        BeanUtils.copyProperties(dto, meeting);
        meeting.setUuid(uuid);
        meeting.setCreatorId(creatorId);
        meeting.setStart(dto.getStart() + ":00");
        meeting.setEnd(dto.getEnd() + ":00");
        meeting.setStatus(Enums.MeetingStatus.UNAPPROVED.getCode());
        
        meetingService.createMeeting(meeting);
        String instanceId = workflowService.createMeetingProcess(uuid, creatorId, meeting.getDate(), meeting.getStart());
        meetingService.setInstanceId(uuid, instanceId);
        return R.ok();
    }

    @PostMapping("/updateMeeting")
    @ApiOperation("更新会议信息")
    @RequiresPermissions(value = {"ROOT", "MEETING:UPDATE"}, logical = Logical.OR)
    public R updateMeeting(@Valid @RequestBody UpdateMeetingDTO dto) {
        log.debug("更新会议记录,状态为待审批");
        log.debug("删除已有的流程实例,创建新的流程实例,绑定实例ID");
        check(dto.getType(), dto.getPlace(), dto.getStart(), dto.getEnd(), dto.getMembers());

        Meeting oldOne = meetingService.getMeeting(dto.getId());
        Meeting newOne = new Meeting();
        BeanUtils.copyProperties(dto, newOne);
        newOne.setStart(dto.getStart() + ":00");
        newOne.setEnd(dto.getEnd() + ":00");
        newOne.setStatus(Enums.MeetingStatus.UNAPPROVED.getCode());

        meetingService.updateMeeting(newOne);
        workflowService.deleteProcess(newOne.getInstanceId(), "会议申请", oldOne.getUuid());
        String instanceId = workflowService.createMeetingProcess(oldOne.getUuid(), oldOne.getCreatorId(), newOne.getDate(), newOne.getStart());
        meetingService.setInstanceId(oldOne.getUuid(), instanceId);
        return R.ok();
    }

    @GetMapping("/deleteMeeting/{id}")
    @ApiOperation("删除会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:DELETE"}, logical = Logical.OR)
    public R deleteMeeting(@NotNull @PathVariable Long id) {
        Meeting meeting = meetingService.getMeeting(id);
        DateTime start = DateUtil.parse(meeting.getDate() + " " + meeting.getStart());
        DateTime now = DateUtil.date();
        if (now.isAfterOrEquals(start.offset(DateField.MINUTE, -20))) {
            throw new ServerProblem("会议开始前20分钟不允许删除会议");
        }
        
        meetingService.deleteMeeting(id);
        workflowService.deleteProcess(meeting.getInstanceId(), "会议申请", meeting.getUuid());
        return R.ok();
    }

    private void check(int type, String place, String start, String end, String members) {
        if (type == Constants.OFFLINE && StrUtil.isBlank(place)) {
            throw new ServerProblem("线下会议地点不能为空");
        }
        DateTime startTime = DateUtil.parse(start + ":00");
        DateTime endTime = DateUtil.parse( end + ":00");
        if (endTime.isBeforeOrEquals(startTime)) {
            throw new ServerProblem("开始时间要求早于结束时间");
        }
        if (!JSONUtil.isJsonArray(members)) {
            throw new ServerProblem("字段[members]要求为JSON数组");
        }
    }
}
