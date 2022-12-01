package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import com.dorohedoro.domain.dto.ApprovalTaskDTO;
import com.dorohedoro.domain.dto.GetTasksDTO;
import com.dorohedoro.service.IWorkflowService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "工作流模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/workflow")
public class WorkflowController {

    private final IWorkflowService workflowService;
    private final JwtUtil jwtUtil;

    @PostMapping("/getTasks")
    @ApiOperation("查询指派给该用户的任务")
    public R getTasks(@Valid @RequestBody GetTasksDTO getTasksDTO, @RequestHeader("Authorization") String accessToken) {
        getTasksDTO.setUserId(Convert.toLong(jwtUtil.get(accessToken, "userid")));
        List<Map> tasks = workflowService.getTasks(getTasksDTO);
        return R.ok(tasks, null);
    }

    @PostMapping("/approvalTask")
    @ApiOperation("审批")
    public R approvalTask(@Valid @RequestBody ApprovalTaskDTO approvalTaskDTO) {
        workflowService.approvalTask(approvalTaskDTO);
        return R.ok();
    }
}