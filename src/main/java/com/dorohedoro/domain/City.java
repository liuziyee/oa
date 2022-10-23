package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("`city`")
public class City implements Serializable {
    
    @TableId
    private Integer id;

    private String city;
    
    private String code;
}
