package com.dorohedoro.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (!StrUtil.isBlank(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        log.debug("values可能为空,在做Stream流操作前最好转为Optional");
        return Optional.ofNullable(values).map(arr -> Arrays.stream(arr)
                .map(o -> StrUtil.isBlank(o) ? o : HtmlUtil.filter(o))
                .toArray(String[]::new))
                .orElse(null);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramMap = super.getParameterMap();
        return Optional.ofNullable(paramMap).map(map -> {
            map.keySet().stream().peek(key -> {
                String[] arr = Arrays.stream(map.get(key))
                        .map(o -> StrUtil.isBlank(o) ? o : HtmlUtil.filter(o))
                        .toArray(String[]::new);
                map.put(key, arr);
            });
            return map;
        }).orElse(null);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        log.debug("将输入流转为Map");
        Map<String, Object> payload = JSONObject.parseObject(super.getInputStream(), Map.class);

        Optional.ofNullable(payload).map(map -> map.keySet().stream().peek(key -> {
            Object value = map.get(key);
            if (value instanceof String && !StrUtil.isBlank(value.toString())) {
                map.put(key, HtmlUtil.filter(value.toString()));
            }
        }).collect(toSet()));
        log.debug("Stream的map,peek等中间操作为惰性操作,Optional的map不是惰性操作");

        log.debug("将Map转为输入流");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(JSONObject.toJSONString(payload).getBytes());
        return new ServletInputStream() {
            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }
        };
    }

    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);
        return doFilter(header);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> enums = super.getHeaders(name);
        Vector<String> headers = new Vector<>();
        while (enums.hasMoreElements()) {
            String header = doFilter(enums.nextElement());
            headers.add(header);
        }
        return headers.elements();
    }

    private String doFilter(String header) {
        if (!StrUtil.isBlank(header)) {
            header = HtmlUtil.filter(header);
            if (header.startsWith("Bearer")) {
                log.info("截取Authorization请求头");
                return header.replace("Bearer", "").trim();
            }
        }
        return header;
    }
}
