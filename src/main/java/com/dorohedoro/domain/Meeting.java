package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("`meeting`")
public class Meeting implements Serializable {

    @TableId
    private Long id;

    private String uuid;

    private String title;

    private Long creatorId;

    private String date;

    private String place;

    private String start;

    private String end;

    private Integer type;
    
    private String members;

    private String desc;

    private String instanceId;

    private Integer status;

    private Date createTime;
}
