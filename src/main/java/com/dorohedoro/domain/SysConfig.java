package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("`sys_config`")
public class SysConfig implements Serializable {
    
    @TableId
    private Long id;

    private String paramKey;

    private String paramValue;

    private Boolean status;

    private String remark;
}
