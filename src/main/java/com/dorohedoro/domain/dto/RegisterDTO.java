package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel
public class RegisterDTO {

    @NotBlank(message = "注册码不能为空")
    @Pattern(regexp = "^[0-9]{6}$")
    private String registerCode;

    @NotBlank(message = "微信授权码不能为空")
    private String code;
    
    private String nickName;
    
    private String avatarUrl;
}
