package com.dorohedoro;

import cn.hutool.core.date.DateUtil;
import com.dorohedoro.mongo.entity.Message;
import com.dorohedoro.mongo.entity.MessagePushRecord;
import com.dorohedoro.service.IMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.IntStream;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MsgTest {

    @Autowired
    private IMessageService messageService;
    
    @Test
    public void generateMsgPushRecords() {
        IntStream.range(0, 100).forEach(no -> {
            Message message = new Message();
            message.setSenderId(0L);
            message.setSenderName("通知");
            message.setCreateTime(DateUtil.date());
            message.setMsg("NO " + no);
            String msgId = messageService.createMessage(message);

            MessagePushRecord msgPushRecord = new MessagePushRecord();
            msgPushRecord.setMessageId(msgId);
            msgPushRecord.setReceiverId(1L);
            msgPushRecord.setIsRead(false);
            msgPushRecord.setIsLast(true);
            messageService.createMsgPushRecord(msgPushRecord);
        });
    }
}
