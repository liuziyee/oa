package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CheckinDTO {

    private Long userId;
    
    private String imgPath;
    
    private String address;
    
    private String country;
    
    private String province;
    
    private String city;
    
    private String district;
    
    @ApiModelProperty("签到日期")
    private String date;
    
    @ApiModelProperty("签到时间")
    private String createTime;
    
    @ApiModelProperty("签到状态")
    private String status;
    
    @ApiModelProperty("疫情风险等级")
    private String risk;
    
    @ApiModelProperty("工作日或节假日")
    private String type;

    @ApiModelProperty("星期")
    private String day;
}
