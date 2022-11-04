package com.dorohedoro.config.shiro;

import com.dorohedoro.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultRealm extends AuthorizingRealm {

    private final JwtUtil jwtUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        return AccessToken.class.isAssignableFrom(token.getClass()); // 兼容子类
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // TODO 查询用户角色和权限
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // TODO 查询账户是否被冻结
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
        return info;
    }
}
