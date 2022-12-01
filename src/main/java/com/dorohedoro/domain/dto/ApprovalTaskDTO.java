package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class ApprovalTaskDTO {
    
    @NotBlank
    private String taskId;
    
    @NotBlank
    private String result;
}
