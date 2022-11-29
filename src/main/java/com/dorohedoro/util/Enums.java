package com.dorohedoro.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;


public class Enums {

    @Getter
    @AllArgsConstructor
    public enum Status {
        UNAVAILABLE(0, "unavailable"),
        AVAILABLE(1, "available");
        
        private int code;
        private String desc;
    }
    
    @Getter
    @AllArgsConstructor
    public enum CheckinStatus {
        ABSENT(0, "缺勤"),
        NORMAL(1, "正常"),
        LATE(2, "迟到");

        private int code;
        private String desc;
        private static final Map<Integer, CheckinStatus> map = Arrays.stream(values()).collect(toMap(CheckinStatus::getCode, item -> item));

        public static String code2Desc(int code) {
            return map.get(code).getDesc();
        }
    }
    
    @Getter
    @AllArgsConstructor
    public enum Risk {
        LOW(1, "低风险"),
        MEDIUM(2, "中风险"),
        HIGH(3, "高风险");

        private int code;
        private String desc;
        private static final Map<String, Risk> map = Arrays.stream(values()).collect(toMap(Risk::getDesc, item -> item));

        public static int desc2Code(String desc) {
            return map.get(desc).getCode();
        }
    }

    @Getter
    @AllArgsConstructor
    public enum MeetingStatus {
        UNAPPROVED(1, "待审批"),
        FAILED(2, "审批未通过"),
        UNSTART(3, "未开始"),
        DOING(4, "进行中"),
        FINISHED(5, "已结束");

        private int code;
        private String desc;
    }
}
