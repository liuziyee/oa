package com.dorohedoro.controller;

import com.dorohedoro.domain.dto.SayHelloDTO;
import com.dorohedoro.util.R;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "示例模块")
@RestController
@RequestMapping("/demo")
public class DemoController {
    
    @ApiOperation("示例接口-问好")
    @ApiOperationSupport(order = 1)
    @PostMapping("/sayHello")
    public R sayHello(@Valid @RequestBody SayHelloDTO sayHelloDTO) {
        return R.ok("你好," + sayHelloDTO.getUsername());
    }
}
