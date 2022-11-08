package com.dorohedoro.util;

import cn.hutool.core.date.DateUtil;
import com.dorohedoro.config.AppProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final AppProperties appProperties;
    public static final Key accessKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 密钥

    // 生成访问令牌
    public String generate(Long userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(DateUtil.offsetDay(new Date(), appProperties.getJwt().getExpire()))
                .signWith(accessKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // 校验访问令牌
    public void check(String accessToken) {
        Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken);
    }

    public <T> T get(String accessToken, String key) {
        return (T) Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken).getBody().get(key);
    }
}
