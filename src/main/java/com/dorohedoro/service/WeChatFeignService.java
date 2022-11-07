package com.dorohedoro.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "wechat", url = "https://api.weixin.qq.com/sns")
public interface WeChatFeignService {

    @GetMapping("/jscode2session")
    Map<String, Object> code2Session(@RequestParam Map<String, String> map);
}
