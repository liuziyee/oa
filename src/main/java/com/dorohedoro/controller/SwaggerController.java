package com.dorohedoro.controller;

import com.dorohedoro.util.R;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "swagger模块")
@RestController
@RequestMapping("/swagger")
public class SwaggerController {
    
    @ApiOperation("问好")
    @ApiOperationSupport(order = 1)
    @GetMapping("/sayHello")
    public R sayHello() {
        return R.ok("你好");
    }
}
