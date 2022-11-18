package com.dorohedoro.service;

import com.dorohedoro.domain.dto.CheckinDTO;
import com.dorohedoro.domain.dto.GetMonthDTO;

import java.util.List;

public interface ICheckinService {

    String check(Long userId, Long distance);

    void checkin(CheckinDTO checkinDTO);

    void createFaceModel(Long userId, String imgPath);
    
    CheckinDTO getToday(Long userId);

    List<CheckinDTO> getWeek(Long userId);

    List<CheckinDTO> getMonth(Long userId, GetMonthDTO getMonthDTO);
    
    int getDays(Long userId);

}
