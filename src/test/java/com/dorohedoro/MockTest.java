package com.dorohedoro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.dorohedoro.domain.Meeting;
import com.dorohedoro.job.MessageJob;
import com.dorohedoro.mongo.entity.Message;
import com.dorohedoro.mongo.entity.MessagePushRecord;
import com.dorohedoro.service.IMeetingService;
import com.dorohedoro.service.IMessageService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MockTest {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private IMeetingService meetingService;
    @Autowired
    private MessageJob messageJob;
    @Autowired
    private Channel channel;

    @Test
    public void mockMsgPushRecords() {
        IntStream.range(0, 100).forEach(o -> {
            Message message = new Message();
            message.setSenderId(0L);
            message.setSenderName("通知");
            message.setCreateTime(DateUtil.date());
            message.setMsg("No" + o);
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
    public void mockMeetings() {
        IntStream.range(0, 100).forEach(o -> {
            Meeting meeting = new Meeting();
            meeting.setUuid(IdUtil.simpleUUID());
            meeting.setTitle("线上研讨会No" + o);
            meeting.setCreatorId(1L);
            DateTime day = RandomUtil.randomDay(-5, 5);
            meeting.setDate(day.toDateStr());
            meeting.setStart(day.toString("HH:mm"));
            meeting.setEnd(day.offset(DateField.HOUR, RandomUtil.randomInt()).toString("HH:mm"));
            meeting.setType(1);
            meeting.setMembers("[1]");
            meeting.setDesc("线上研讨会No" + o);
            meeting.setInstanceId(IdUtil.simpleUUID());
            meeting.setStatus(3);
            meetingService.createMeeting(meeting);
        });
    }

    @Test
    @SneakyThrows
    public void mockPolling() {
        // 这里模拟小程序轮询刷新消息推送记录接口,第一次轮询,甲拉取到一条消息,第二次轮询,乙也拉取到了甲还没有确认的同一条消息
        Message message = new Message();
        message.setSenderId(0L);
        message.setSenderName("通知");
        message.setCreateTime(DateUtil.date());
        message.setMsg("测试消息");
        messageJob.send("1", message);
        
        ThreadUtil.execAsync(() -> messageJob.receive("1"));
        TimeUnit.SECONDS.sleep(5);
        ThreadUtil.execAsync(() -> {
            while (true) {
                log.info("{}", channel.basicGet("1", false));
                TimeUnit.SECONDS.sleep(1);
            }
        });
        TimeUnit.MINUTES.sleep(5);
    }
}
