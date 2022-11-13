package com.dorohedoro.service;

import com.dorohedoro.domain.dto.CheckinDTO;

public interface ICheckinService {

    String check(Long userId);

    void checkin(CheckinDTO checkinDTO);
}
