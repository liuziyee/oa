package com.dorohedoro.mapper;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.dorohedoro.domain.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageDao {

    private final MongoTemplate mongoTemplate;

    public String insert(Message message) {
        DateTime createTime = DateUtil.offsetHour(message.getCreateTime(), 8);
        message.setCreateTime(createTime);
        return mongoTemplate.insert(message).get_id();
    }
}
