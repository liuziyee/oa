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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.config.Constants;
import com.dorohedoro.config.Properties;
import com.dorohedoro.domain.Checkin;
import com.dorohedoro.domain.FaceModel;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.CheckinDTO;
import com.dorohedoro.domain.dto.GetMonthDTO;
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

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

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

        return checkinMapper.selectByCreateTime(userId, attendanceStartTime, attendanceEndTime) == null ? "可以签到" : "已签到";
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
            status = Enums.CheckinStatus.LATE.getCode();
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
        BeanUtils.copyProperties(checkinDTO, checkin);
        checkin.setUserId(userId);
        checkin.setStatus(status);
        checkin.setRisk(risk);
        checkin.setDate(DateUtil.today());
        checkin.setCreateTime(checkinTime);
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
    public CheckinDTO getToday(Long userId) {
        return checkinMapper.selectToday(userId);
    }

    @Override
    public List<CheckinDTO> getWeek(Long userId) {
        log.debug("统计本周的签到数据(入职日期之前不做统计)");
        log.debug("默认统计本周一到周日,如果员工在本周入职,入职日期要做为统计的开始日期");

        DateTime monday = DateUtil.beginOfWeek(DateUtil.date()); // 本周一
        DateTime sunday = DateUtil.endOfWeek(DateUtil.date()); // 本周日
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
        DateTime hiredate = DateUtil.parse(user.getHiredate());
        if (hiredate.isAfter(monday)) {
            log.debug("入职时间在本周一之后,说明员工在本周入职,入职日期要做为统计的开始日期");
            monday = hiredate;
        }
        return getPeriod(userId, monday, sunday);
    }

    @Override
    public List<CheckinDTO> getMonth(Long userId, GetMonthDTO getMonthDTO) {
        log.debug("查询某月的签到数据(入职日期之前不做统计)");
        log.debug("默认统计该月第一天到最后一天,如果员工在本月入职,入职日期要做为统计的开始日期");

        Integer year = getMonthDTO.getYear();
        Integer month = getMonthDTO.getMonth();
        DateTime monthStart = DateUtil.parse(StrUtil.format("{}-{}-01", 
                year, month < 10 ? "0" + month : month)); // 该月第一天
        DateTime monthEnd = DateUtil.endOfMonth(monthStart); // 该月最后一天

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
        DateTime hiredate = DateUtil.parse(user.getHiredate());
        if (monthStart.isBefore(DateUtil.beginOfMonth(hiredate))) {
            log.debug("该月第一天在入职月份的第一天之前,无签到数据");
            throw new ServerProblem("无签到数据");
        }
        if (monthStart.isBefore(hiredate)) {
            log.debug("该月第一天在入职日期之前,说明员工在该月入职,入职日期要做为统计的开始时间");
            monthStart = hiredate;
        }
        return getPeriod(userId, monthStart, monthEnd);
    }

    @Override
    public int getDays(Long userId) {
        return checkinMapper.selectDays(userId);
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

    private List<CheckinDTO> getPeriod(Long userId, DateTime startDate, DateTime endDate) {
        log.debug("统计给定日期范围的签到数据");
        log.debug("该日为工作日,且该日为当前日期或在当前日期之前");
        log.debug("查询该日的签到记录,没有则记为缺勤");
        log.debug("该日是今日,且当前统计时间在考勤结束时间之前(也即今日的考勤还没有结束),且没有签到记录 => 签到状态不应该记为缺勤");
        
        List<Checkin> checkins = checkinMapper.selectByDate(userId, startDate, endDate);
        List<String> workdays = workdayMapper.selectByDate(startDate, endDate);
        List<String> holidays = holidayMapper.selectByDate(startDate, endDate);

        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH); // 以天做为分割单位
        log.debug("Iterable转为Stream流");
        return StreamSupport.stream(range.spliterator(), false).map(day -> {
            String date = day.toString("yyyy-MM-dd");
            String status = "";
            String type = Constants.WORKDAY;
            if (day.isWeekend()) {
                type = Constants.HOLIDAY;
            }
            if (workdays != null && workdays.contains(date)) {
                type = Constants.WORKDAY;
            }
            if (holidays != null && holidays.contains(date)) {
                type = Constants.HOLIDAY;
            }

            if (type.equals(Constants.WORKDAY) && day.isBeforeOrEquals(DateUtil.date())) {
                status = Enums.CheckinStatus.ABSENT.getDesc();
                Checkin dayCheckin = checkins.stream().filter(item -> item.getDate().equals(date)).findAny()
                        .orElse(null);
                if (dayCheckin != null) {
                    status = Enums.CheckinStatus.code2Desc(dayCheckin.getStatus());
                }

                DateTime attendanceEndTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceEndTime);
                if (date.equals(DateUtil.today()) && DateUtil.date().isBefore(attendanceEndTime) && dayCheckin == null) {
                    status = "";
                }
            }
            log.debug("{} {} {} {}", date, day.dayOfWeekEnum().toChinese("周"), type, status);

            CheckinDTO checkinDTO = new CheckinDTO();
            checkinDTO.setStatus(status);
            checkinDTO.setDate(date);
            checkinDTO.setType(type);
            checkinDTO.setDay(day.dayOfWeekEnum().toChinese("周"));
            return checkinDTO;
        }).collect(toList());
    }
}
