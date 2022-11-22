:beers:  
[ubuntu安装mongodb参考文档](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-ubuntu)  
[problem-spring-web参考文档](https://github.com/zalando/problem-spring-web/tree/main/problem-spring-web)  
[swagger访问地址](http://localhost:30000/oa/swagger-ui.html)  
[knife4j访问地址](http://localhost:30000/oa/doc.html)
> ##### 关于如何刷新访问令牌
>> ###### 把生成的访问令牌缓存到Redis(下面叫做缓存令牌),缓存令牌的过期时间设为访问令牌的一倍,下面以访问令牌过期时间5天为例
>> ###### 如果访问令牌过期,缓存令牌没有过期,说明访问令牌过期后的间隔时间还没有超过5天,要生成新的访问令牌(即续期)并缓存到Redis
>> ###### 如果访问令牌过期,缓存令牌也过期了,说明访问令牌过期后的间隔时间超过了5天,要重新登录
> ##### 关于MySQL和Java日期数据类型的映射
>> ###### MySQL的datetime映射为Java的Date(下同)
>> ###### date映射为String,不用Java的Date是因为Date同时包含日期和时间,而MySQL的date只包含日期,所以这里用日期字符串来替代Date
>> ###### JDK8+: datetime,timestamp映射为LocalDateTime,date映射为LocalDate
> ##### 踩坑
>> ###### MySQL的时区要设置为Asia/Shanghai
>> ###### 签到接口会接收签到照片和位置信息(JSON),用@RequestParam接收文件,不要用@RequestBody接收JSON
>> ###### Stream的map,peek等中间操作为惰性操作,Optional的map不是惰性操作
>> ###### 接口用到了@RequestBody,会执行XssHttpServletRequestWrapper的getInputStream()
`keytool -genkeypair -alias [keypair] -keyalg RSA -keystore [keypair.keystore] -keypass [dorohedoro] -storepass [dorohedoro]`  
`docker load < /usr/local/face.tar.gz`  
`docker run -d -it -p 3000:3000 -v /usr/local/python/demo:/usr/local/demo --name face-recognition face`  
`docker pull rabbitmq:3.11.3-management`  
`docker run -d -it -p 15672:15672 -p 5672:5672 --name rabbit rabbitmq:3.11.3-management`  
`docker exec -it face-recognition bash`  
`nohup python3 -c "from app import app;" > log.out 2>&1 &`  
`用到的mysql函数:if,ifnull,json_contains,date_format,current_date,case when then,cast`  
`A.isBeforeOrEquals(B)等价于A.compareTo(B)<=0`  
`mongo message:消息集合 message_push_record:消息推送记录集合`  
`spring.data.mongodb.password要加上单引号`