package com.dorohedoro.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document("message")
public class Message implements Serializable {
    
    @Id
    private String _id;
    
    @Indexed
    private Long senderId;
    
    private String senderName;

    private String senderAvatarUrl = "https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132";
    
    private String msg;
    
    @Indexed
    private Date createTime; // 推送时间
}
