package com.dorohedoro.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document("message_push_record")
public class MessagePushRecord implements Serializable {
    
    @Id
    private String _id;
    
    @Indexed
    private String message_id;
    
    @Indexed
    private String receiverId;
    
    @Indexed
    private Boolean isRead; // 是否已读
    
    @Indexed
    private Boolean isLast; // 是否为新接收的消息
}
