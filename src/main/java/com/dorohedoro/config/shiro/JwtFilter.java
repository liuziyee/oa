package com.dorohedoro.config.shiro;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import com.dorohedoro.util.RedisUtil;
import com.dorohedoro.util.ThreadLocalUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class JwtFilter extends AuthenticatingFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        log.debug("将访问令牌封装为认证对象");
        HttpServletRequest req = (HttpServletRequest) request;
        String accessToken = req.getHeader("Authorization");
        if (StrUtil.isBlank(accessToken)) {
            return null;
        }
        return new AccessToken(accessToken);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equals("OPTIONS")) {
            log.debug("放行Options请求");
            return true;
        }
        log.debug("该请求交给Shiro处理");
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        log.debug("清空ThreadLocal");
        ThreadLocalUtil.clear();

        String accessToken = req.getHeader("Authorization");
        if (StrUtil.isBlank(accessToken)) {
            log.debug("访问令牌为空 => 无效的令牌");
            R r = R.error(HttpStatus.UNAUTHORIZED.value(), "无效的访问令牌");
            res.getWriter().write(JSONObject.toJSONString(r));
            return false;
        }

        try {
            jwtUtil.check(accessToken);
        } catch (ExpiredJwtException e) {
            if (redisUtil.hasKey(accessToken)) {
                log.debug("访问令牌过期,缓存令牌未过期 => 生成新的访问令牌并缓存到Redis");
                Long userId = redisUtil.<Long>get(accessToken);
                redisUtil.delete(accessToken);
                
                String refreshToken = jwtUtil.generate(userId);
                ThreadLocalUtil.set(refreshToken);
                redisUtil.set(refreshToken, userId);
            } else {
                log.debug("访问令牌过期,缓存令牌过期 => 令牌已过期");
                R r = R.error(HttpStatus.UNAUTHORIZED.value(), "访问令牌已过期");
                res.getWriter().write(JSONObject.toJSONString(r));
                return false;
            }
        } catch (Throwable e) {
            log.error("JWT错误信息: {}", e.getMessage());
            log.debug("访问令牌校验失败 => 无效的令牌");
            R r = R.error(HttpStatus.UNAUTHORIZED.value(), "无效的访问令牌");
            res.getWriter().write(JSONObject.toJSONString(r));
            return false;
        }

        return executeLogin(request, response);
    }

    @Override
    @SneakyThrows
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse res = (HttpServletResponse) response;

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.getWriter().write(e.getMessage());
        return false;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }
}
