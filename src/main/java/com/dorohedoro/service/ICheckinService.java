package com.dorohedoro.service;

import com.dorohedoro.domain.dto.CheckinDTO;

public interface ICheckinService {

    String check(Long userId, Long distance);

    void checkin(CheckinDTO checkinDTO);

    void createFaceModel(Long userId, String imgPath);
}
