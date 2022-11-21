package com.dorohedoro.service.impl;

import com.dorohedoro.mongo.dao.MessageDao;
import com.dorohedoro.mongo.dao.MessagePushRecordDao;
import com.dorohedoro.mongo.entity.Message;
import com.dorohedoro.mongo.entity.MessagePushRecord;
import com.dorohedoro.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final MessageDao messageDao;
    private final MessagePushRecordDao msgPushRecordDao;
    
    @Override
    public String createMessage(Message message) {
        return messageDao.insert(message);
    }

    @Override
    public Map getMessage(String msgId) {
        return messageDao.selectById(msgId);
    }

    @Override
    public List<Map> getMsgPushRecords(Long userId, long skip, int size) {
        return msgPushRecordDao.selectPage(userId, skip, size);
    }

    @Override
    public String createMsgPushRecord(MessagePushRecord msgPushRecord) {
        return msgPushRecordDao.insert(msgPushRecord);
    }

    @Override
    public long getUnreadMsgCount(Long userId) {
        return msgPushRecordDao.selectUnreadCount(userId);
    }

    @Override
    public long getLastMsgCount(Long userId) {
        return msgPushRecordDao.selectLastCount(userId);
    }

    @Override
    public long messageRead(String msgPushRecordId) {
        return msgPushRecordDao.updateIsReadById(msgPushRecordId);
    }

    @Override
    public long deleteMsgPushRecord(String msgPushRecordId) {
        return msgPushRecordDao.deleteById(msgPushRecordId);
    }

    @Override
    public long deleteAllMsgPushRecords(Long userId) {
        return msgPushRecordDao.deleteByUserId(userId);
    }
}
