package com.dorohedoro.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.dorohedoro.domain.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MessageDao {

    private final MongoTemplate mongoTemplate;

    public String insert(Message message) {
        DateTime createTime = DateUtil.offsetHour(message.getCreateTime(), 8);
        message.setCreateTime(createTime);
        return mongoTemplate.insert(message).get_id();
    }
    
    public Map selectById(String id) {
        Map map = mongoTemplate.findById(id, HashMap.class, "message");
        DateTime createTime = DateUtil.offsetHour((Date) map.get("createTime"), -8);
        map.put("createTime", DateUtil.format(createTime, "yyyy-MM-dd HH:mm"));
        return map;
    }
}
