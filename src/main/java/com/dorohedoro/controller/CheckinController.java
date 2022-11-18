package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import com.dorohedoro.config.Constants;
import com.dorohedoro.config.Properties;
import com.dorohedoro.domain.User;
import com.dorohedoro.domain.dto.CheckinDTO;
import com.dorohedoro.problem.ServerProblem;
import com.dorohedoro.service.ICheckinService;
import com.dorohedoro.service.IUserService;
import com.dorohedoro.util.JwtUtil;
import com.dorohedoro.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "签到模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/checkin")
public class CheckinController {

    private final ICheckinService checkinService;
    private final JwtUtil jwtUtil;
    private final Properties properties;
    private final IUserService userService;

    @GetMapping("/check")
    @ApiOperation("检查当前时间点是否可以签到")
    public R check(@RequestHeader("Authorization") String accessToken, @RequestParam Long distance) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        return R.ok(null, checkinService.check(userId, distance));
    }

    @PostMapping("/checkin.do")
    @ApiOperation("签到")
    public R checkin(@RequestHeader("Authorization") String accessToken, @RequestParam("photo") MultipartFile file, 
                     CheckinDTO checkinDTO) {
        String imgPath = null;
        try {
            imgPath = copyCheckinImg(file);
            checkinDTO.setUserId(Convert.toLong(jwtUtil.get(accessToken, "userid")));
            checkinDTO.setImgPath(imgPath);
            checkinService.checkin(checkinDTO);
            return R.ok(null, "已签到");
        } finally {
            if (imgPath != null) {
                FileUtil.del(imgPath);
            }
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file, @RequestHeader("Authorization") String accessToken) {
        String imgPath = null;
        try {
            imgPath = copyCheckinImg(file);
            checkinService.createFaceModel(Convert.toLong(jwtUtil.get(accessToken, "userid")), imgPath);
            return R.ok(null, "已创建人脸模型");
        } finally {
            if (imgPath != null) {
                FileUtil.del(imgPath);
            }
        }
    }

    @GetMapping("/todayAndWeek")
    @ApiOperation("查询今日和本周的签到数据")
    public R getTodayAndWeek(@RequestHeader("Authorization") String accessToken) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        Map<String, Object> map = new HashMap<>();

        User user = userService.getDetail(userId).orElse(null);
        CheckinDTO today = checkinService.getToday(userId);
        List<CheckinDTO> week = checkinService.getWeek(userId);
        int days = checkinService.getDays(userId);

        map.put("user", user);
        map.put("today", today);
        map.put("week", week);
        map.put("days", days);
        map.put("attendanceTime", Constants.attendanceTime);
        map.put("closingTime", Constants.closingTime);
        return R.ok(map, null);
    }

    private String copyCheckinImg(MultipartFile file) {
        if (file == null) {
            throw new ServerProblem("未上传文件");
        }
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            throw new ServerProblem("必须上传JPG格式图片");
        }

        try {
            String imgPath = properties.getImgDir() + "/" + fileName;
            file.transferTo(Paths.get(imgPath));
            log.debug("签到照片存储路径: {}", imgPath);
            return imgPath;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServerProblem("签到照片保存错误");
        }
    }
}