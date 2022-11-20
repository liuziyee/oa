package com.dorohedoro.service;

import com.dorohedoro.domain.Message;
import com.dorohedoro.domain.MessagePushRecord;

import java.util.List;
import java.util.Map;

public interface IMessageService {

    String createMessage(Message message);

    List<Map> getMsgPushRecords(Long userId, long skip, int size);
    
    Map getMessage(String msgId);

    String createMsgPushRecord(MessagePushRecord messagePushRecord);

    long getUnreadMsgCount(Long userId);

    long getLastMsgCount(Long userId);

    long messageRead(String msgPushRecordId);

    long deleteMsgPushRecord(String msgPushRecordId);

    long deleteAllMsgPushRecords(Long userId);
}
