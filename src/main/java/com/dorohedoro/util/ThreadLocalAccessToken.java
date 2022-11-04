package com.dorohedoro.util;

public class ThreadLocalAccessToken {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
    
    public static String get() {
        return threadLocal.get();
    }
    
    public static void set(String accessToken) {
        threadLocal.set(accessToken);
    }
    
    public static void clear() {
        if (threadLocal.get() != null) {
            threadLocal.remove();
        }
    }
}
