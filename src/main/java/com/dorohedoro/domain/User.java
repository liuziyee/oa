package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("`user`")
public class User implements Serializable {

    @TableId
    private Long id;

    private String openId;

    private String nickname;

    private String avatarUrl;

    private String name;

    private String sex;

    private String tel;

    private String email;

    private String hiredate;

    private String roles;

    private Boolean root;

    private Long deptId;

    private Integer status;
    
    @TableField(fill = FieldFill.INSERT, select = false)
    private Date createTime;
    
    @TableField(exist = false) // 不是表字段
    private String deptName;
}
