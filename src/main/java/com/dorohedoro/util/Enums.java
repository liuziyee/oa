package com.dorohedoro.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Enums {

    @Getter
    @AllArgsConstructor
    public enum Status {
        UNAVAILABLE(0, "不可用"),
        AVAILABLE(1, "可用");
        
        private int code;
        private String desc;
    }
    
    @Getter
    @AllArgsConstructor
    public enum CheckinStatus {
        ABSENT(0, "矿工"),
        NORMAL(1, "正常"),
        LATE(2, "迟到");
        
        private int code;
        private String desc;
    }
    
    @Getter
    @AllArgsConstructor
    public enum Risk {
        LOW(1, "低风险"),
        MEDIUM(2, "中风险"),
        HIGH(3, "高风险");

        private static final Map<String, Risk> map = Arrays.stream(values()).collect(Collectors.toMap(Risk::getDesc, risk -> risk));

        private int code;
        private String desc;

        public static int desc2Code(String desc) {
            return map.get(desc).getCode();
        }
    }
}
