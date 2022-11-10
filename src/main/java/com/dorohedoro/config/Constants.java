package com.dorohedoro.config;

public class Constants {

    public static final String WORKDAY = "工作日";
    public static final String HOLIDAY = "节假日";
    public static String attendanceTime;
    public static String closingTime;
    public static String attendanceStartTime;
    public static String attendanceEndTime;
    public static String closingStartTime;
    public static String closingEndTime;

    public enum Status {
        UNAVAILABLE(0, "不可用"),
        AVAILABLE(1, "可用");
        
        private int status;
        private String desc;

        Status(int status, String desc) {
            this.status = status;
            this.desc = desc;
        }
    }
}
