package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("`user`")
public class User implements Serializable {

    @TableId
    private Integer id;

    private String openId;

    private String nickname;

    private String photo;

    private String name;

    private String sex;

    private String tel;

    private String email;

    private LocalDate hiredate;

    private String roles;

    private Boolean root;

    private Integer deptId;

    private Integer status;

    private LocalDateTime createTime;
}
