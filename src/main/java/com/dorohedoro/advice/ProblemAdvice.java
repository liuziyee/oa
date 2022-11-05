package com.dorohedoro.advice;

import com.dorohedoro.util.R;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProblemAdvice {

    @ExceptionHandler(Exception.class)
    public R exceptionHandler(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            return R.error(HttpStatus.BAD_REQUEST.value(), 
                    exception.getBindingResult().getFieldError().getDefaultMessage());
        }
        if (e instanceof UnauthorizedException) {
            return R.error(HttpStatus.UNAUTHORIZED.value(), "用户未授权");
        }
        return R.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器异常");
    }
}
