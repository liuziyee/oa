package com.dorohedoro.util;

import cn.hutool.core.date.DateUtil;
import com.dorohedoro.config.AppProperties;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.KeyPair;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final AppProperties appProperties;
    private final KeyPair keyPair;

    public String generate(Long userId) {
        return Jwts.builder()
                .claim("userid", userId)
                .setIssuedAt(DateUtil.date())
                .setExpiration(DateUtil.offsetDay(DateUtil.date(), appProperties.getJwt().getExpire()))
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public void check(String accessToken) {
        Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(accessToken);
    }

    public Object get(String accessToken, String key) {
        return Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(accessToken).getBody().get(key);
    }
}
