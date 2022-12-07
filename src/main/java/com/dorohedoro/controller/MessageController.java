package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import com.dorohedoro.domain.dto.PageDTO;
import com.dorohedoro.job.RabbitJob;
import com.dorohedoro.service.IMessageService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Valid
@Slf4j
@RestController
@RequestMapping("/message")
@Api(tags = "消息模块")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;
    private final RabbitJob rabbitJob;
    private final JwtUtil jwtUtil;

    @PostMapping("/getMsgPushRecords")
    @ApiOperation("查询用户的消息推送记录")
    public R getMsgPushRecords(@Valid @RequestBody PageDTO pageDTO, @RequestHeader("Authorization") String accessToken) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        int page = pageDTO.getPage();
        int size = pageDTO.getSize();
        List<Map> msgPushRecords = messageService.getMsgPushRecords(userId, (page - 1) * size, size);
        return R.ok(msgPushRecords, null);
    }

    @GetMapping("/messageRead/{id}")
    @ApiOperation(("消息置为已读"))
    public R messageRead(@NotBlank @PathVariable String id) {
        return R.ok(messageService.messageRead(id) == 1, null);
    }

    @GetMapping("/deleteMsgPushRecord/{id}")
    @ApiOperation("删除消息推送记录")
    public R deleteMsgPushRecord(@NotBlank @PathVariable String id) {
        return R.ok(messageService.deleteMsgPushRecord(id) == 1, null);
    }
    
    @GetMapping("refreshMsgPushRecords")
    @ApiOperation("刷新消息推送记录")
    public R refreshMsgPushRecords(@RequestHeader("Authorization") String accessToken) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        rabbitJob.receive(userId.toString()); // 拉取MQ消息,刷新消息推送记录
        long last = messageService.getLastMsgCount(userId);
        long unread = messageService.getUnreadMsgCount(userId);
        return R.ok(Map.of("last", last, "unread", unread), null);
    }
}
