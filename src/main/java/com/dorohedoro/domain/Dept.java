package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@TableName("`dept`")
public class Dept implements Serializable {

    @TableId
    private Long id;

    private String deptName;

    @TableField(exist = false)
    private Set<User> members = new HashSet<>();

    @TableField(exist = false)
    private Integer total;
}
