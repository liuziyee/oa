package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@ApiModel
public class GetDeptMembersDTO {
    
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,15}$")
    private String username;
}
