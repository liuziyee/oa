package com.dorohedoro.service.impl;

import com.dorohedoro.dao.MessageDao;
import com.dorohedoro.dao.MessagePushRecordDao;
import com.dorohedoro.domain.Message;
import com.dorohedoro.domain.MessagePushRecord;
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
    public List<Map> getMsgPushRecords(Long userId, long skip, int size) {
        return messageDao.selectPage(userId, skip, size);
    }

    @Override
    public Map getMessage(String msgId) {
        return messageDao.selectById(msgId);
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
        return msgPushRecordDao.updateUnreadById(msgPushRecordId);
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
