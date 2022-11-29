package com.dorohedoro.config.shiro;

import cn.hutool.core.convert.Convert;
import com.dorohedoro.domain.User;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.RedisUtil;
import com.dorohedoro.util.ThreadLocalUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRealm extends AuthorizingRealm {

    private final IUserService userService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        return AccessToken.class.isAssignableFrom(token.getClass()); // 兼容子类
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        Set<String> permissions = userService.getPermissions(user.getId());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String accessToken = (String) token.getCredentials();
        Long userId;
        try {
            // 这里的访问令牌是从请求头获取的,有可能过期
            userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        } catch (ExpiredJwtException e) {
            userId = Convert.toLong(jwtUtil.get(ThreadLocalUtil.get(), "userid"));
        }
        User user = userService.getDetail(userId).orElseThrow(() -> new LockedAccountException("账号已冻结"));
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;
    }
}
