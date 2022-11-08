package com.dorohedoro.config.shiro;

import cn.hutool.core.util.StrUtil;
import com.dorohedoro.config.AppProperties;
import com.dorohedoro.util.JwtUtil;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class JwtFilter extends AuthenticatingFilter {

    private final AppProperties appProperties;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final RedisTemplate redisTemplate;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        log.debug("将访问令牌封装为认证对象");
        String accessToken = getAccessToken((HttpServletRequest) request);
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
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        log.debug("清空ThreadLocal");
        ThreadLocalUtil.clear();

        String accessToken = getAccessToken(req);
        if (StrUtil.isBlank(accessToken)) {
            log.debug("访问令牌为空或校验失败 => 无效的令牌");
            resp.setStatus(HttpStatus.UNAUTHORIZED.value());
            resp.getWriter().write("无效的令牌");
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
                resp.setStatus(HttpStatus.UNAUTHORIZED.value());
                resp.getWriter().write("令牌已过期");
                return false;
            }
        } catch (Exception e) {
            log.debug("访问令牌为空或校验失败 => 无效的令牌");
            resp.setStatus(HttpStatus.UNAUTHORIZED.value());
            resp.getWriter().write("无效的令牌");
            return false;
        }

        return executeLogin(request, response);
    }

    @Override
    @SneakyThrows
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        resp.getWriter().write(e.getMessage());
        return false;
    }

    private String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (StrUtil.isBlank(accessToken)) {
            accessToken = request.getParameter("Authorization");
        }
        return (accessToken != null && accessToken.startsWith("Bearer")) ? accessToken.replace("Bearer", "").trim() : accessToken;
    }
}
