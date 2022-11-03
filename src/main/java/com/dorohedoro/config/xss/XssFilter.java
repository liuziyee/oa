package com.dorohedoro.config.xss;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(value = "/*")
public class XssFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrapper, response); // 这里传递的是包装类
    }
}
