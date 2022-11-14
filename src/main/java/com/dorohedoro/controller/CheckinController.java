package com.dorohedoro.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import com.dorohedoro.config.Properties;
import com.dorohedoro.domain.dto.CheckinDTO;
import com.dorohedoro.problem.BizProblem;
import com.dorohedoro.service.ICheckinService;
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

@Slf4j
@Api(tags = "签到模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/checkin")
public class CheckinController {

    private final ICheckinService checkinService;
    private final JwtUtil jwtUtil;
    private final Properties properties;

    @GetMapping("/check")
    @ApiOperation("检查当前时间点是否可以签到")
    public R check(@RequestHeader("Authorization") String accessToken, @RequestParam Long distance) {
        Long userId = Convert.toLong(jwtUtil.get(accessToken, "userid"));
        return R.ok(checkinService.check(userId, distance));
    }

    @PostMapping("/checkin.do")
    @ApiOperation("签到")
    public R checkin(@RequestHeader("Authorization") String accessToken, @RequestParam("photo") MultipartFile file,
                     @RequestBody CheckinDTO checkinDTO) {
        String imgPath = copyCheckinImg(file);
        checkinDTO.setUserId(Convert.toLong(jwtUtil.get(accessToken, "userid")));
        checkinDTO.setImgPath(imgPath);
        checkinService.checkin(checkinDTO);
        FileUtil.del(imgPath);
        return R.ok("已签到");
    }
    
    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file, @RequestHeader("Authorization") String accessToken) {
        String imgPath = copyCheckinImg(file);
        checkinService.createFaceModel(Convert.toLong(jwtUtil.get(accessToken, "userid")), imgPath);
        FileUtil.del(imgPath);
        return R.ok("已创建人脸模型");
    }

    private String copyCheckinImg(MultipartFile file) {
        if (file == null) {
            throw new BizProblem("未上传文件");
        }
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            throw new BizProblem("必须上传JPG格式图片");
        }

        try {
            String imgPath = properties.getImgDir() + "/" + fileName;
            file.transferTo(Paths.get(imgPath));
            log.debug("签到照片存储路径: {}", imgPath);
            return imgPath;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BizProblem("签到照片保存错误");
        }
    }
}