package com.dorohedoro.util;

import com.dorohedoro.config.AppProperties;
import com.dorohedoro.problem.WeChatProblem;
import com.dorohedoro.service.WeChatFeignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WeChatUtil {

    private final WeChatFeignService weChatFeignService;
    private final AppProperties appProperties;
    
    public String getOpenId(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", appProperties.getWechat().getAppid());
        map.put("secret", appProperties.getWechat().getAppSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        Map<String, Object> res = weChatFeignService.code2Session(map);
        if (res.get("errcode") != null && !res.get("errcode").equals(0)) {
            throw new WeChatProblem(res.get("errmsg").toString());
        }
        return res.get("openid").toString();
    }
}
