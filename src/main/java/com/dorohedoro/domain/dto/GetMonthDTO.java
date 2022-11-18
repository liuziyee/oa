package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class GetMonthDTO {
    
    @NotNull
    @Range(min = 2000, max = 2500)
    private Integer year;
    
    @NotNull
    @Range(min = 1, max = 12)
    private Integer month;
}
