package com.dorohedoro.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class GetMessagesDTO {
    
    @NotNull
    @Min(1)
    private Integer page;
    @NotNull
    @Range(min = 5, max = 25)
    private Integer size;
}
