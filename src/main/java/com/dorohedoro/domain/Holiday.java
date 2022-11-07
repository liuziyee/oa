package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("`holiday`")
public class Holiday implements Serializable {
    
    @TableId
    private Long id;

    private String date;
}
