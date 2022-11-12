package com.dorohedoro.util;

import cn.hutool.core.date.DateUtil;
import com.dorohedoro.config.AppProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final AppProperties appProperties;
    public static final Key accessKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generate(Long userId) {
        return Jwts.builder()
                .claim("userid", userId)
                .setIssuedAt(DateUtil.date())
                .setExpiration(DateUtil.offsetDay(DateUtil.date(), appProperties.getJwt().getExpire()))
                .signWith(accessKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public void check(String accessToken) {
        Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken);
    }

    public Object get(String accessToken, String key) {
        return Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken).getBody().get(key);
    }
}
