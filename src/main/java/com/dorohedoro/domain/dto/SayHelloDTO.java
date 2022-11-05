package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class SayHelloDTO {

    @NotBlank
    @ApiModelProperty("用户名")
    private String username;
}