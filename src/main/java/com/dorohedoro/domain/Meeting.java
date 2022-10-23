package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("`meeting`")
public class Meeting implements Serializable {

    @TableId
    private Long id;

    private String uuid;

    private String title;

    private Long creatorId;

    private LocalDate date;

    private String place;

    private LocalTime start;

    private LocalTime end;

    private Integer type;
    
    private String members;

    private String desc;

    private String instanceId;

    private Integer status;

    private LocalDateTime createTime;
}
