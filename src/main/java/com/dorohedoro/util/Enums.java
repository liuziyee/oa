package com.dorohedoro.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
