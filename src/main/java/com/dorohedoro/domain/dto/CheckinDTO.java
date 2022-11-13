package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
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
}
