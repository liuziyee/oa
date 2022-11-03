package com.dorohedoro.util;

import cn.hutool.core.date.DateUtil;
import com.dorohedoro.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    private final AppProperties appProperties;
    public static final Key accessKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 密钥
    
    // 生成访问令牌
    public String generate(long userid) {
        return Jwts.builder()
                .claim("userid", userid)
                .setIssuedAt(new Date())
                .setExpiration(DateUtil.offsetDay(new Date(), appProperties.getJwt().getExpire()))
                .signWith(accessKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // 校验访问令牌
    private Boolean check(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }
    }

    // 获取负载信息
    public Optional<Claims> getClaim(String accessToken) {
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken).getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
