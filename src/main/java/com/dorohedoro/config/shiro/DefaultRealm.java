package com.dorohedoro.config.shiro;

import com.dorohedoro.domain.User;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultRealm extends AuthorizingRealm {

    private final JwtUtil jwtUtil;
    private final IUserService userService;

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
        String accessToken = (String) token.getCredentials();
        Long userId = jwtUtil.<Long>get(accessToken, "userid");
        User userDetail = userService.getUserDetail(userId).orElseThrow(() -> new LockedAccountException("账号已冻结"));
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userDetail, accessToken, getName());
        return info;
    }
}
