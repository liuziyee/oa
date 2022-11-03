package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
public class SayHelloDTO {

    @ApiModelProperty("用户名")
    @NotBlank
    private String username;
}
