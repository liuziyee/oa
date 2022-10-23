package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("`role`")
public class Role implements Serializable {
    
    @TableId
    private Integer id;

    private String roleName;

    private String permissions;
}
