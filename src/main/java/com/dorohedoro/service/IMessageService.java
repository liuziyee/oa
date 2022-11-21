package com.dorohedoro.service;

import com.dorohedoro.mongo.entity.Message;
import com.dorohedoro.mongo.entity.MessagePushRecord;

import java.util.List;
import java.util.Map;

public interface IMessageService {

    String createMessage(Message message);

    Map getMessage(String msgId);
    
    List<Map> getMsgPushRecords(Long userId, long skip, int size);
    
    String createMsgPushRecord(MessagePushRecord messagePushRecord);

    long getUnreadMsgCount(Long userId);

    long getLastMsgCount(Long userId);

    long messageRead(String msgPushRecordId);

    long deleteMsgPushRecord(String msgPushRecordId);

    long deleteAllMsgPushRecords(Long userId);
}
