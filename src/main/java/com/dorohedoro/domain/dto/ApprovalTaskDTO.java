package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel
public class ApprovalTaskDTO {
    
    @NotBlank
    private String taskId;
    
    @NotBlank
    @Pattern(regexp = "^同意$|^不同意$")
    private String result;
}
