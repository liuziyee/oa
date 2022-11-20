package com.dorohedoro.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.dorohedoro.domain.Message;
import com.dorohedoro.domain.MessagePushRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

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
    
    public List<Map> selectPage(Long userId, long skip, int size) {
        log.debug("分页查询用户的消息推送记录");
        JSONObject json = new JSONObject();
        json.put("$toString", "$_id");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.addFields().addField("id").withValue(json).build(),
                Aggregation.lookup("message_push_record", "id", "messageId", "push_records"),
                Aggregation.match(Criteria.where("push_records.receiverId").is(userId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "sendTime")),
                Aggregation.skip(skip),
                Aggregation.limit(size)
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "message", HashMap.class);
        List<HashMap> pushRecords = results.getMappedResults();
        return pushRecords.stream().peek(map -> {
            MessagePushRecord pushRecord = ((List<MessagePushRecord>) map.get("push_records")).get(0); // 用户某一消息的推送记录
            map.put("isRead", pushRecord.getIsRead());
            map.put("isLast", pushRecord.getIsLast());
            map.put("_id", null);
            map.put("push_records", null);
            
            DateTime createTime = DateUtil.offsetHour((Date) map.get("createTime"), -8);
            map.put("createTime", DateUtil.format(createTime, "yyyy/MM/dd"));
            if (DateUtil.today().equals(createTime.toDateStr())) {
                // 消息是在今日推送的
                map.put("createTime", DateUtil.format(createTime, "HH:mm"));
            }
        }).collect(toList());
    }
    
    public Map selectById(String id) {
        Map map = mongoTemplate.findById(id, HashMap.class, "message");
        DateTime createTime = DateUtil.offsetHour((Date) map.get("createTime"), -8);
        map.put("createTime", DateUtil.format(createTime, "yyyy-MM-dd HH:mm"));
        return map;
    }
}
