package com.dorohedoro.service;

import cn.hutool.core.date.DateTime;
import com.dorohedoro.domain.dto.CheckinDTO;

import java.util.List;

public interface ICheckinService {

    String check(Long userId, Long distance);

    void checkin(CheckinDTO checkinDTO);

    void createFaceModel(Long userId, String imgPath);

    List<CheckinDTO> getWeekStatus(Long userId, DateTime monday, DateTime sunday);
}
