package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class LoginDTO {

    @NotBlank(message = "微信授权码不能为空")
    private String code;
}
