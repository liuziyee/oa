package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("`action`")
public class Action implements Serializable {

    @TableId
    private Long id;
    
    private String actionCode;
    
    private String actionName;
}
