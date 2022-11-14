package com.dorohedoro.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.config.Constants;
import com.dorohedoro.config.Properties;
import com.dorohedoro.domain.Checkin;
import com.dorohedoro.domain.City;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.CheckinDTO;
import com.dorohedoro.mapper.*;
import com.dorohedoro.problem.BizProblem;
import com.dorohedoro.service.ICheckinService;
import com.dorohedoro.task.MailTask;
import com.dorohedoro.util.Enums;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.SimpleMailMessage;
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
    private final UserMapper userMapper;
    private final CityMapper cityMapper;
    private final Properties properties;
    private final MailTask mailTask;

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
        log.debug("确定签到状态(正常或迟到),旷工不会生成签到记录");
        log.debug("上传签到照片和人脸模型");
        log.debug("匹配 => 查询疫情风险等级(高风险 => 发送告警邮件),生成签到记录");
        
        DateTime checkinTime = DateUtil.date(); // 签到时间
        DateTime attendanceTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceTime);
        DateTime attendanceEndTime = DateUtil.parse(DateUtil.today() + " " + Constants.attendanceEndTime);
        
        int status = Enums.CheckinStatus.NORMAL.getCode();
        if (checkinTime.compareTo(attendanceTime) > 0 && checkinTime.compareTo(attendanceEndTime) <= 0) {
            status = Enums.CheckinStatus.ABSENT.getCode();
        }
        
        Long userId = checkinDTO.getUserId();
        String faceModel = faceModelMapper.selectByUserId(userId).orElseThrow(() -> new BizProblem("未创建人脸模型"));
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
    
    private void uploadImgAndFaceModel(String imgPath, String faceModel) {
        HttpResponse response = HttpUtil.createPost(properties.getFace().getCheckin_url())
                .form("photo", FileUtil.file(imgPath), "targetModel", faceModel)
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

        log.debug("响应为True,签到照片和人脸模型匹配");
    }
    
    @SneakyThrows
    private Integer getRisk(String city, String district) {
        String code = cityMapper.selectOne(Wrappers.<City>lambdaQuery().eq(City::getCity, city)).getCode();
        String url = StrUtil.format("http://m.{}.bendibao.com/news/yqdengji/?qu={}", code, district);
        Document document = Jsoup.connect(url).get();
        Element element = document.getElementsByClass("cls18").get(0);
        int risk = Enums.Risk.desc2Code(element.text());
        return risk;
    }
}
