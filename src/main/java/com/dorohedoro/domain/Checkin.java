package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("`checkin`")
public class Checkin implements Serializable {

    @TableId
    private Integer id;
    
    private Integer userId;
    
    private String address;
    
    private String country;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private Integer status;
    
    private Integer risk;
    
    private LocalDate date;
    
    private LocalDateTime createTime;
}
