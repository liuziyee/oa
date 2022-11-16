package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("`checkin`")
public class Checkin implements Serializable {

    @TableId
    private Long id;
    
    private Long userId;
    
    private String address;
    
    private String country;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private Integer status;
    
    private Integer risk;
    
    private String date;
    
    private Date createTime;
    
    private String statusDesc;
    private String riskDesc;
    private String checkinTime;
}
