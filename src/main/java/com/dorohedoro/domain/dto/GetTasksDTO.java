package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class GetTasksDTO extends PageDTO {

    private Long userId;
    
    @NotBlank
    private String status; // 审批状态
    
    private String type; // 审批类型
}
