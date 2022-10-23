package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("`module`")
public class Module implements Serializable {
    
    @TableId
    private Integer id;

    private String moduleCode;

    private String moduleName;
}
