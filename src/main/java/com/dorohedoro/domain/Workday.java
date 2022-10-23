package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("`workday`")
public class Workday implements Serializable {

    @TableId
    private Integer id;
    
    private LocalDate date;
}
