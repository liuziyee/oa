package com.dorohedoro.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.dorohedoro.config.Constants;
import com.dorohedoro.config.Properties;
import com.dorohedoro.domain.Checkin;
import com.dorohedoro.domain.FaceModel;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.CheckinDTO;
import com.dorohedoro.mapper.*;
import com.dorohedoro.problem.ServerProblem;
import com.dorohedoro.service.ICheckinService;
import com.dorohedoro.task.MailTask;
import com.dorohedoro.util.Enums;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements ICheckinService {

    private final WorkdayMapper workdayMapper;
    private final HolidayMapper holidayMapper;
    private final CheckinMapper checkinMapper;
    private final FaceModelMapper faceModelMapper;
    private final UserMapper userMapper;
    private final Properties properties;
    private final MailTask mailTask;

    @Override
    public String check(Long userId, Long distance) {
        log.debug("检查今日是否为工作日:1.默认周一到周五为工作日,周末为节假日;2.查询今日是否为特殊的工作日或节假日");
        log.debug("检查签到地点是否在公司附近");
        log.debug("检查当前时间点是否在签到时间范围内");
        log.debug("查询今日是否有签到记录");

        String today = Constants.WORKDAY;
        if (DateUtil.date().isWeekend()) {
            today = Constants.HOLIDAY;
        }
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

        if (Enums.Status.AVAILABLE.getDesc().equals(Constants.checkDistance) &&
                distance > Convert.toLong(Constants.checkinDistance)) {
            return "签到地点不在公司附近";
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

        return checkinMapper.selectToday(userId, attendanceStartTime, attendanceEndTime) == null ? "可以签到" : "已签到";
    }

    @Override
    public void checkin(CheckinDTO checkinDTO) {
        log.debug("确定签到状态(正常或迟到),缺勤不会生成签到记录");
        log.debug("上传签到照片和人脸模型");
        log.debug("匹配 => 查询疫情风险(高风险 => 发送告警邮件),生成签到记录");

        DateTime checkinTime = DateUtil.date(); // 签到时间
        DateTime attendanceTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceTime);
        DateTime attendanceEndTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceEndTime);

        int status = Enums.CheckinStatus.NORMAL.getCode();
        if (checkinTime.compareTo(attendanceTime) > 0 && checkinTime.compareTo(attendanceEndTime) <= 0) {
            status = Enums.CheckinStatus.ABSENT.getCode();
        }

        Long userId = checkinDTO.getUserId();
        String faceModel = faceModelMapper.selectByUserId(userId).orElseThrow(() -> new ServerProblem("未创建人脸模型"));
        uploadImgAndFaceModel(checkinDTO.getImgPath(), faceModel);

        Integer risk = null;
        String city = checkinDTO.getCity();
        String district = checkinDTO.getDistrict();
        String address = checkinDTO.getAddress();
        if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {
            risk = getRisk(city, district);
            if (risk == Enums.Risk.HIGH.getCode()) {
                User user = userMapper.selectById(userId).orElseThrow();

                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(properties.getMail().getHr());
                msg.setSubject("疫情告警");
                msg.setText(StrUtil.format("{}员工{}于{}的位置信息为: {}, 属于疫情高风险地区",
                        user.getDeptName(), user.getName(), DateUtil.today(), address));
                mailTask.run(msg);
            }
        }

        Checkin checkin = new Checkin();
        checkin.setUserId(userId);
        checkin.setStatus(status);
        checkin.setRisk(risk);
        checkin.setDate(DateUtil.today());
        checkin.setCreateTime(checkinTime);
        BeanUtils.copyProperties(checkinDTO, checkin);
        checkinMapper.insert(checkin);
    }

    @Override
    public void createFaceModel(Long userId, String imgPath) {
        String body = RandomUtil.randomString(25);
        if ("true" == "false") {
            HttpResponse response = HttpUtil.createPost(properties.getFace().getCreateUrl())
                    .form("photo", FileUtil.file(imgPath))
                    .execute();

            body = response.body();
            if ("无法识别人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new ServerProblem(body);
            }
        }

        FaceModel faceModel = new FaceModel();
        faceModel.setUserId(userId);
        faceModel.setFaceModel(body);
        faceModelMapper.insert(faceModel);
    }

    @Override
    public List<CheckinDTO> getWeekRecords(Long userId, DateTime monday, DateTime sunday) {
        log.debug("统计一周的签到状态");
        List<Checkin> checkins = checkinMapper.selectByDate(userId, monday, sunday);
        List<String> workdays = workdayMapper.selectByDate(monday, sunday);
        List<String> holidays = holidayMapper.selectByDate(monday, sunday);
        List<CheckinDTO> checkinStatus = new ArrayList<>();

        DateRange weekRange = DateUtil.range(monday, sunday, DateField.DAY_OF_MONTH); // 以天做为分割单位
        weekRange.forEach(day -> {
            String date = day.toString("yyyy-MM-dd");
            String status = "";
            String type = Constants.WORKDAY;
            if (day.compareTo(DateUtil.date()) <= 0) {
                log.debug("该日{}在当前日期之前", day.toString());
                log.debug("检查该日是否为工作日");
                log.debug("是工作日 => 查询该日的签到记录,没有则记为缺勤");

                if (day.isWeekend()) {
                    type = Constants.HOLIDAY;
                }
                if (workdays != null && workdays.contains(date)) {
                    type = Constants.WORKDAY;
                }
                if (holidays != null && holidays.contains(date)) {
                    type = Constants.HOLIDAY;
                }

                if (type.equals(Constants.WORKDAY)) {
                    status = "缺勤";
                    boolean hasCheckin = false;
                    Checkin dayCheckin = checkins.stream().filter(item -> item.getDate().equals(date)).findAny()
                            .orElse(null);
                    if (dayCheckin != null) {
                        log.debug("该天有签到记录");
                        hasCheckin = true;
                        status = Enums.CheckinStatus.code2Desc(dayCheckin.getStatus());
                    }

                    DateTime attendanceEndTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceEndTime);
                    if (date.equals(DateUtil.today()) && DateUtil.date().isBefore(attendanceEndTime) && !hasCheckin) {
                        log.debug("该日是今日,且当前统计时间在考勤结束时间之前,且没有签到记录 => 签到状态不应该记为缺勤");
                        status = "";
                    }
                }
            }

            CheckinDTO checkinDTO = new CheckinDTO();
            checkinDTO.setStatus(status);
            checkinDTO.setDate(date);
            checkinDTO.setType(type);
            checkinDTO.setDay(day.dayOfWeekEnum().toChinese("周"));
            checkinStatus.add(checkinDTO);
        });
        return checkinStatus;
    }

    private void uploadImgAndFaceModel(String imgPath, String faceModel) {
        if ("true" == "false") {
            HttpResponse response = HttpUtil.createPost(properties.getFace().getCheckinUrl())
                    .form("photo", FileUtil.file(imgPath), "targetModel", faceModel)
                    .execute();

            if (response.getStatus() != 200) {
                throw new ServerProblem("人脸识别服务不可用");
            }

            String body = response.body();
            if ("无法识别人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new ServerProblem(body);
            }
            if ("False".equals(body)) {
                throw new ServerProblem("签到无效,非本人签到");
            }

            log.debug("响应为True,签到照片和人脸模型匹配");
        }
    }

    @SneakyThrows
    private Integer getRisk(String city, String district) {
        log.debug("这里用本地宝查询疫情风险行不通");
        return Enums.Risk.HIGH.getCode();
    }
}
