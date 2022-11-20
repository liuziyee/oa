package com.dorohedoro.dao;

import cn.hutool.core.bean.BeanUtil;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MessagePushRecordDao {

    private final MongoTemplate mongoTemplate;

    public String insert(MessagePushRecord msgPushRecord) {
        return mongoTemplate.save(msgPushRecord).get_id();
    }

    public List<Map> selectPage(Long userId, long skip, int size) {
        log.debug("查询用户的消息推送记录");
        JSONObject json = new JSONObject();
        json.put("$toObjectId", "$messageId");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.addFields().addField("msgId").withValue(json).build(),
                Aggregation.lookup("message", "msgId", "_id", "messages"),
                Aggregation.match(Criteria.where("receiverId").is(userId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "createTime")),
                Aggregation.skip(skip),
                Aggregation.limit(size)
        );
        List<HashMap> msgPushRecords = mongoTemplate.aggregate(aggregation, "message_push_record", HashMap.class)
                .getMappedResults();
        return msgPushRecords.stream().peek(map -> {
            log.debug("取出该条消息推送记录关联的消息记录,转为Map,合并两个Map");
            Message message = ((List<Message>) map.get("messages")).get(0);
            BeanUtil.beanToMap(message).forEach((k, v) -> {
                log.debug("键_id会重复,这里保留消息推送记录的主键ID");
                map.merge(k, v, (v1, v2) -> v1);
            });

            DateTime createTime = DateUtil.offsetHour((Date) map.get("createTime"), -8);
            map.put("createTime", DateUtil.format(createTime, "yyyy/MM/dd"));
            if (DateUtil.today().equals(createTime.toDateStr())) {
                log.debug("消息是在今日推送的");
                map.put("createTime", DateUtil.format(createTime, "HH:mm"));
            }
        }).collect(toList());
    }
    
    public long selectUnreadCount(Long userId) {
        log.debug("查询用户未读消息数");
        Query query = new Query();
        query.addCriteria(Criteria.where("isRead").is(false).and("receiverId").is(userId));
        return mongoTemplate.count(query, "message_push_record");
    }
    
    public long selectLastCount(Long userId) {
        log.debug("查询用户新接收消息数");
        Query query = Query.query(Criteria.where("isLast").is(true).and("receiverId").is(userId));
        Update update = Update.update("isLast", false);
        return mongoTemplate.updateMulti(query, update, "message_push_record").getModifiedCount();
    }

    public long updateUnreadById(String id) {
        log.debug("消息置为已读");
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = Update.update("isRead", true);
        return mongoTemplate.updateFirst(query, update, "message_push_record").getModifiedCount();
    }
    
    public long deleteById(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, "message_push_record").getDeletedCount();
    }
    
    public long deleteByUserId(Long userId) {
        log.debug("删除用户的全部消息推送记录");
        Query query = Query.query(Criteria.where("receiverId").is(userId));
        return mongoTemplate.remove(query, "message_push_record").getDeletedCount();
    }
}
