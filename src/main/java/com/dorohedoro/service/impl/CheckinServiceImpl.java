package com.dorohedoro.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.dorohedoro.config.Constants;
import com.dorohedoro.config.Properties;
import com.dorohedoro.domain.dto.CheckinDTO;
import com.dorohedoro.mapper.CheckinMapper;
import com.dorohedoro.mapper.FaceModelMapper;
import com.dorohedoro.mapper.HolidayMapper;
import com.dorohedoro.mapper.WorkdayMapper;
import com.dorohedoro.problem.BizProblem;
import com.dorohedoro.service.ICheckinService;
import com.dorohedoro.util.Enums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements ICheckinService {

    private final WorkdayMapper workdayMapper;
    private final HolidayMapper holidayMapper;
    private final CheckinMapper checkinMapper;
    private final FaceModelMapper faceModelMapper;
    private final Properties properties;

    @Override
    public String check(Long userId) {
        log.debug("检查当天是否为工作日");
        log.debug("是工作日 => 检查当前时间是否在签到时间范围内");
        log.debug("在签到时间范围内 => 查询当天是否有签到记录");
        
        log.debug("默认周一到周五为工作日,周末为节假日");
        String today = Constants.WORKDAY;
        if (DateUtil.date().isWeekend()) {
            today = Constants.HOLIDAY;
        }
        log.debug("查询当天是否为工作日或节假日");
        boolean isWorkday = workdayMapper.selectToday() != null;
        boolean isHoliday = holidayMapper.selectToday() != null;
        if (isWorkday) {
            today = Constants.WORKDAY;
        }
        if (isHoliday) {
            today = Constants.HOLIDAY;
        }

        if (today.equals(Constants.HOLIDAY)) {
            return "非考勤日";
        }

        DateTime now = DateUtil.date();
        DateTime attendanceStartTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceStartTime);
        DateTime attendanceEndTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceEndTime);
        if (now.isBefore(attendanceStartTime)) {
            return "没到上班考勤开始时间";
        }
        if (now.isAfter(attendanceEndTime)) {
            return "超出上班考勤结束时间";
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("start", attendanceStartTime);
        map.put("end", attendanceEndTime);
        return checkinMapper.selectToday(map) == null ? "可以签到" : "已签到";
    }

    @Override
    public void checkin(CheckinDTO checkinDTO) {
        log.debug("确定签到状态");
        log.debug("上传照片和人脸模型");
        
        DateTime now = DateUtil.date();
        DateTime attendanceTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceTime);
        DateTime attendanceEndTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceEndTime);
        
        int status = Enums.CheckinStatus.NORMAL.getCode();
        if (now.compareTo(attendanceTime) > 0 && now.compareTo(attendanceEndTime) <= 0) {
            status = Enums.CheckinStatus.ABSENT.getCode();
        }

        Long userId = checkinDTO.getUserId();
        String faceModel = faceModelMapper.selectByUserId(userId).orElseThrow(() -> new BizProblem("未创建人脸模型"));

        HttpResponse response = HttpUtil.createPost(properties.getFace().getCheckin_url())
                .form("photo", FileUtil.file(checkinDTO.getImgPath()), "targetModel", faceModel)
                .execute();

        if (response.getStatus() != 200) {
            throw new BizProblem("人脸识别服务不可用");
        }

        String body = response.body();
        if ("无法识别人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
            throw new BizProblem(body);
        }
        if ("False".equals(body)) {
            throw new BizProblem("签到无效,非本人签到");
        }
        if ("True".equals(body)) {
            log.debug("签到照片和人脸模型匹配");
        }
    }
}
