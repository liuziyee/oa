package com.dorohedoro.job;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.dorohedoro.mongo.entity.Message;
import com.dorohedoro.mongo.entity.MessagePushRecord;
import com.dorohedoro.service.IMessageService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageJob {

    private final IMessageService messageService;
    private final Channel channel;

    @Async
    public void send(String topic, Message message) {
        log.info("创建推送消息,投递MQ消息,该MQ消息用于创建消息推送记录");
        try {
            channel.queueDeclare(topic, true, false, false, null); // 声明队列,该队列会绑定到默认交换机
            String msgId = messageService.createMessage(message);
            Map<String, Object> map = Map.of("msgId", msgId, "uuid", IdUtil.simpleUUID());
            AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                    .headers(map).build();
            channel.basicPublish("", topic, props, message.getMsg().getBytes()); // 投递消息到默认交换机
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async
    public void receive(String topic) {
        log.info("拉取MQ消息,创建消息推送记录");
        try {
            channel.queueDeclare(topic, true, false, false, null);
            while (true) {
                GetResponse response = channel.basicGet(topic, false); // 拉取消息
                if (response != null) {
                    Map<String, Object> headers = response.getProps().getHeaders();
                    MessagePushRecord msgPushRecord = new MessagePushRecord();
                    msgPushRecord.setMessageId(headers.get("msgId").toString());
                    msgPushRecord.setUuid(headers.get("uuid").toString());
                    msgPushRecord.setReceiverId(Convert.toLong(topic));
                    msgPushRecord.setIsRead(false);
                    msgPushRecord.setIsLast(true);
                    messageService.createMsgPushRecord(msgPushRecord);
                    //TimeUnit.SECONDS.sleep(25);
                    channel.basicAck(response.getEnvelope().getDeliveryTag(), false); // 手动确认
                    continue;
                }
                break;
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
    
    @Async
    public void deleteQueue(String topic) {
        try {
            channel.queueDelete(topic);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
}
