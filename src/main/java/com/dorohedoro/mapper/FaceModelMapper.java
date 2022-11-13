package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.domain.FaceModel;

import java.util.Optional;

public interface FaceModelMapper extends BaseMapper<FaceModel> {

    Optional<String> selectByUserId(Long userId);
}
