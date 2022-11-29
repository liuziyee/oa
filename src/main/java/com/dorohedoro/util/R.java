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

    private String accessToken;

    public static R ok() {
        return R.builder().code(HttpStatus.HTTP_OK).build();
    }
    
    public static <T> R<T> ok(T data, String msg) {
        return R.<T>builder().code(HttpStatus.HTTP_OK).msg(msg).data(data).build();
    }
    
}
