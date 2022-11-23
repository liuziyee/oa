package com.dorohedoro;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.dorohedoro.job.MessageJob;
import com.dorohedoro.mongo.entity.Message;
import com.dorohedoro.mongo.entity.MessagePushRecord;
import com.dorohedoro.service.IMessageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MsgTest {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private MessageJob messageJob;

    @Test
    public void createMsgPushRecords() {
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

    @Test
    @SneakyThrows
    public void mockPolling() {
        // 这里模拟小程序轮询刷新消息推送记录接口,第一次轮询,甲消费者拉取到一条消息,第二次轮询,乙消费者也拉取到了甲还没有确认的同一条消息
        Message message = new Message();
        message.setSenderId(0L);
        message.setSenderName("通知");
        message.setCreateTime(DateUtil.date());
        message.setMsg("测试消息");
        messageJob.send("1", message);
        
        ThreadUtil.execAsync(() -> messageJob.receive("1"));
        TimeUnit.SECONDS.sleep(5);
        ThreadUtil.execAsync(() -> messageJob.receive("1"));
        TimeUnit.MINUTES.sleep(5);
    }
}
