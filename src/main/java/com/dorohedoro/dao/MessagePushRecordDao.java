package com.dorohedoro.dao;

import com.dorohedoro.domain.MessagePushRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MessagePushRecordDao {

    private final MongoTemplate mongoTemplate;

    public String insert(MessagePushRecord msgPushRecord) {
        return mongoTemplate.save(msgPushRecord).get_id();
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
