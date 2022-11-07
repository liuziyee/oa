package com.dorohedoro.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("`face_model`")
public class FaceModel implements Serializable {

    @TableId
    private Long id;

    private Long userId;

    private String faceModel;
}
