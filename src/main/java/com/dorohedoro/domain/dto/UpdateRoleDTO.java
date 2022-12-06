package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateRoleDTO {
    
    @NotNull
    private Long id;
    
    @NotBlank
    private String permissions;
}
