package com.dorohedoro.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toList;

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
        return Arrays.stream(values)
                .map(o -> StrUtil.isBlank(o) ? o : HtmlUtil.filter(o))
                .collect(toList())
                .toArray(new String[0]);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = super.getParameterMap();
        map.keySet().stream().map(key -> {
            String[] arr = Arrays.stream(map.get(key))
                    .map(o -> StrUtil.isBlank(o) ? o : HtmlUtil.filter(o))
                    .collect(toList())
                    .toArray(new String[0]);
            map.put(key, arr);
            return key;
        });
        return map;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        Map<String, Object> map = JSONObject.parseObject(super.getInputStream(), Map.class); // 读取输入流
        map.keySet().stream().map(key -> {
            Object value = map.get(key);
            if (value instanceof String && !StrUtil.isBlank(value.toString())) {
                map.put(key, HtmlUtil.filter(value.toString()));
            }
            return key;
        });

        ByteArrayInputStream inputStream = new ByteArrayInputStream(JSONObject.toJSONString(map).getBytes()); // 把字节数组转为输入流
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
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
        if (!StrUtil.isBlank(header)) {
            header = HtmlUtil.filter(header);
        }
        return header;
    }
}
