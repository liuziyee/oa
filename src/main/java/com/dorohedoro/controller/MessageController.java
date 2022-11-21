package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import com.dorohedoro.domain.dto.GetMessagesDTO;
import com.dorohedoro.service.IMessageService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Valid
@RestController
@RequestMapping("/message")
@Api(tags = "消息模块")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;
    private final JwtUtil jwtUtil;

    @PostMapping("/getMsgPushRecords")
    @ApiOperation("查询用户消息推送记录")
    public R getMsgPushRecords(@Valid @RequestBody GetMessagesDTO getMsgDTO, @RequestHeader("Authorization") String accessToken) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        int page = getMsgDTO.getPage();
        int size = getMsgDTO.getSize();
        List<Map> msgPushRecords = messageService.getMsgPushRecords(userId, (page - 1) * size, size);
        return R.ok(msgPushRecords, null);
    }
    
    @GetMapping("/getMessage/{id}")
    @ApiOperation("查询消息")
    public R getMessage(@NotBlank @PathVariable String id) {
        Map message = messageService.getMessage(id);
        return R.ok(message, null);
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
}
