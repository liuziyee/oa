package com.dorohedoro.util;

import cn.hutool.http.HttpStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class R<T> {

    private int code;
    
    private String msg;
    
    private T data;
    
    public static <T> R<T> ok(T data) {
        return R.<T>builder().code(HttpStatus.HTTP_OK).data(data).build();
    }
    
    public static R error(int code, String msg) {
        return R.builder().code(code).msg(msg).build();
    }
}
