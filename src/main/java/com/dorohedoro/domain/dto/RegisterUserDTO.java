package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class RegisterUserDTO {
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String email;
}
