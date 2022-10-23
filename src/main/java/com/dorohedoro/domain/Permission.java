package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("`permission`")
public class Permission implements Serializable {
    
    @TableId
    private Integer id;

    private String permissionName;

    private Integer moduleId;

    private Integer actionId;
}
