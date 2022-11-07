package com.dorohedoro.advice;

import com.dorohedoro.util.R;
import com.dorohedoro.util.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RefreshTokenAspect {

    @Pointcut("execution(* com.dorohedoro.controller.*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public R doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        R r = (R) joinPoint.proceed();
        String refreshToken = ThreadLocalUtil.get();
        if (refreshToken != null) {
            log.debug("将刷新后的访问令牌放入响应对象R");
            r.setAccessToken(refreshToken);
            ThreadLocalUtil.clear();
        }
        return r;
    }
}
