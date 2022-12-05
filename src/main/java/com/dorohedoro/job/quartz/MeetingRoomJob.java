package com.dorohedoro.job.quartz;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.dorohedoro.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingRoomJob extends QuartzJobBean {

    private final RedisUtil redisUtil;

    @Override
    protected void executeInternal(JobExecutionContext ctx) {
        Map map = ctx.getJobDetail().getJobDataMap();
        String uuid = map.get("uuid").toString();
        DateTime expire = DateUtil.parse(map.get("expire").toString());
        while (true) {
            long roomId = RandomUtil.randomLong(1L, 4294967295L);
            if (redisUtil.hasKey("roomId:" + roomId)) {
                continue;
            }
            redisUtil.set("roomId:" + roomId, uuid, expire);
            redisUtil.set(uuid, roomId, expire);
            log.debug("会议{}已生成房间ID{}", uuid, roomId);
            break;
        }
    }
}
